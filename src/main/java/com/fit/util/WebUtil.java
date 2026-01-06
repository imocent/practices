package com.fit.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @AUTO 请求对象工具类
 * @Author AIM
 * @DATE 2018/6/15
 */
public class WebUtil {

    private static final String COMMA = ",";
    private static final String EMPTY = "";
    /**
     * 前台登录地址
     */
    public static String LOGIN_URL = "/login";

    public static String ADMIN_URL = "/admin";

    public static String ADMIN_MAIN_URL = "/index";
    /**
     * 后台登录地址
     */
    public static String ADMIN_LOGIN_URL = "/admin/login";
    private static final String[] ADDR_HEADER = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    private WebUtil() {
        throw new Error("工具类不能实例化！");
    }

    /**
     * URL解码
     */
    public static String decode(String s) {
        String ret = s;
        try {
            ret = URLDecoder.decode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    /**
     * URL编码
     */
    public static String encode(String s) {
        String ret = s;
        try {
            ret = URLEncoder.encode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    /**
     * 请求参数URL转MAP对象
     */
    public static Map<String, String> reqStr2Map(String s) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String[] splitByte = s.split("&");
            for (String str : splitByte) {
                map.put(str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1));
            }
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
        return map;
    }

    /**
     * 将MAP转换成URL
     */
    public static String reqMap2Str(Map<String, Object> map) {
        try {
            if (map == null) {
                return "";
            }
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(entry.getKey() + "=" + entry.getValue());
                sb.append("&");
            }
            if (sb.toString().endsWith("&")) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
    }

    public static Map<String, String[]> parseQueryString(String s) {
        String[] valArray = null;
        if (s == null) {
            throw new IllegalArgumentException();
        } else {
            Map<String, String[]> ht = new HashMap();
            StringTokenizer st = new StringTokenizer(s, "&");
            while (true) {
                String pair;
                int pos;
                do {
                    if (!st.hasMoreTokens()) {
                        return ht;
                    }

                    pair = st.nextToken();
                    pos = pair.indexOf(61);
                } while (pos == -1);
                String key = pair.substring(0, pos);
                String val = pair.substring(pos + 1, pair.length());
                if (!ht.containsKey(key)) {
                    valArray = new String[]{val};
                } else {
                    String[] oldVals = (String[]) ht.get(key);
                    valArray = new String[oldVals.length + 1];

                    for (int i = 0; i < oldVals.length; ++i) {
                        valArray[i] = oldVals[i];
                    }

                    valArray[oldVals.length] = val;
                }

                ht.put(key, valArray);
            }
        }
    }

    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     */
    public static String formatUrlMap(Map<String, Object> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (OftenUtil.isNotEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue().toString().trim();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }

    /**
     * 请求参数转Map对象无前缀
     */
    public static Map<String, Object> getRequestMap(HttpServletRequest request) {
        return getRequestMap(request, null, false);
    }

    /**
     * 请求参数转Map对象有前缀
     *
     * @param request 客户端的请求对象
     * @param prefix  前缀
     */
    public static Map<String, Object> getRequestMapWithPrefix(HttpServletRequest request, String prefix) {
        return getRequestMap(request, prefix, true);
    }

    /**
     * 请求参数中指定前缀转Map对象
     *
     * @param request        客户端的请求对象
     * @param prefix         前缀
     * @param nameWithPrefix 是否有key值前缀名
     */
    private static Map<String, Object> getRequestMap(HttpServletRequest request, String prefix, boolean nameWithPrefix) {
        Map<String, Object> map = new HashMap();
        Enumeration names = request.getParameterNames();

        while (names.hasMoreElements()) {//测试此枚举是否包含更多的元素
            String name = (String) names.nextElement();// 参数名
            // 判断name开头是否与指定的字符串匹配
            if (prefix != null && name.startsWith(prefix)) {
                String key = nameWithPrefix ? name : name.substring(prefix.length());
                String value = join(COMMA, request.getParameterValues(name));
                map.put(key, value);
            } else {
                String key = name;
                String value = join(COMMA, request.getParameterValues(name));
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将集合转化为字符串
     *
     * @param separator 分隔符
     * @param list      参数集合
     */
    public static String join(String separator, List<?> list) {
        Object[] objs = new Object[list.size()];
        list.toArray(objs);
        return join(separator, objs);
    }

    /**
     * 将数组转化为字符串
     *
     * @param array     参数数组
     * @param separator 分隔符
     */
    public static String join(String separator, Object[] array) {
        if (array == null) {
            return null;
        } else if (array.length <= 0) {
            return EMPTY;
        } else if (array.length == 1) {
            return String.valueOf(array[0]);
        } else {
            StringBuilder sb = new StringBuilder(array.length * 16);
            for (int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(separator);
                }
                sb.append(array[i]);
            }
            return sb.toString();
        }
    }

    /**
     * 获取请求头的信息
     */
    public static Map<String, Object> getReqHeaderMsg(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<?> enum1 = request.getHeaderNames();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 获取请求内容的信息
     */
    public static String getReqBodyMsg(HttpServletRequest request) {
        String inputLine, str = "";
        try {
            BufferedReader br = request.getReader();
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 请求地址
     */
    public static String getURL(HttpServletRequest request) {
        String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        String scheme = request.getScheme();
        String url = scheme + "://" + request.getServerName();
        url = url + ":" + getPort(request) + contextPath;
        return url;
    }

    /**
     * 请求端口
     */
    public static int getPort(HttpServletRequest request) {
        try {
            return request.getServerPort();
        } catch (Exception e) {
            return 80;
        }
    }

    /**
     * 字符串首位去空
     */
    public static String trimSpaces(String str) {
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    /**
     * 判断是不是IP地址
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        IP = trimSpaces(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = IP.split("\\.");
            if ((Integer.parseInt(s[0]) < 255) && (Integer.parseInt(s[1]) < 255) && (Integer.parseInt(s[2]) < 255) && (Integer.parseInt(s[3]) < 255))
                b = true;
        }
        return b;
    }

    /**
     * 获取请求的域名或IP
     */
    public static String generic_domain(HttpServletRequest request) {
        String system_domain = "localhost";
        String serverName = request.getServerName();
        if (isIp(serverName)) system_domain = serverName;
        else {
            system_domain = serverName.substring(serverName.indexOf(".") + 1);
        }

        return system_domain;
    }

    /**
     * 请求request获取完整请求路径
     */
    public static String getLocation(HttpServletRequest request) {
        StringBuffer buff = request.getRequestURL();
        return buff.toString();
    }

    /**
     * 判断是否ajax请求
     *
     * @param request 客户端的请求对象
     */
    public static boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
    }

    /**
     * 返回输出
     *
     * @param str      返回信息
     * @param response 服务器的响应对象
     */
    public static void writeToBrowser(String str, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter out = response.getWriter();
            out.print(str);
            out.flush();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    /**
     * 获取请求的IP地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String addr = "127.0.0.1";
        if (request != null) {
            // 按优先级遍历请求头获取IP
            for (String header : ADDR_HEADER) {
                addr = request.getHeader(header);
                if (!(StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr))) {
                    break;
                }
            }
            if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
                addr = request.getRemoteAddr();
            } else {
                addr = addr.split(",")[0].trim();
            }
        }
        return addr;
    }
}