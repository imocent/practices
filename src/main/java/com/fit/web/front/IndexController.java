package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.*;
import com.fit.service.*;
import com.fit.util.BeanUtil;
import com.fit.util.JsonRepair;
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
    private LmsExamSubjectService subjectService;
    @Autowired
    private MenuNodeService menuService;
    @Autowired
    private LmsTopService topService;
    @Autowired
    private LmsExamRoomService roomService;
    @Autowired
    private LmsQuestionService questionsService;
    @Autowired
    private LmsQuestionLearnService learnService;
    @Autowired
    private LmsQuestionUserService questionUserService;
    @Autowired
    private LmsQuestionUserAnswerService questionUserAnswerService;

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

    private String treeSubject(Long id) {
        LmsExamSubject subject = this.subjectService.get(id);
        if (subject == null) {
            return "";
        }
        if (subject.getPid() == 0) {// 如果是顶级节点，直接返回
            return subject.getId().toString();
        }
        // 递归获取父级路径，然后拼接当前ID
        return treeSubject(subject.getPid()) + "," + subject.getId();
    }

    private void shiftSubject(Map<String, Object> map, Model model) {
        String pid = "";
        if (map.containsKey("sid")) {
            String sid = map.get("sid").toString();
            if (sid.length() > 0) {
                String subjectId = treeSubject(Long.parseLong(sid));
                pid = subjectId.split(",")[0];
                map.put("subjectPid", subjectId);
            }
        }
        model.addAttribute("pid", pid);
    }

    @GetMapping("/rooms")
    public String rooms(HttpServletRequest request, Model model) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        shiftSubject(map, model);
        String eid = "", mid = "";
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
        model.addAttribute("role", 5);
        return "front/rooms";
    }

    @GetMapping("/room")
    public String room(Model model, Long id, Long quaid) {
        LmsExamRoom room = this.roomService.get(id);
        Map<String, Object> params = new HashMap<>();
        params.put("rid", id);
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT q.*, GROUP_CONCAT(CONCAT_WS('|', a.ID, a.CONTENT, a.VERIFY ) SEPARATOR ';') AS answer_info, '' AS checked");
        sb.append(" FROM lms_question q LEFT JOIN lms_question_answer a ON q.ID = a.QUESTION_ID");
        sb.append(" WHERE q.`EXAM_ROOM_ID` = #{params.rid} GROUP BY q.`ID` limit 10");
        List<Map<String, Object>> questions = this.questionsService.selectBySQL(sb.toString(), params);
        if (room.getSubjectSortMode()) {
            Collections.shuffle(questions);
        }
        String duration = "0";
        if (quaid != null) {
            Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
            if (userId != null) {
                LmsQuestionUser questionUser = this.questionUserService.get(quaid);
                duration = questionUser.getDuration();
                params.clear();
                params.put("quid", quaid);
                params.put("cuser", userId);
                List<LmsQuestionUserAnswer> list = this.questionUserAnswerService.findList(params);
                if (!list.isEmpty()) {
                    for (LmsQuestionUserAnswer questionUserAnswer : list) {
                        for (Map<String, Object> question : questions) {
                            if (question.get("ID").equals(questionUserAnswer.getQid())) {
                                question.put("checked", questionUserAnswer.getQoValue());
                            }
                        }
                    }
                }
            }
        }
        model.addAttribute("duration", duration);
        model.addAttribute("optSort", room.getSubjectOptSortMode());
        model.addAttribute("questions", questions);
        model.addAttribute("room", room);
        model.addAttribute("total", toInt(Math.ceil((double) questions.size() / 6)));
        return "front/room";
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

    @GetMapping("/learn")
    public String learn(Model model, Long id) {
        LmsQuestionLearn learn = this.learnService.get(id);
        model.addAttribute("learn", learn);
        return "front/learn";
    }

    @GetMapping("/tools")
    public String tools(HttpServletRequest request, Model model) {
        return "front/tools";
    }

    @GetMapping("/json/repair")
    @ResponseBody
    public Object json_repair(String json) {
        return AjaxResult.success(JsonRepair.getInstance(json));
    }
}