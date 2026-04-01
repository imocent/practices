package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.entity.WxAccount;
import com.fit.entity.WxAccountFans;
import com.fit.entity.WxAccountFansTag;
import com.fit.service.WxAccountFansService;
import com.fit.service.WxAccountFansTagService;
import com.fit.service.WxApiTokenService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2026/2/26
 */
@Slf4j
@Controller
@RequestMapping("/admin/wx/fans/tag")
public class WechatAccountFansTagController {

    private static String PREFIX = "/admin/wx/accountFansTag/";

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAccountFansTagService service;

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
        List<WxAccountFansTag> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            WxAccountFansTag bean = service.getByObjId(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(WxAccountFansTag bean) {
        WxAccountFansTag entity = this.service.get(bean.getId());
        if (null == entity) {
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


    @PostMapping("/syncFansTagsList")
    @ResponseBody
    public Object syncFansTagsList() {
        WxAccount wxAccount = tokenService.getCurrentWxAccount();
        String accessToken = WechatUtil.getAccessToken(wxAccount.getAppid(), wxAccount.getAppsecret());
        JSONObject objs = WechatUtil.getFansTagsList(accessToken);
        if (objs != null && objs.containsKey("tags")) {
            JSONArray tags = objs.getJSONArray("tags");
            for (int i = 0; i < tags.size(); i++) {
                JSONObject tag = tags.getJSONObject(i);
                WxAccountFansTag bean = this.service.get(tag.getLong("id"));
                if (null == bean) {
                    bean = tag.toJavaObject(WxAccountFansTag.class);
                    bean.setAccount(this.tokenService.getCurrentAccount());
                    this.service.save(bean);
                } else {
                    if (!tag.getInteger("count").equals(bean.getCount())) {
                        bean.setCount(tag.getInteger("count"));
                        this.service.update(bean);
                    }
                }
            }
            return AjaxResult.success();
        }
        return AjaxResult.error("同步获取标签列表错误信息", objs);
    }
}