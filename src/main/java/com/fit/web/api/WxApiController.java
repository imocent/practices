package com.fit.web.api;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.service.WxApiTokenService;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 微信与开发者服务器交互接口
 */
@Slf4j
@Controller
@RequestMapping("/wxapi")
public class WxApiController extends BaseController {

    @Autowired
    private WxApiTokenService tokenService;

    @PostMapping("/shift")
    @ResponseBody
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
    @RequestMapping(value = "/{account}/message", method = RequestMethod.GET)
    @ResponseBody
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

}