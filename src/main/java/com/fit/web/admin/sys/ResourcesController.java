package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysResources;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.service.SysResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author AIM
 * @Des 资源控制器
 * @DATE 2020/8/13
 */
@Controller
@RequestMapping("/admin/res")
public class ResourcesController extends BaseController {

    private static String PREFIX = "/admin/sys/resources/";

    @Autowired
    private SysResourcesService resourcesService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("title", "资源列表");
        return PREFIX + "list";
    }

    /**
     * 资源列表
     */
    @PostMapping("/list")
    @ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<SysResources> userList = resourcesService.findList(map);
        int count = resourcesService.findCount(map);
        writeJson(response, AjaxResult.tables(count, userList));
    }

    /**
     * 添加更新页面
     */
    @RequestMapping("edit")
    public String edit(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysResources resources = resourcesService.get(id);
            model.addAttribute("menu", resources);
        }
        return PREFIX + "edit";
    }
}