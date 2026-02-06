package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.*;
import com.fit.service.*;
import com.fit.util.JsonRepair;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/tools")
    public String tools() {
        return "front/tools";
    }

    @GetMapping("/json/repair")
    @ResponseBody
    public Object json_repair(String json) {
        return AjaxResult.success(JsonRepair.getInstance(json));
    }
}