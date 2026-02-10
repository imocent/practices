package com.fit.web.api;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/2/10
 */
@Slf4j
@Controller
@RequestMapping("/api")
public class ApiController extends BaseController {

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult apiLogin(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        log.debug(map.toString());
        return AjaxResult.success();
    }
}
