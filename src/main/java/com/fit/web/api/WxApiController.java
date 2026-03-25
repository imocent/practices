package com.fit.web.api;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.WxAccount;
import com.fit.entity.WxAccountMenu;
import com.fit.entity.WxMsgTemplate;
import com.fit.entity.WxMsgText;
import com.fit.enums.MsgType;
import com.fit.enums.WechatAPI;
import com.fit.service.WxAccountMenuService;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxMsgTemplateService;
import com.fit.service.WxMsgTextService;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信与开发者服务器交互接口
 */
@Slf4j
@RestController
@RequestMapping("/wxapi")
public class WxApiController extends BaseController {

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAccountMenuService menuService;
    @Autowired
    private WxMsgTextService textService;
    @Autowired
    private WxMsgTemplateService templateService;

    @ResponseBody
    @RequestMapping(value = "/shift", method = RequestMethod.POST)
    public Object shift(String id) {
        if (OftenUtil.isNotEmpty(id)) {
            this.tokenService.switchTo(id);
            return AjaxResult.success("切换成功");
        } else {
            return AjaxResult.error("切换异常");
        }
    }

    /**
     * GET请求：进行URL、Tocken 认证；
     * 1. 将token、timestamp、nonce三个参数进行字典序排序
     * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
     * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
     */
    @ResponseBody
    @RequestMapping(value = "/{account}/message", method = RequestMethod.GET)
    public String doGet(HttpServletRequest request, @PathVariable String account) {
        //如果是多账号，根据url中的account参数获取对应的MpAccount处理即可
        Map<String, Object> param = WebUtil.getRequestMap(request);
        String signature = param.get("signature").toString();
        String echostr = param.get("echostr").toString();
        param.remove("signature");
        param.remove("echostr");
        param.put("token", tokenService.getCurrentToken());
        //在生产环境中使用加密的方式是最好的。
        if (WechatUtil.checkSignature(signature, param.values().toArray(new String[0]))) {
            return echostr; //测试通过
        }
        return "error"; //测试失败
    }

    @ResponseBody
    @RequestMapping(value = "/doPublishMenu", method = RequestMethod.POST)
    public Object doPublishMenu(HttpServletRequest request) {
        WxAccount account = tokenService.getCurrentWxAccount();
        Map<String, Object> param = WebUtil.getRequestMap(request);
        param.put("account", account.getAccount());
        List<WxAccountMenu> menus = this.menuService.findList(param);
        JSONObject menuJson = WechatUtil.getMenuJson(menus);
        return WechatUtil.apiPostCall(WechatAPI.MENU_CREATE.format(tokenService.getAccessToken(account.getAccount())), menuJson);
    }

    /**
     * 群发消息
     */
    @ResponseBody
    @RequestMapping(value = "/sendTextMessage", method = RequestMethod.POST)
    public AjaxResult sendTextMessage(String openIds, Long id) {
        String token = tokenService.getCurrentToken();
        WxMsgText msgText = this.textService.get(id);
        JSONObject text = new JSONObject();
        text.put("content", msgText.getContent());
        JSONObject call = WechatUtil.bulkMessaging(token, openIds.split(","), MsgType.Text.name(), text);
        if (WechatUtil.isWxError(call)) {
            return AjaxResult.error("群发失败", call);
        }
        return AjaxResult.success("群发成功");
    }

    /**
     * 发送模板消息
     */
    @ResponseBody
    @RequestMapping(value = "/sendTemplateMessage", method = RequestMethod.POST)
    public AjaxResult sendTemplateMessage(String openIds, Long id) {
        String token = tokenService.getCurrentToken();
        WxMsgTemplate tplMsg = this.templateService.get(id);
        JSONObject jsObj = new JSONObject();
        jsObj.put("touser", openIds.split(","));
        jsObj.put("template_id", tplMsg.getTplId());
        jsObj.put("url", "http://weixin.qq.com/download");
        jsObj.put("topcolor", "#FF0000");
        jsObj.put("data", "");
        JSONObject call = WechatUtil.apiPostCall(WechatAPI.MSG_TEMPLATE_SEND.format(token), jsObj);
        if (WechatUtil.isWxError(call)) {
            return AjaxResult.error("发送模板消息失败", call);
        }
        return AjaxResult.success("发送模板消息成功");
    }
}