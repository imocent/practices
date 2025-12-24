package com.fit.web.admin.lms;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsQuestion;
import com.fit.entity.LmsQuestionAnswer;
import com.fit.service.LmsQuestionAnswerService;
import com.fit.service.LmsQuestionService;
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
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/lms/question/answer")
public class QuestionAnswerController extends BaseController {

    private static String PREFIX = "/admin/lms/question/";

    @Autowired
    private LmsQuestionAnswerService service;

    /**
     * 列表页面
     */
    @GetMapping("/list")
    public String index(Model model, Long id) {
        model.addAttribute("id", id);
        return PREFIX + "answers";
    }

    /**
     * 查询列表
     */
    @PostMapping("/list")
    @ResponseBody
    public Object list(HttpServletRequest request) {
        Map<String, Object> params = WebUtil.getRequestMap(request);
        List<LmsQuestionAnswer> list = this.service.findList(params);
        int count = this.service.findCount(params);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            LmsQuestionAnswer bean = this.service.get(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(LmsQuestionAnswer bean) {
        LmsQuestionAnswer entity = this.service.get(bean.getId());
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
}