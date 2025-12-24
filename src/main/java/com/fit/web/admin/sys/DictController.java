package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysDict;
import com.fit.entity.SysUser;
import com.fit.entity.ZTreeNode;
import com.fit.service.SysDictService;
import com.fit.service.ZtreeNodeService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 部门控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/dict")
public class DictController extends BaseController {

    private static String PREFIX = "/admin/sys/dict/";

    @Autowired
    private SysDictService dictService;
    @Autowired
    private ZtreeNodeService ztreeNodeService;

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
    public Object list(HttpServletRequest request) {
        Map<String, Object> params = WebUtil.getRequestMap(request);
        List<SysDict> list = dictService.findList(params);
        int count = dictService.findCount(params);
        return AjaxResult.tables(count, list);
    }

    /**
     * @AUTO 获取字典的tree列表
     * @DATE 2019/5/23
     */
    @RequestMapping("/tree")
    @ResponseBody
    public Object tree() {
        List<ZTreeNode> tree = this.ztreeNodeService.dictZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.success(tree);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysDict sysDict = dictService.get(id);
            model.addAttribute("dict", sysDict);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(SysDict dict) {
        SysDict sysDict = this.dictService.get(dict.getId());
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (null == sysDict) {
            dict.setCtime(new Date());
            dict.setCuser(userId);
            this.dictService.save(dict);
        } else {
            BeanUtil.copyProperties(dict, sysDict);
            sysDict.setEtime(new Date());
            sysDict.setEuser(userId);
            this.dictService.update(sysDict);
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
            this.dictService.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }
}