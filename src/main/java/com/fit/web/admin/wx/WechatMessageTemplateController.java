package com.fit.web.admin.wx;

import com.fit.base.AjaxResult;
import com.fit.entity.WxMsgTemplate;
import com.fit.service.WxAffairService;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxMsgTemplateService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
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
@RequestMapping("/admin/wx/template")
public class WechatMessageTemplateController {

    private static String PREFIX = "/admin/wx/template/";

    @Autowired
    private WxMsgTemplateService service;
    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAffairService affairService;

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
        List<WxMsgTemplate> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            WxMsgTemplate bean = service.getByObjId(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(WxMsgTemplate bean) {
        WxMsgTemplate entity = this.service.get(bean.getId());
        if (null == entity) {
            bean.setCreateTime(new Date());
            bean.setAccount(tokenService.getCurrentAccount());
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

    @PostMapping("/syncTemplate")
    @ResponseBody
    public Object syncTemplate() {
        affairService.syncTemplates();
        return AjaxResult.success();
    }
}