package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.*;
import com.fit.service.*;
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
import java.util.*;

/**
 * @AUTO
 * @FILE IndexController.java
 * @DATE 2018-3-23 下午10:32:27
 * @Author AIM
 */
@Slf4j
@Controller
public class IndexController extends BaseController {

    @Autowired
    private MenuNodeService menuService;
    @Autowired
    private LmsTopService topService;
    @Autowired
    private LmsExamRoomService roomService;
    @Autowired
    private LmsQuestionLearnService learnService;

    @GetMapping(value = {"", "/", "/index"})
    public String index(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<MenuNode> menus = menuService.getUserMenuNodes(Arrays.asList(Long.valueOf("1")), request);
        map.put("mold", 2);
        List<LmsTop> tops = topService.findList(map);
        model.addAttribute("menus", menus);
        model.addAttribute("tops", tops);
        map.clear();
        map.put("page", 1);
        map.put("limit", 3);
        map.put("enabled", 2);
        List<LmsExamRoom> rooms = this.roomService.findList(map);
        model.addAttribute("rooms", rooms);
        model.addAttribute("role", 5);
        return "front/index";
    }

    @GetMapping("/screen")
    @ResponseBody
    public Object getScreen(String id) {
        if (id.isEmpty()) {
            id = "0";
        }
        return AjaxResult.success(this.menuService.getScreen(id));
    }

    @GetMapping("/subjects")
    @ResponseBody
    public Object getSubject() {
        return AjaxResult.success(this.menuService.getSubjectsDropdown());
    }

    private void shiftSubject(Map<String, Object> map, Model model) {
        String pid = "", cid = "";
        if (map.containsKey("sid")) {
            String subjectId = map.get("sid").toString();
            String[] sids = subjectId.split("/");
            pid = sids[0];
            if (sids.length > 1) {
                cid = sids[1];
            }
            map.remove("sid");
            map.put("subjectId", subjectId);
        }
        model.addAttribute("pid", pid.equals("0") ? cid : pid);
        model.addAttribute("cid", cid);
    }

    @GetMapping("/rooms")
    public String rooms(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        String eid = "", mid = "";
        shiftSubject(map, model);
        if (map.containsKey("eid")) {
            eid = map.get("eid").toString();
            map.remove("eid");
            map.put("examMode", eid);
        }
        model.addAttribute("eid", eid);
        if (map.containsKey("mid")) {
            mid = map.get("mid").toString();
            map.remove("mid");
            map.put("markShowMode", mid);
        }
        model.addAttribute("mid", mid);
        model.addAttribute("title", map.get("title"));
        List<LmsExamRoom> rooms = this.roomService.findList(map);
        model.addAttribute("rooms", rooms);
        model.addAttribute("count", rooms.size());
        map.clear();
        map.put("enabled", 2);
        List<Map<String, Object>> screens = this.menuService.getScreen("0");
        model.addAttribute("screens", screens);
        return "front/rooms";
    }

    @GetMapping("/room")
    public String room(Model model, Long id) {
        LmsExamRoom room = this.roomService.get(id);
        model.addAttribute("room", room);
        return "front/room";
    }

    @GetMapping("/learn")
    public String learn(Model model, Long id) {
        LmsQuestionLearn learn = this.learnService.get(id);
        model.addAttribute("learn", learn);
        return "front/learn";
    }

    @GetMapping("/learns")
    public String learns(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        shiftSubject(map, model);
        model.addAttribute("title", map.get("title"));
        List<LmsQuestionLearn> learns = this.learnService.findList(map);
        model.addAttribute("learns", learns);
        model.addAttribute("count", learns.size());
        map.clear();
        map.put("enabled", 2);
        List<Map<String, Object>> screens = this.menuService.getScreen("0");
        model.addAttribute("screens", screens);
        return "front/learns";
    }
}