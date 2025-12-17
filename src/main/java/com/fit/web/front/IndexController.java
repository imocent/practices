package com.fit.web.front;

import com.fit.base.BaseController;
import com.fit.entity.LmsExamRoom;
import com.fit.entity.LmsTop;
import com.fit.entity.MenuNode;
import com.fit.service.LmsExamRoomService;
import com.fit.service.LmsTopService;
import com.fit.service.MenuNodeService;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/rooms")
    public String rooms(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<MenuNode> menus = menuService.getUserMenuNodes(Arrays.asList(Long.valueOf("1")), request);
        map.put("mold", 2);
        List<LmsTop> tops = topService.findList(map);
        model.addAttribute("menus", menus);
        model.addAttribute("tops", tops);
        map.clear();
        map.put("page", toInt(request.getParameter("pageNumber")));
        map.put("limit", 12);
        map.put("enabled", 2);
        List<LmsExamRoom> rooms = this.roomService.findList(map);
        model.addAttribute("rooms", rooms);
        return "front/rooms";
    }

    @GetMapping("/detail")
    public String detail(Model model, Long id) {
        LmsExamRoom room = this.roomService.get(id);
        model.addAttribute("room", room);
        return "front/detail";
    }
}