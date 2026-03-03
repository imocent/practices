package com.fit.web.api;

import com.fit.base.BaseController;
import com.fit.entity.WxAccount;
import com.fit.service.WxApiTokenService;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import com.fit.util.WechatVerifyUtil;
import com.fit.util.WechatXmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/wechat/msg/{appid}")
public class WechatMsgController extends BaseController {

    @Autowired
    private WxApiTokenService tokenService;

    /**
     * 微信服务器的认证消息
     *
     * @param appid
     * @return
     */
    @GetMapping
    @ResponseBody
    public String authGet(HttpServletRequest request, @PathVariable String appid) {
        Map<String, Object> params = WebUtil.getRequestMap(request);
        log.info("\n接收到来自微信服务器的认证消息：[{}]", params);
        String signature = params.get("signature").toString();
        String timestamp = params.get("timestamp").toString();
        String nonce = params.containsKey("nonce") ? params.get("nonce").toString() : "";
        String echostr = params.get("echostr").toString();
        if (WechatUtil.isAnyEmpty(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        this.tokenService.switchTo(appid);

        if (WechatUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }

        return "非法请求";
    }

    /**
     * 微信各类消息
     *
     * @param appid
     * @param requestBody
     * @return
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(HttpServletRequest request, @PathVariable String appid, @RequestBody String requestBody) {
        Map<String, Object> params = WebUtil.getRequestMap(request);
        String signature = params.get("signature").toString();
        String timestamp = params.get("timestamp").toString();
        String nonce = params.containsKey("nonce") ? params.get("nonce").toString() : "";
        String openid = params.containsKey("openid") ? params.get("openid").toString() : "";
        String encryptType = params.containsKey("encrypt_type") ? params.get("encrypt_type").toString() : "raw";
        String msgSignature = params.containsKey("msg_signature") ? params.get("msg_signature").toString() : "";
        if (!WechatUtil.checkSignature(signature, timestamp, nonce)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        this.tokenService.switchTo(appid);
        WxAccount account = this.tokenService.getCurrentWxAccount();
        String out = "";
        if (encryptType.equalsIgnoreCase("aes")) { // aes加密的消息
            out = WechatVerifyUtil.decrypt(account.getAesKey(), requestBody);
        } else { // 明文传输的消息
            Map<String, Object> map = WechatXmlUtil.xml2Map(requestBody);
            out = map.toString();
        }
        log.debug("\n组装回复信息：{}", out);
        return out;
    }
}