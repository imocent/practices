package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsExamRoom;
import com.fit.entity.LmsTop;
import com.fit.entity.MenuNode;
import com.fit.entity.SysUser;
import com.fit.service.LmsExamRoomService;
import com.fit.service.LmsTopService;
import com.fit.service.MenuNodeService;
import com.fit.service.SysUserService;
import com.fit.util.JsonRepair;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Autowired
    private SysUserService userService;


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
        Long rid = 5L;
        if (SecurityUtils.getSubject().isAuthenticated()) {
            Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
            SysUser sysUser = this.userService.get(userId);
            rid = sysUser.getRid();
        }
        model.addAttribute("role", rid);
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