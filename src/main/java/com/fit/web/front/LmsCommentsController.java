package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsComments;
import com.fit.entity.LmsCommentsLike;
import com.fit.service.LmsCommentsLikeService;
import com.fit.service.LmsCommentsService;
import com.fit.util.BeanUtil;
import com.fit.util.IpUtil;
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
public class LmsCommentsController extends BaseController {

    @Autowired
    private LmsCommentsService commentsService;
    @Autowired
    private LmsCommentsLikeService commentsLikeService;

    @GetMapping("/comment")
    public String comment(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("enabled", 1);
        map.put("limit", 4);
        map.put("page", 0);
        List<LmsComments> comments = this.commentsService.findList(map);
        model.addAttribute("comments", comments);
        getLikes(request, model);
        return "front/comment";
    }

    @GetMapping("/comments")
    public String comments(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("enabled", 1);
        if (!map.containsKey("page") || !map.containsKey("limit")) {
            map.put("page", 0);
            map.put("limit", 10);
        }
        model.addAttribute("content", map.get("content"));
        model.addAttribute("limit", map.get("limit"));
        model.addAttribute("page", map.get("page"));
        List<LmsComments> comments = this.commentsService.findList(map);
        model.addAttribute("comments", comments);
        int count = this.commentsService.findCount(map);
        model.addAttribute("count", count);
        getLikes(request, model);
        return "front/comments";
    }

    private void getLikes(HttpServletRequest request, Model model) {
        Map<String, Object> map = new HashMap<>();
        Map<Long, Object> likes = new HashMap<>();
        if (SecurityUtils.getSubject().isAuthenticated()) {
            map.put("userId", SecurityUtils.getSubject().getPrincipal());
        } else {
            map.put("ip", IpUtil.getClientIp(request));
            map.put("signed", IpUtil.getSigned(request));
        }
        List<LmsCommentsLike> list = this.commentsLikeService.findList(map);
        for (LmsCommentsLike commentsLike : list) {
            likes.put(commentsLike.getCommentId(), commentsLike.getEnabled());
        }
        model.addAttribute("likes", likes);
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
                bean.setMode("apk");
            } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad") || userAgent.toLowerCase().contains("ipod")) {
                bean.setMode("ios");
            } else {
                bean.setMode("web");
            }
            bean.setCtime(new Date());
            this.commentsService.save(bean);
            return AjaxResult.success();
        } catch (NumberFormatException e) {
            return AjaxResult.error();
        }
    }

    @PostMapping("/comments/like")
    @ResponseBody
    public Object like(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        String ip = IpUtil.getClientIp(request);
        String signed = IpUtil.getSigned(request);
        map.put("ip", ip);
        map.put("signed", signed);
        if (SecurityUtils.getSubject().isAuthenticated()) {
            map.put("userId", SecurityUtils.getSubject().getPrincipal().toString());
        }
        List<LmsCommentsLike> list = this.commentsLikeService.findList(map);
        Long commentId = Long.valueOf(map.get("commentId").toString());
        LmsComments comments = this.commentsService.get(commentId);
        if (list.size() > 0) {
            LmsCommentsLike commentsLike = list.get(0);
            commentsLike.setEtime(new Date());
            if (commentsLike.getEnabled()) {
                commentsLike.setEnabled(false);
                if (comments.getLikeCount() > 0) {
                    comments.setLikeCount(comments.getLikeCount() - 1);
                    this.commentsService.update(comments);
                }
            } else {
                commentsLike.setEnabled(true);
                comments.setLikeCount(comments.getLikeCount() + 1);
                this.commentsService.update(comments);
            }
            this.commentsLikeService.update(commentsLike);
        } else {
            LmsCommentsLike commentsLike = new LmsCommentsLike();
            commentsLike.setEtime(new Date());
            commentsLike.setIp(ip);
            commentsLike.setSigned(signed);
            commentsLike.setCommentId(commentId);
            if (map.containsKey("userId")) {
                commentsLike.setUserId(Long.valueOf(map.get("userId").toString()));
            }
            commentsLike.setEnabled(true);
            this.commentsLikeService.save(commentsLike);
            comments.setLikeCount(comments.getLikeCount() + 1);
            this.commentsService.update(comments);
        }
        return AjaxResult.success();
    }
}
