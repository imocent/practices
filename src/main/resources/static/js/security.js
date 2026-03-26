(function (window) {
    'use strict';
    /**
     * 混合加密工具类
     * 使用 jsencrypt 和 CryptoJS 库实现同步加解密
     */
    var SecureUtils = {
        // ==================== 工具方法 ====================
        /**
         * 将字符串转换为Base64
         */
        toBase64: function (str) {
            return btoa(unescape(encodeURIComponent(str)));
        },
        /**
         * 将Base64转换为字符串
         */
        fromBase64: function (base64) {
            return decodeURIComponent(escape(atob(base64)));
        },
        /**
         * 生成随机字符串
         * @param {number} length 长度
         * @returns {string} 随机字符串
         */
        randomString: function (length) {
            var result = '';
            var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            var charactersLength = characters.length;
            for (var i = 0; i < length; i++) {
                result += characters.charAt(Math.floor(Math.random() * charactersLength));
            }
            return result;
        },
        /**
         * 生成随机IV（16字节）
         * @returns {string} Base64格式的IV
         */
        generateRandomIv: function () {
            var iv = '';
            for (var i = 0; i < 16; i++) {
                iv += String.fromCharCode(Math.floor(Math.random() * 256));
            }
            return btoa(iv);
        },
        // ==================== AES 相关方法（使用CryptoJS） ====================
        /**
         * 生成随机AES密钥（128位）
         * @returns {string} Base64格式的AES密钥
         */
        generateAesKey: function () {
            var key = '';
            for (var i = 0; i < 16; i++) {
                key += String.fromCharCode(Math.floor(Math.random() * 256));
            }
            return btoa(key);
        },
        /**
         * 生成AES密钥和IV
         * @returns {Object} 包含key和iv的对象
         */
        generateAesKeyWithIv: function () {
            return {
                key: this.generateAesKey(),
                iv: this.generateRandomIv()
            };
        },
        /**
         * AES加密（CBC模式，PKCS5填充）
         * @param {string} plainText 明文
         * @param {string} aesKeyBase64 Base64格式的AES密钥
         * @param {string} ivBase64 Base64格式的IV
         * @returns {string} Base64格式的密文
         */
        aesEncrypt: function (plainText, aesKeyBase64, ivBase64) {
            var key = CryptoJS.enc.Base64.parse(aesKeyBase64);
            var iv = CryptoJS.enc.Base64.parse(ivBase64);
            var encrypted = CryptoJS.AES.encrypt(plainText, key, {
                iv: iv,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });

            return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
        },

        /**
         * AES解密
         * @param {string} cipherText Base64格式的密文
         * @param {string} aesKeyBase64 Base64格式的AES密钥
         * @param {string} ivBase64 Base64格式的IV
         * @returns {string} 明文字符串
         */
        aesDecrypt: function (cipherText, aesKeyBase64, ivBase64) {
            var key = CryptoJS.enc.Base64.parse(aesKeyBase64);
            var iv = CryptoJS.enc.Base64.parse(ivBase64);
            var decrypted = CryptoJS.AES.decrypt(cipherText, key, {
                iv: iv,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });

            return decrypted.toString(CryptoJS.enc.Utf8);
        },

        // ==================== RSA 相关方法（使用JSEncrypt） ====================
        /**
         * RSA加密
         * @param {string} plainText 明文
         * @param {string} publicKeyBase64 Base64格式的RSA公钥
         * @returns {string} Base64格式的密文
         */
        rsaEncrypt: function (plainText, publicKeyBase64) {
            var encrypt = new JSEncrypt();
            // JSEncrypt需要PEM格式的公钥
            var publicKeyPem = this.formatPublicKey(publicKeyBase64);
            encrypt.setPublicKey(publicKeyPem);
            return encrypt.encrypt(plainText);
        },
        /**
         * RSA解密
         * @param {string} cipherText Base64格式的密文
         * @param {string} privateKeyBase64 Base64格式的RSA私钥
         * @returns {string} 明文字符串
         */
        rsaDecrypt: function (cipherText, privateKeyBase64) {
            var decrypt = new JSEncrypt();
            var privateKeyPem = this.formatPrivateKey(privateKeyBase64);
            decrypt.setPrivateKey(privateKeyPem);
            return decrypt.decrypt(cipherText);
        },
        /**
         * 格式化公钥为PEM格式
         */
        formatPublicKey: function (publicKeyBase64) {
            // 如果已经是PEM格式，直接返回
            if (publicKeyBase64.indexOf('-----BEGIN PUBLIC KEY-----') !== -1) {
                return publicKeyBase64;
            }
            var pem = '-----BEGIN PUBLIC KEY-----\n';
            // 每64个字符换行
            for (var i = 0; i < publicKeyBase64.length; i += 64) {
                pem += publicKeyBase64.substr(i, 64) + '\n';
            }
            pem += '-----END PUBLIC KEY-----';
            return pem;
        },
        /**
         * 格式化私钥为PEM格式
         */
        formatPrivateKey: function (privateKeyBase64) {
            // 如果已经是PEM格式，直接返回
            if (privateKeyBase64.indexOf('-----BEGIN PRIVATE KEY-----') !== -1) {
                return privateKeyBase64;
            }
            var pem = '-----BEGIN PRIVATE KEY-----\n';
            for (var i = 0; i < privateKeyBase64.length; i += 64) {
                pem += privateKeyBase64.substr(i, 64) + '\n';
            }
            pem += '-----END PRIVATE KEY-----';
            return pem;
        },
        // ==================== 混合加密方法 ====================
        /**
         * 混合加密：使用随机AES密钥加密数据，然后使用RSA公钥加密AES密钥
         * @param {string} data 要加密的原始数据
         * @param {string} rsaPublicKeyBase64 RSA公钥（Base64格式）
         * @returns {Object} 加密结果对象，包含encryptedData和encryptedKey
         */
        hybridEncrypt: function (data, rsaPublicKeyBase64) {
            // 1. 生成随机AES密钥和IV
            var aesKeyWithIv = this.generateAesKeyWithIv();
            var aesKey = aesKeyWithIv.key;
            var iv = aesKeyWithIv.iv;
            // 2. 使用AES加密数据
            var encryptedData = this.aesEncrypt(data, aesKey, iv);
            // 3. 组合AES密钥和IV（格式：key|iv）
            var keyAndIv = aesKey + '|' + iv;
            // 4. 使用RSA公钥加密AES密钥和IV
            var encryptedKey = this.rsaEncrypt(keyAndIv, rsaPublicKeyBase64);
            return {encryptedData: encryptedData, encryptedKey: encryptedKey};
        },
        /**
         * 混合解密：使用RSA私钥解密出AES密钥，再用AES密钥解密数据
         * @param {string} encryptedData AES加密后的数据（Base64）
         * @param {string} encryptedKey RSA加密后的AES密钥和IV组合（Base64）
         * @param {string} rsaPrivateKeyBase64 RSA私钥（Base64格式）
         * @returns {string} 解密后的原始数据
         */
        hybridDecrypt: function (encryptedData, encryptedKey, rsaPrivateKeyBase64) {
            // 1. 使用RSA私钥解密获取AES密钥和IV
            var keyAndIv = this.rsaDecrypt(encryptedKey, rsaPrivateKeyBase64);
            var parts = keyAndIv.split('|');
            if (parts.length !== 2) {
                throw new Error('Invalid encrypted key format');
            }
            var aesKey = parts[0];
            var iv = parts[1];

            // 2. 使用AES解密数据
            return this.aesDecrypt(encryptedData, aesKey, iv);
        },
        // ==================== 辅助方法 ====================
        /**
         * 计算字符串的SHA-256哈希值（Base64格式）
         * @param {string} input 输入字符串
         * @returns {string} Base64格式的哈希值
         */
        sha256Base64: function (input) {
            var hash = CryptoJS.SHA256(input);
            return hash.toString(CryptoJS.enc.Base64);
        },
        /**
         * 计算字符串的SHA-256哈希值（十六进制格式）
         * @param {string} input 输入字符串
         * @returns {string} 十六进制格式的哈希值
         */
        sha256Hex: function (input) {
            var hash = CryptoJS.SHA256(input);
            return hash.toString(CryptoJS.enc.Hex);
        }
    };
    // 导出到全局
    window.SecureUtils = SecureUtils;
})(window);