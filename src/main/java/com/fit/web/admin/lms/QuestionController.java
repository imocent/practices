package com.fit.web.admin.lms;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsQuestion;
import com.fit.entity.LmsTop;
import com.fit.service.LmsQuestionService;
import com.fit.service.LmsTopService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/lms/question")
public class QuestionController extends BaseController {

    private static String PREFIX = "/admin/lms/question/";

    @Autowired
    private LmsQuestionService service;

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
        List<LmsQuestion> list = this.service.findList(params);
        int count = this.service.findCount(params);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            LmsQuestion bean = this.service.get(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(LmsQuestion bean) {
        LmsQuestion entity = this.service.get(bean.getId());
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (null == entity) {
            bean.setCtime(new Date());
            bean.setCuser(userId);
            this.service.save(bean);
        } else {
            BeanUtil.copyProperties(bean, entity);
            entity.setEtime(new Date());
            entity.setEuser(userId);
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
    public Object del(String ids) {
        if (OftenUtil.isNotEmpty(ids)) {
            this.service.batchDelete(ids.split(","));
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }
}