package com.fit.web.admin.lms;

import com.fit.aop.BizLog;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsComments;
import com.fit.entity.SysUser;
import com.fit.service.LmsCommentsService;
import com.fit.util.BeanUtil;
import com.fit.util.DateUtils;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/lms/comment")
public class CommentController extends BaseController {

    private static String PREFIX = "/admin/lms/comment/";

    @Autowired
    private LmsCommentsService service;

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
        List<LmsComments> list = this.service.findList(params);
        int count = this.service.findCount(params);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            LmsComments bean = this.service.get(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(LmsComments bean) {
        LmsComments entity = this.service.get(bean.getId());
        if (null == entity) {
            if (!bean.getUsername().isEmpty()) {
                Map<String, Object> param = new HashMap<>();
                param.put("username", bean.getUsername());
                List<Map<String, Object>> maps = this.service.selectBySQL("SELECT * FROM `sys_user` WHERE `USERNAME`=#{params.username}", param);
                if (maps.size() > 0) {
                    bean.setUserId(Long.valueOf(maps.get(0).get("ID").toString()));
                }
            }
            bean.setCtime(new Date());
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
     * 修改状态
     */
    @RequestMapping("/setState")
    @ResponseBody
    public Object changeState(Long id) {
        LmsComments bean = this.service.get(id);
        if (bean != null) {
            if (bean.getEnabled()) {
                bean.setEnabled(false);
            } else {
                bean.setEnabled(true);
            }
            this.service.update(bean);
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改状态失败");
    }
}