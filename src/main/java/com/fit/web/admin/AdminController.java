package com.fit.web.admin;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.MenuNode;
import com.fit.entity.ZTreeNode;
import com.fit.service.MenuNodeService;
import com.fit.service.ZtreeNodeService;
import com.fit.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @AUTO
 * @FILE AdminController.java
 * @DATE 2018-3-23 下午10:32:27
 * @Author AIM
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private MenuNodeService menuService;
    @Autowired
    private ZtreeNodeService ztreeNodeService;

    @GetMapping(value = {"", "/", "/index"})
    public String index(HttpServletRequest request, Model model) {
        List<MenuNode> menus = menuService.getUserMenuNodes(Arrays.asList(Long.valueOf("1")), request);
        model.addAttribute("menus", menus);
        return "admin/index.html";
    }

    @GetMapping("/welcome")
    public String main(Model model) {
        model.addAttribute("os", SystemUtil.getOsInfo());
        return "admin/welcome.html";
    }

    @RequestMapping("/dept/tree")
    @ResponseBody
    public AjaxResult depTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.deptZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.tree(tree);
    }

    @RequestMapping("/dict/tree")
    @ResponseBody
    public Object dicTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.dictZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.tree(tree);
    }

    @RequestMapping(value = "/res/tree")
    @ResponseBody
    public Object resTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.menuZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.tree(tree);
    }

    @RequestMapping(value = "/role/tree")
    @ResponseBody
    public Object roleTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.roleZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.success(tree);
    }

    @RequestMapping("/subject/tree")
    @ResponseBody
    public AjaxResult subTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.subjectZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.tree(tree);
    }

    @RequestMapping("/room/tree")
    @ResponseBody
    public AjaxResult roomTree() {
        List<ZTreeNode> tree = this.ztreeNodeService.roomsZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.tree(tree);
    }
}