package com.fit.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;

public class WechatVerifyUtil {


    public static String genRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 16; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    /**
     *
     * @param aesKey
     * @param encryptStr
     * @return
     */
    public static String encrypt(String aesKey, String encryptStr) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(StandardCharsets.UTF_8), 0, 16);
            cipher.init(1, keySpec, iv);
            byte[] encrypted = cipher.doFinal(encryptStr.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param aesKey
     * @param decryptStr
     * @return
     */
    public static String decrypt(String aesKey, String decryptStr) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey.getBytes(StandardCharsets.UTF_8), 0, 16));
            cipher.init(2, keySpec, iv);
            byte[] encrypted = Base64.getDecoder().decode(decryptStr);
            byte[] original = cipher.doFinal(encrypted);
            return new String(original);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 十六进制字符串转换为byte[]
     *
     * @param hexStr 需要转换为byte[]的字符串
     * @return 转换后的byte[]
     */
    public static byte[] hexStr2Bytes(String hexStr) {
        hexStr = hexStr.trim().replace(" ", "").toUpperCase(Locale.US);
        int len = hexStr.length();
        byte[] ret = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ret[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return ret;
    }

    /**
     * byte[]转换为十六进制字符串
     *
     * @param bytes 需要转换为字符串的byte[]
     * @return 转换后的十六进制字符串
     */
    public static String byte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString().toUpperCase(Locale.US);
    }
}