package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysDept;
import com.fit.service.SysDeptService;
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
@RequestMapping("/admin/dept")
public class DeptController extends BaseController {

    private static String PREFIX = "/admin/sys/dept/";

    @Autowired
    private SysDeptService deptService;

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
        List<SysDept> list = deptService.findList(map);
        int count = deptService.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysDept sysDept = deptService.get(id);
            model.addAttribute("dept", sysDept);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(SysDept dept) {
        SysDept sysDept = this.deptService.get(dept.getId());
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (null == sysDept) {
            dept.setCtime(new Date());
            dept.setCuser(userId);
            this.deptService.save(dept);
        } else {
            BeanUtil.copyProperties(dept, sysDept);
            sysDept.setEtime(new Date());
            sysDept.setEuser(userId);
            this.deptService.update(sysDept);
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
            this.deptService.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }
}