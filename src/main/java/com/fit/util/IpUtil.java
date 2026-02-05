package com.fit.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/2/4
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String[] HEADER_NAMES = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "CF-Connecting-IP"};

    public static String getSigned(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getHeader("User-Agent"));
        sb.append("^").append(request.getHeader("Accept-Language"));
        sb.append("^").append(getClientIp(request));
        try {
            MessageDigest sign = MessageDigest.getInstance("MD5");
            byte[] digest = sign.digest(sb.toString().getBytes("UTF-8"));
            sb.setLength(0);
            for (byte b : digest) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("生成指纹失败:", e);
        }
    }

    /**
     * 获取客户端真实 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = UNKNOWN;
        // 1. 优先从代理头获取
        ip = getIpFromHeader(request);
        // 2. 如果通过代理没获取到，使用 getRemoteAddr
        if (isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 3. 处理本地地址
        ip = handleLocalIp(ip);
        // 4. 处理多个 IP 的情况（如：192.168.1.1, 10.0.0.1）
        if (!isBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 从请求头中获取 IP
     */
    private static String getIpFromHeader(HttpServletRequest request) {
        String ip = null;
        for (String header : HEADER_NAMES) {
            ip = request.getHeader(header);
            if (!isBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
                break;
            }
        }
        return ip;
    }

    /**
     * 处理本地地址
     */
    private static String handleLocalIp(String ip) {
        if (isBlank(ip)) {
            return ip;
        }

        if (LOCALHOST_IPV6.equals(ip) || "::1".equals(ip)) {
            return LOCALHOST_IPV4;
        }

        return ip;
    }

    /**
     * 获取服务端 IP 地址
     */
    public static String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return LOCALHOST_IPV4;
        }
    }

    /**
     * 判断是否为内网 IP
     */
    public static boolean isInternalIp(String ip) {
        if (isBlank(ip)) {
            return false;
        }

        // 本地地址
        if (LOCALHOST_IPV4.equals(ip)) {
            return true;
        }

        // 内网地址段
        return ip.matches("^(127\\.0\\.0\\.1)|" + "(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|" + "(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|" + "(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
    }

    public static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }

        // 遍历每个字符，检查是否是空白字符
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
