package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.entity.WxMsgNews;
import com.fit.enums.MsgType;
import com.fit.enums.WechatAPI;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxMsgNewsService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2026/2/26
 */
@Controller
@RequestMapping("/admin/wx/message")
public class WechatMessageController {

    private static String PREFIX = "/admin/wx/message/";

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxMsgNewsService service;

    /**
     * 列表页面
     */
    @GetMapping("/list")
    public String index() {
        return PREFIX + "list";
    }

    /**
     * 查询列表
     */
    @PostMapping("/list")
    @ResponseBody
    public AjaxResult list(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("account", tokenService.getCurrentAccount());
        List<WxMsgNews> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            WxMsgNews bean = service.getByObjId(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(WxMsgNews bean) {
        WxMsgNews entity = this.service.queryByKey("wx_msg_news", "input_code", bean.getInputCode());
        if (null == entity) {
            bean.setAccount(tokenService.getCurrentAccount());
            bean.setCreateTime(new Date());
            bean.setMsgType(MsgType.Text.name);
            this.service.save(bean);
        } else {
            BeanUtil.copyProperties(bean, entity);
            this.service.update(entity);
        }
        return AjaxResult.success();
    }

    /**
     * 删除
     *
     * @param ids 删除ID集合
     */
    @PostMapping("/del")
    @ResponseBody
    public Object del(@RequestParam("ids") List<Long> ids) {
        if (OftenUtil.isNotEmpty(ids)) {
            this.service.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * @param id      消息ID
     * @param msgType 消息类型
     * @param openid  粉丝ID
     * @return
     */
    @PostMapping("/sendMessage")
    @ResponseBody
    public Object sendMessage(Long id, String msgType, String openid) {
        String token = this.tokenService.getCurrentToken();
        WxMsgNews news = this.service.get(id);
        JSONObject send = new JSONObject();
        send.put("touser", openid);
        send.put("msgtype", msgType);
        if (msgType.equalsIgnoreCase(MsgType.Text.name())) {
            JSONObject textObj = new JSONObject();
            textObj.put("content", news.getContent());
            send.put("text", textObj);
        } else {
            JSONObject media = new JSONObject();
            media.put("media_id", news.getMediaId());
            send.put("mpnews", media);
        }
        JSONObject call = WechatUtil.apiPostCall(WechatAPI.SEND_CUSTOM_MESSAGE.format(token), send);
        if (WechatUtil.isWxError(call)) {
            return AjaxResult.error("发送失败," + call.get("errmsg"), call);
        }
        return AjaxResult.success("发送成功");
    }
}