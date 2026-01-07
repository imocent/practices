package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsComments;
import com.fit.service.LmsCommentsService;
import com.fit.util.BeanUtil;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/1/6
 */
@Slf4j
@Controller
public class CommentsController extends BaseController {

    @Autowired
    private LmsCommentsService commentsService;

    @GetMapping("/comment")
    public String comment(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            map.put("enabled", 1);
        }
        map.put("limit", 2);
        map.put("page", 0);
        List<LmsComments> comments = this.commentsService.findList(map);
        model.addAttribute("comments", comments);
        return "front/comment";
    }

    @GetMapping("/comments")
    public String comments(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<LmsComments> comments = this.commentsService.findList(map);
        model.addAttribute("comments", comments);
        model.addAttribute("count", comments.size());
        return "front/comments";
    }

    @PostMapping("/comments/save")
    @ResponseBody
    public Object save(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        try {
            LmsComments bean = BeanUtil.map2Bean(LmsComments.class, map);
            if (!bean.getUsername().isEmpty()) {
                Map<String, Object> param = new HashMap<>();
                param.put("username", map.get("username"));
                List<Map<String, Object>> maps = this.commentsService.selectBySQL("SELECT * FROM `sys_user` WHERE `USERNAME`=#{params.username}", param);
                if (maps.size() > 0) {
                    bean.setUserId(Long.valueOf(maps.get(0).get("ID").toString()));
                }
            }
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.toLowerCase().contains("android")) {
                bean.setMode("安卓");
            } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad") || userAgent.toLowerCase().contains("ipod")) {
                bean.setMode("ios");
            } else {
                bean.setMode("网页");
            }
            bean.setCtime(new Date());
            this.commentsService.save(bean);
            return AjaxResult.success();
        } catch (NumberFormatException e) {
            return AjaxResult.error();
        }
    }
}
