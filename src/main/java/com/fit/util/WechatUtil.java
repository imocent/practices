package com.fit.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit.entity.WxAccountMenu;
import com.fit.enums.WechatAPI;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 微信公众平台工具类
 * https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login
 */
@Slf4j
public class WechatUtil {

    public static final List<String> MENU_NEED_KEY = Arrays.asList("click", "scancode_push", "scancode_waitmsg", "pic_sysphoto", "pic_photo_or_album", "pic_weixin", "location_select");
    //素材文件后缀
    public static Map<String, String> type_fix = new HashMap<>();
    public static Map<String, String> media_fix = new HashMap<>();
    //素材文件大小
    public static Map<String, Long> type_length = new HashMap<>();

    static {
        type_fix.put("image", "bmp|png|jpeg|jpg|gif");
        type_fix.put("voice", "mp3|wma|wav|amr");
        type_fix.put("video", "mp4");
        type_fix.put("thumb", "jpg");

        media_fix.put("image", "png|jpeg|jpg|gif");
        media_fix.put("voice", "mp3|amr");
        media_fix.put("video", "mp4");
        media_fix.put("thumb", "jpg");

        type_length.put("image", new Long(2 * 1024 * 1024));
        type_length.put("voice", new Long(2 * 1024 * 1024));
        type_length.put("video", new Long(10 * 1024 * 1024));
        type_length.put("thumb", new Long(64 * 1024));
    }

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
            JSONObject jsonObject = apiGetCall(WechatAPI.TOKEN.format(appid, appSecret));
            return jsonObject != null ? jsonObject.getString("access_token") : null;
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            return null;
        }
    }

    public static JSONArray getChangeOpenid(String access_token, String appid, String[] openIds) {
        String uri = WechatAPI.CHANGE_OPENID.format(access_token);
        JSONObject json = new JSONObject();
        json.put("from_appid", appid);
        json.put("openid_list", openIds);
        JSONObject call = apiCall(uri, "POST", json);
        return call.getIntValue("errcode") == 0 ? call.getJSONArray("openid_list") : null;
    }

    /**
     * 获取粉丝列表
     */
    public static JSONObject getFansList(String access_token) {
        try {
            return apiGetCall(WechatAPI.FANS_LIST.format(access_token));
        } catch (Exception e) {
            log.error("获取关注列表异常", e);
        }
        return null;
    }

    /**
     * 获取粉丝详情
     */
    public static JSONObject getFansInfo(String openId, String access_token) {
        try {
            return apiGetCall(WechatAPI.FANS_INFO.format(access_token, openId));
        } catch (Exception e) {
            log.error("获取粉丝详情异常", e);
        }
        return null;
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
        JSONObject call = apiGetCall(WechatAPI.GET_TICKET.format(access_token, type));
        return call != null ? call.getString("ticket") : null;
    }

    /**
     * 获取用户信息
     *
     * @param openid
     * @param lang
     * @return
     */
    public static JSONObject getUserInfo(String access_token, String openid, String lang) {
        if (lang == null) {
            lang = "zh_CN";
        }
        return apiGetCall(WechatAPI.USER_INFO.format(access_token, openid, lang));
    }

    public static JSONObject tagging(String access_token, Long tagId, String openid) {
        JSONObject json = new JSONObject();
        json.put("tagid", tagId);
        json.put("openid_list", new String[]{openid});
        String uri = WechatAPI.TAGS_MEMBERS_TAGGING.format(access_token);
        return apiCall(uri, "POST", json);
    }

    public static JSONObject untagging(String access_token, Long tagId, String openid) {
        JSONObject json = new JSONObject();
        json.put("tagid", tagId);
        json.put("openid_list", new String[]{openid});
        String uri = WechatAPI.TAGS_MEMBERS_UNTAGGING.format(access_token);
        return apiCall(uri, "POST", json);
    }

    /**
     * 草稿箱开关设置
     *
     * @return
     */
    public static JSONObject getDraftSwitch(String access_token) {
        return apiPostCall(WechatAPI.DRAFT_SWITCH.format(access_token));
    }

    public static JSONObject apiPostCall(String uri) {
        return apiPostCall(uri, null);
    }

    public static JSONObject apiPostCall(String uri, JSONObject params) {
        return apiCall(uri, "POST", params);
    }

    public static JSONObject apiGetCall(String uri) {
        return apiCall(uri, "GET", null);
    }

    /**
     * 素材添加
     *
     * @param type   素材类型（image/voice/video/thumb）
     * @param file   文件
     * @param params 视频数据
     * @return
     */
    public static JSONObject uploadMedia(String accessToken, String type, File file, JSONObject params) {
        if (!file.exists()) {
            throw new RuntimeException("{\"errcode\":-2,\"errmsg\":\"文件不存在\"}");
        }
        String fileName = file.getName();
        //获取后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        long length = file.length();
        //此处做判断是为了尽可能的减少对微信API的调用次数
        if (type_fix.get(type).indexOf(suffix) == -1) {
            throw new RuntimeException("{\"errcode\":40005,\"errmsg\":\"不合法的文件类型\"}");
        }
        if (length > type_length.get(type)) {
            throw new RuntimeException("{\"errcode\":40006,\"errmsg\":\"不合法的文件大小\"}");
        }
        params.put("media", file);

        return apiCall(WechatAPI.MEDIA_UPLOAD.format(accessToken, type), "POST", params);
    }

    /**
     * @param accessToken 接口调用凭证
     * @param toUser      用户OpenID列表（2-10000个）
     * @param type        消息类型：text, mpnews, images, voice, mpvideo, wxcard
     * @param params      消息参数，根据msgtype不同传入不同参数
     * @return
     */
    public static JSONObject bulkMessaging(String accessToken, String[] toUser, String type, JSONObject params) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("touser", toUser);
        requestBody.put("msgtype", type);
        requestBody.put(type, params);
        return apiPostCall(WechatAPI.MASS_SEND.format(accessToken), requestBody);
    }

    /**
     * 请求到微信接口
     *
     * @param uri    请求路径
     * @param method 请求方式（GET/POST）
     * @param params 请求参数（Map形式，会自动转换为JSON字符串）
     * @return
     */
    public static JSONObject apiCall(String uri, String method, JSONObject params) {
        HttpsURLConnection httpUrlConn = null;
        InputStream inputStream = null;
        Scanner scanner = null;
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
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
            // 将Map转换为JSON字符串
            String outputStr = JSONObject.toJSONString(params);
            // 当有参数需要提交时（非GET请求且有参数）
            if (null != params && !params.isEmpty() && !"GET".equalsIgnoreCase(method)) {
                // 检查是否包含文件上传
                if (params.containsKey("media")) {
                    // 处理文件上传（multipart/form-data）
                    String boundary = "----------" + System.currentTimeMillis();
                    httpUrlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    dataOutputStream = new DataOutputStream(httpUrlConn.getOutputStream());
                    // 1. 写入 media 字段
                    dataOutputStream.writeBytes(boundary + "\r\n");
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"media\"; filename=\"" + params.getString("filename") + "\"\r\n");
                    dataOutputStream.writeBytes("Content-Type: " + getMimeType(params.getString("type")) + "\r\n");
                    dataOutputStream.writeBytes("\r\n");
                    // 写入文件内容（关键修复点）
                    dataOutputStream.write(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), params.getString("media"))));
                    dataOutputStream.writeBytes("\r\n");
                    // 2. 如果有 description 参数（视频素材必需）
                    if (params.containsKey("description")) {
                        dataOutputStream.writeBytes(boundary + "\r\n");
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n");
                        dataOutputStream.writeBytes("Content-Type: application/json\r\n");
                        dataOutputStream.writeBytes("\r\n");
                        dataOutputStream.writeBytes(params.getString("description"));
                        dataOutputStream.writeBytes("\r\n");
                    }
                    // 3. 结束标记（关键修复点）
                    dataOutputStream.writeBytes(boundary + "--\r\n");
                    dataOutputStream.flush();
                } else {
                    // 处理普通的JSON请求
                    httpUrlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    outputStream = httpUrlConn.getOutputStream();
                    outputStream.write(outputStr.getBytes("UTF-8"));
                    outputStream.flush();
                }
            }
            // 读取响应
            inputStream = httpUrlConn.getInputStream();
            scanner = new Scanner(inputStream, "UTF-8");
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                String response = scanner.next();
                if (response != null && !response.trim().isEmpty()) {
                    JSONObject result = JSONObject.parseObject(response);
                    // 检查是否有错误码
                    if (result.containsKey("errcode") && result.getInteger("errcode") != 0) {
                        log.error("微信接口返回错误：errcode={}, errmsg={}", result.getInteger("errcode"), result.getString("errmsg"));
                    }
                    return result;
                } else {
                    log.error("响应内容为空");
                }
            } else {
                log.error("没有读取到响应内容");
            }
        } catch (FileNotFoundException e) {
            log.error("文件不存在：", e);
        } catch (Exception e) {
            log.error("请求微信接口错误信息：", e);
            // 尝试读取错误响应
            if (httpUrlConn != null) {
                try {
                    InputStream errorStream = httpUrlConn.getErrorStream();
                    if (errorStream != null) {
                        scanner = new Scanner(errorStream, "UTF-8");
                        scanner.useDelimiter("\\A");
                        if (scanner.hasNext()) {
                            String errorResponse = scanner.next();
                            log.error("微信接口错误响应：{}", errorResponse);
                            return JSONObject.parseObject(errorResponse);
                        }
                    }
                } catch (Exception ex) {
                    log.error("读取错误响应异常：", ex);
                }
            }
        } finally {
            // 关闭资源
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
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (Exception e) {
                    log.error("关闭数据输出流异常", e);
                }
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return null;
    }

    /**
     * 获取菜单结构
     *
     * @param menus
     * @return
     */
    public static JSONObject getMenuJson(List<WxAccountMenu> menus) {
        JSONObject root = new JSONObject();
        if (!menus.isEmpty()) {
            List<WxAccountMenu> parentAM = new ArrayList<WxAccountMenu>();
            Map<Long, List<JSONObject>> subAm = new HashMap<Long, List<JSONObject>>();
            for (WxAccountMenu m : menus) {
                if (m.getParentId().intValue() == 0) {//一级菜单
                    parentAM.add(m);
                } else {//二级菜单
                    if (subAm.get(m.getParentId()) == null) {
                        subAm.put(m.getParentId(), new ArrayList<JSONObject>());
                    }
                    List<JSONObject> tmpMenus = subAm.get(m.getParentId());
                    // 直接构建二级菜单对象
                    tmpMenus.add(buildMenuJSON(m));
                    subAm.put(m.getParentId(), tmpMenus);
                }
            }

            JSONArray arr = new JSONArray();
            for (WxAccountMenu m : parentAM) {
                if (subAm.get(m.getId()) != null) {//有子菜单
                    // 构建带子菜单的一级菜单
                    JSONObject parentObj = new JSONObject();
                    parentObj.put("name", m.getName());
                    parentObj.put("sub_button", subAm.get(m.getId()));
                    arr.add(parentObj);
                } else {//没有子菜单
                    // 直接构建一级菜单
                    arr.add(buildMenuJSON(m));
                }
            }
            root.put("button", arr);
        }
        return root;
    }

    /**
     * 构建菜单JSON对象的内部方法
     *
     * @param menu 菜单对象
     * @return 菜单JSON对象
     */
    private static JSONObject buildMenuJSON(WxAccountMenu menu) {
        JSONObject obj = new JSONObject();
        obj.put("name", menu.getName());
        obj.put("type", menu.getMtype());
        if (WechatUtil.MENU_NEED_KEY.contains(menu.getMtype())) {//事件菜单
            if ("fix".equals(menu.getEventType())) {//fix 消息
                obj.put("key", "_fix_" + menu.getMsgId());//以 _fix_ 开头
            } else {
                if (WechatUtil.isEmpty(menu.getInputCode())) {//如果inputcode 为空，默认设置为 subscribe，以免创建菜单失败
                    obj.put("key", "subscribe");
                } else {
                    obj.put("key", menu.getInputCode());
                }
            }
            //存msgtype id
            obj.put("msgType", menu.getMsgType());
            obj.put("msgId", menu.getMsgId());//
        } else {//链接菜单-view
            obj.put("url", menu.getUrl());
        }
        return obj;
    }

    /**
     * 根据文件类型获取MIME类型
     *
     * @param type 文件类型（image/voice/video/thumb）
     * @return MIME类型
     */
    private static String getMimeType(String type) {
        switch (type.toLowerCase()) {
            case "voice":
                return "audio/amr";
            case "video":
                return "video/mp4";
            case "image":
            case "thumb":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 判断是否微信返回错误
     */
    public static boolean isWxError(JSONObject param) {
        if (null == param || param.getIntValue("errcode") != 0) {
            return true;
        }
        return false;
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