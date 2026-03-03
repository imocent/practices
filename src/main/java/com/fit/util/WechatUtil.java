package com.fit.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 微信公众平台工具类
 * https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login
 */
@Slf4j
public class WechatUtil {

    public final static String API_HOST_URL = "https://api.weixin.qq.com";
    public final static String WX_ACCESS_TOKEN = "%s/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public final static String WX_GZ = "%s/cgi-bin/user/get?access_token=%s&next_openid=";
    public final static String WX_TICKET = "%s/cgi-bin/ticket/getticket?access_token=%s&type=%s";
    public final static String WX_USER_INFO = "%s/cgi-bin/user/info?openid=%s&lang=%s";
    public final static String WX_TAGS_MEMBERS_TAGGING = "%s/cgi-bin/tags/members/batchtagging?access_token=%s";
    public final static String WX_TAGS_MEMBERS_UNTAGGING = "%s/cgi-bin/tags/members/batchuntagging?access_token=%s";

    public static boolean isAnyEmpty(CharSequence... css) {
        if (css != null && Array.getLength(css) != 0) {
            for (CharSequence cs : css) {
                if (isEmpty(cs)) {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 获取access_token
     */
    public static String getAccessToken(String appid, String appSecret) {
        try {
            String uri = String.format(WX_ACCESS_TOKEN, API_HOST_URL, appid, appSecret);
            JSONObject jsonObject = apiCall(uri);
            return jsonObject != null ? jsonObject.getString("access_token") : null;
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            return null;
        }
    }

    /**
     * 获取关注列表
     *
     * @param appid
     * @param appSecret
     */
    public static void getGz(String appid, String appSecret) {
        try {
            String access_token = getAccessToken(appid, appSecret);
            if (access_token == null) {
                log.error("获取access_token失败");
                return;
            }
            String uri = String.format(WX_GZ, API_HOST_URL, access_token);
            JSONObject jsonObject = apiCall(uri);
            if (jsonObject != null) {
                log.info("获取关注列表成功：{}", jsonObject);
                System.out.println(jsonObject);
            } else {
                log.error("获取关注列表失败");
            }
        } catch (Exception e) {
            log.error("获取关注列表异常", e);
        }
    }

    public static boolean checkSignature(String signature, String... arr) {
        try {
            if (isAnyEmpty(arr)) {
                throw new IllegalArgumentException("非法请求参数，有部分参数为空 : " + Arrays.toString(arr));
            } else {
                Arrays.sort(arr);
                StringBuilder sb = new StringBuilder();
                for (String a : arr) {
                    sb.append(a);
                }
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
                // 转换为十六进制
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString().equals(signature);
            }
        } catch (Exception e) {
            log.error("Checking signature failed, and the reason is :" + e.getMessage());
            return false;
        }
    }

    /**
     * 权限签名算法 jsapi_ticket的有效期为7200秒
     *
     * @param access_token
     * @param type
     * @return
     */
    public static String getTicket(String access_token, String type) {
        String uri = String.format(WX_TICKET, API_HOST_URL, access_token, type);
        JSONObject jsonObject = apiCall(uri);
        return jsonObject != null ? jsonObject.getString("ticket") : null;
    }

    /**
     * 获取用户信息
     *
     * @param openid
     * @param lang
     * @return
     */
    public static JSONObject getUserInfo(String openid, String lang) {
        if (lang == null) {
            lang = "zh_CN";
        }
        String uri = String.format(WX_USER_INFO, API_HOST_URL, openid, lang);
        return apiCall(uri);
    }

    public static JSONObject tagging(String access_token, Long tagId, String openid) {
        JSONObject json = new JSONObject();
        json.put("tagid", tagId);
        json.put("openid_list", new String[]{openid});
        String uri = String.format(WX_TAGS_MEMBERS_TAGGING, API_HOST_URL, access_token);
        return apiCall(uri, "POST", json.toJSONString());
    }

    public static JSONObject untagging(String access_token, Long tagId, String openid) {
        JSONObject json = new JSONObject();
        json.put("tagid", tagId);
        json.put("openid_list", new String[]{openid});
        String uri = String.format(WX_TAGS_MEMBERS_UNTAGGING, API_HOST_URL, access_token);
        return apiCall(uri, "POST", json.toJSONString());
    }

    public static JSONObject apiCall(String uri) {
        return apiCall(uri, "GET", null);
    }

    /**
     * 请求到微信接口
     *
     * @param uri       请求路径
     * @param method    请求方式（GET/POST）
     * @param outputStr
     * @return
     */
    public static JSONObject apiCall(String uri, String method, String outputStr) {
        HttpsURLConnection httpUrlConn = null;
        InputStream inputStream = null;
        Scanner scanner = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(uri);
            httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(getSSLSocketFactory());
            httpUrlConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(method);
            if ("GET".equalsIgnoreCase(method)) {
                httpUrlConn.connect();
            }
            // 当有数据需要提交时
            if (null != outputStr) {
                outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.flush();
            }
            inputStream = httpUrlConn.getInputStream();
            scanner = new Scanner(inputStream, "UTF-8");
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                String response = scanner.next();
                if (response != null && !response.trim().isEmpty()) {
                    return JSONObject.parseObject(response);
                } else {
                    log.error("响应内容为空");
                }
            } else {
                log.error("没有读取到响应内容");
            }
        } catch (Exception e) {
            log.error("请求微信接口错误信息：", e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("关闭输入流异常", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("关闭输出流异常", e);
                }
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return null;
    }

    private static SSLSocketFactory getSSLSocketFactory() throws Exception {
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
        sslcontext.init(null, tm, new java.security.SecureRandom());
        return sslcontext.getSocketFactory();
    }
}

class MyX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}