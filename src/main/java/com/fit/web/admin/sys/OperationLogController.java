package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.entity.SysOperationLog;
import com.fit.service.SysOperationLogService;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 业务日志控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/log")
public class OperationLogController {

    private static String PREFIX = "/admin/sys/log/";

    @Autowired
    private SysOperationLogService logService;

    /**
     * 列表页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String index() {
        return PREFIX + "list";
    }

    /**
     * 查询列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Object list(HttpServletRequest request) {
        Map<String, Object> params = WebUtil.getRequestMap(request);
        List<SysOperationLog> list = logService.findList(params);
        int count = logService.findCount(params);
        return AjaxResult.tables(count, list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public Object seeDetails(Long id) {
        if (OftenUtil.isNotEmpty(id)) {
            SysOperationLog operationLog = logService.get(id);
            return AjaxResult.success(operationLog);
        }
        return AjaxResult.error("参数异常");
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public Object clearLog() {
        logService.deleteTable();
        return AjaxResult.success();
    }
}