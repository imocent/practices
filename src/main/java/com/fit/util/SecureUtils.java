package com.fit.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

/**
 * 混合加密工具类
 * RSA + AES 混合加密方案，无第三方依赖
 * 适用于前后端分离场景下的敏感数据传输
 */
public class SecureUtils {
    // ==================== 算法常量 ====================
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String SHA_ALGORITHM = "SHA-256";
    private static final int AES_KEY_SIZE = 128;  // AES密钥长度，可选128/192/256
    private static final int DEFAULT_RSA_KEY_SIZE = 2048; // RSA密钥长度

    /**
     * 生成公钥和私钥
     */
    public static HashMap<String, Object> getKeys() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPair keyPair = generateRsaKeyPair(2048);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    // ==================== AES 相关方法 ====================

    /**
     * 生成AES密钥（Base64格式）
     */
    public static String generateAesKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(AES_KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成AES密钥和随机IV（初始化向量）
     *
     * @return 包含key和iv的数组，索引0为key(Base64)，索引1为iv(Base64)
     */
    public static String[] generateAesKeyWithIv() throws Exception {
        String[] result = new String[2];
        // 生成密钥
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(AES_KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        result[0] = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        // 生成随机IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        result[1] = Base64.getEncoder().encodeToString(iv);
        return result;
    }

    /**
     * AES加密
     *
     * @param plainText    明文
     * @param aesKeyBase64 Base64格式的AES密钥
     * @param ivBase64     Base64格式的初始化向量（16字节）
     * @return Base64格式的密文
     */
    public static String aesEncrypt(String plainText, String aesKeyBase64, String ivBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * AES解密
     *
     * @param cipherText   Base64格式的密文
     * @param aesKeyBase64 Base64格式的AES密钥
     * @param ivBase64     Base64格式的初始化向量（16字节）
     * @return 明文字符串
     */
    public static String aesDecrypt(String cipherText, String aesKeyBase64, String ivBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(cipherBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // ==================== RSA 相关方法 ====================

    /**
     * RSA密钥对生成（使用默认密钥长度）
     */
    public static KeyPair generateRsaKeyPair() throws Exception {
        return generateRsaKeyPair(DEFAULT_RSA_KEY_SIZE);
    }

    /**
     * RSA密钥对生成（指定密钥长度）
     *
     * @param keySize 密钥长度，推荐2048或4096
     */
    public static KeyPair generateRsaKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取Base64格式的公钥字符串
     */
    public static String getPublicKeyBase64(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取Base64格式的私钥字符串
     */
    public static String getPrivateKeyBase64(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 从Base64字符串加载公钥
     */
    public static PublicKey loadPublicKey(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从Base64字符串加载私钥
     */
    public static PrivateKey loadPrivateKey(String privateKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * RSA加密
     *
     * @param plainText       明文
     * @param publicKeyBase64 Base64格式的公钥
     * @return Base64格式的密文
     */
    public static String rsaEncrypt(String plainText, String publicKeyBase64) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyBase64);
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * RSA解密
     *
     * @param cipherText       Base64格式的密文
     * @param privateKeyBase64 Base64格式的私钥
     * @return 明文字符串
     */
    public static String rsaDecrypt(String cipherText, String privateKeyBase64) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(cipherBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // ==================== 混合加密方法 ====================

    /**
     * 混合加密：使用随机AES密钥加密数据，然后使用RSA公钥加密AES密钥
     *
     * @param data               要加密的原始数据
     * @param rsaPublicKeyBase64 RSA公钥（Base64格式）
     * @return 加密结果对象，包含encryptedData（AES加密后的数据）和encryptedKey（RSA加密后的AES密钥）
     */
    public static HybridEncryptResult hybridEncrypt(String data, String rsaPublicKeyBase64) throws Exception {
        // 1. 生成随机AES密钥和IV
        String[] keyWithIv = generateAesKeyWithIv();
        String aesKeyBase64 = keyWithIv[0];
        String ivBase64 = keyWithIv[1];
        // 2. 使用AES加密数据
        String encryptedData = aesEncrypt(data, aesKeyBase64, ivBase64);
        // 3. 使用RSA公钥加密AES密钥和IV的组合（格式：key|iv）
        String keyAndIv = aesKeyBase64 + "|" + ivBase64;
        String encryptedKey = rsaEncrypt(keyAndIv, rsaPublicKeyBase64);
        return new HybridEncryptResult(encryptedData, encryptedKey);
    }

    /**
     * 混合解密：使用RSA私钥解密出AES密钥，再用AES密钥解密数据
     *
     * @param encryptedData       AES加密后的数据（Base64）
     * @param encryptedKey        RSA加密后的AES密钥和IV组合（Base64）
     * @param rsaPrivateKeyBase64 RSA私钥（Base64格式）
     * @return 解密后的原始数据
     */
    public static String hybridDecrypt(String encryptedData, String encryptedKey, String rsaPrivateKeyBase64) throws Exception {
        // 1. 使用RSA私钥解密获取AES密钥和IV
        String keyAndIv = rsaDecrypt(encryptedKey, rsaPrivateKeyBase64);
        String[] parts = keyAndIv.split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted key format");
        }
        String aesKeyBase64 = parts[0];
        String ivBase64 = parts[1];
        // 2. 使用AES解密数据
        return aesDecrypt(encryptedData, aesKeyBase64, ivBase64);
    }

    /**
     * 混合加密结果封装类
     */
    public static class HybridEncryptResult {
        private final String encryptedData;  // AES加密后的数据
        private final String encryptedKey;   // RSA加密后的AES密钥+IV

        public HybridEncryptResult(String encryptedData, String encryptedKey) {
            this.encryptedData = encryptedData;
            this.encryptedKey = encryptedKey;
        }

        public String getEncryptedData() {
            return encryptedData;
        }

        public String getEncryptedKey() {
            return encryptedKey;
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 计算字符串的SHA-256哈希值（Base64格式）
     */
    public static String sha256Base64(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(SHA_ALGORITHM);
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 计算字符串的SHA-256哈希值（十六进制格式）
     */
    public static String sha256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(SHA_ALGORITHM);
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // ==================== 主方法测试 ====================
    public static void main(String[] args) {
        System.out.println("========== 混合加密工具类测试开始 ==========\n");
        try {
            // 测试1: AES 加密解密
            testAesEncryption();
            // 测试2: RSA 加密解密
            testRsaEncryption();
            // 测试3: 混合加密解密
            testHybridEncryption();
            // 测试4: SHA256 哈希
            testSha256();
            // 测试5: 边界条件测试
            testEdgeCases();
            System.out.println("\n========== 所有测试通过 ==========");
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试AES加密解密
     */
    private static void testAesEncryption() throws Exception {
        System.out.println("【测试1】AES加密解密测试");
        System.out.println("----------------------------------------");
        // 生成AES密钥和IV
        String[] keyWithIv = generateAesKeyWithIv();
        String aesKey = keyWithIv[0];
        String iv = keyWithIv[1];
        System.out.println("生成的AES密钥: " + aesKey);
        System.out.println("生成的IV向量: " + iv);
        // 测试数据
        String[] testData = {"Hello World", "这是一个中文测试", "混合内容: English + 中文 + 123!@#", "{\"username\":\"admin\",\"password\":\"123456\"}"};
        for (String original : testData) {
            // 加密
            String encrypted = aesEncrypt(original, aesKey, iv);
            // 解密
            String decrypted = aesDecrypt(encrypted, aesKey, iv);
            System.out.println("\n原始数据: " + original);
            System.out.println("加密后: " + encrypted);
            System.out.println("解密后: " + decrypted);
            System.out.println("验证结果: " + (original.equals(decrypted) ? "✓ 通过" : "✗ 失败"));
        }
        System.out.println();
    }

    /**
     * 测试RSA加密解密
     */
    private static void testRsaEncryption() throws Exception {
        System.out.println("【测试2】RSA加密解密测试");
        System.out.println("----------------------------------------");
        // 生成RSA密钥对
        KeyPair keyPair = generateRsaKeyPair();
        String publicKey = getPublicKeyBase64(keyPair);
        String privateKey = getPrivateKeyBase64(keyPair);
        int keySize = ((RSAPublicKey) keyPair.getPublic()).getModulus().bitLength();
        System.out.println("RSA公钥 (前100字符): " + publicKey.substring(0, Math.min(100, publicKey.length())) + "...");
        System.out.println("RSA私钥 (前100字符): " + privateKey.substring(0, Math.min(100, privateKey.length())) + "...");
        System.out.println("密钥长度: " + keySize + "位");
        // 测试数据（RSA加密有长度限制，2048位最多加密245字节）
        String[] testData = {"Short text", "1234567890", "{\"key\":\"value\"}"};
        for (String original : testData) {
            String encrypted = rsaEncrypt(original, publicKey);// 加密
            String decrypted = rsaDecrypt(encrypted, privateKey);// 解密
            System.out.println("\n原始数据: " + original);
            System.out.println("加密后: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
            System.out.println("解密后: " + decrypted);
            System.out.println("验证结果: " + (original.equals(decrypted) ? "✓ 通过" : "✗ 失败"));
        }
        System.out.println();
    }

    /**
     * 测试混合加密解密
     */
    private static void testHybridEncryption() throws Exception {
        System.out.println("【测试3】RSA+AES混合加密解密测试");
        System.out.println("----------------------------------------");
        // 模拟服务端生成RSA密钥对
        KeyPair serverKeyPair = generateRsaKeyPair();
        String serverPublicKey = getPublicKeyBase64(serverKeyPair);
        String serverPrivateKey = getPrivateKeyBase64(serverKeyPair);
        System.out.println("服务端公钥已生成");
        System.out.println("服务端私钥已生成");
        // 测试大文本数据（混合加密可以处理任意大小数据）
        String[] testData = {"普通文本数据", "这是一个包含中文的长文本数据，用于测试混合加密方案的实际效果。", "{\"username\":\"张三\",\"password\":\"Admin@123\",\"timestamp\":1640995200000,\"data\":\"这是一个很长的JSON数据，用于模拟实际的业务数据传输场景\"}", generateLargeText(500) // 生成500字符的长文本
        };

        for (String original : testData) {
            System.out.println("\n原始数据长度: " + original.length() + " 字符");
            System.out.println("原始数据: " + (original.length() > 100 ? original.substring(0, 100) + "..." : original));
            // 前端/客户端：使用服务端公钥进行混合加密
            HybridEncryptResult encryptResult = hybridEncrypt(original, serverPublicKey);
            System.out.println("加密后数据长度: " + encryptResult.getEncryptedData().length() + " 字符");
            System.out.println("加密后密钥长度: " + encryptResult.getEncryptedKey().length() + " 字符");
            // 服务端：使用私钥进行混合解密
            String decrypted = hybridDecrypt(encryptResult.getEncryptedData(), encryptResult.getEncryptedKey(), serverPrivateKey);
            System.out.println("解密结果: " + (decrypted.length() > 100 ? decrypted.substring(0, 100) + "..." : decrypted));
            System.out.println("验证结果: " + (original.equals(decrypted) ? "✓ 通过" : "✗ 失败"));
        }
        System.out.println();
    }

    /**
     * 测试SHA256哈希
     */
    private static void testSha256() throws Exception {
        System.out.println("【测试4】SHA256哈希测试");
        System.out.println("----------------------------------------");
        String[] testData = {"Hello World", "密码123", "https://example.com/api/login"};
        for (String data : testData) {
            String hashBase64 = sha256Base64(data);
            String hashHex = sha256Hex(data);
            System.out.println("\n原始数据: " + data);
            System.out.println("SHA256(Base64): " + hashBase64);
            System.out.println("SHA256(Hex): " + hashHex);
            System.out.println("哈希长度: " + hashBase64.length() + " (Base64) / " + hashHex.length() + " (Hex)");
        }
        System.out.println();
    }

    /**
     * 边界条件测试
     */
    private static void testEdgeCases() throws Exception {
        System.out.println("【测试5】边界条件测试");
        System.out.println("----------------------------------------");
        // 生成测试用的密钥
        KeyPair keyPair = generateRsaKeyPair();
        String publicKey = getPublicKeyBase64(keyPair);
        String privateKey = getPrivateKeyBase64(keyPair);
        String[] keyWithIv = generateAesKeyWithIv();
        String aesKey = keyWithIv[0];
        String iv = keyWithIv[1];
        // 测试空字符串
        System.out.println("测试空字符串:");
        String emptyEncrypted = aesEncrypt("", aesKey, iv);
        String emptyDecrypted = aesDecrypt(emptyEncrypted, aesKey, iv);
        System.out.println("  AES空字符串: " + ("".equals(emptyDecrypted) ? "✓ 通过" : "✗ 失败"));
        // 测试特殊字符
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`\n\t\r\\";
        String specialEncrypted = aesEncrypt(specialChars, aesKey, iv);
        String specialDecrypted = aesDecrypt(specialEncrypted, aesKey, iv);
        System.out.println("  特殊字符测试: " + (specialChars.equals(specialDecrypted) ? "✓ 通过" : "✗ 失败"));
        // 测试Unicode字符
        String unicodeText = "🎉 中文 English 123 表情包 😀🌟";
        HybridEncryptResult hybridResult = hybridEncrypt(unicodeText, publicKey);
        String hybridDecrypted = hybridDecrypt(hybridResult.getEncryptedData(), hybridResult.getEncryptedKey(), privateKey);
        System.out.println("  Unicode字符测试: " + (unicodeText.equals(hybridDecrypted) ? "✓ 通过" : "✗ 失败"));
        // 测试重复加密（每次结果应该不同，因为IV随机）
        String sameText = "重复加密测试";
        HybridEncryptResult result1 = hybridEncrypt(sameText, publicKey);
        HybridEncryptResult result2 = hybridEncrypt(sameText, publicKey);
        System.out.println("  重复加密结果不同: " + (!result1.getEncryptedData().equals(result2.getEncryptedData()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    /**
     * 生成长文本用于测试
     */
    private static String generateLargeText(int length) {
        StringBuilder sb = new StringBuilder();
        String base = "这是一段用于测试的长文本数据，包含中英文和数字1234567890。";
        while (sb.length() < length) {
            sb.append(base);
        }
        return sb.substring(0, length);
    }
}