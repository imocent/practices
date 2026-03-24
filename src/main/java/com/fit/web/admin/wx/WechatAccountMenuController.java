package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.entity.WxAccountMenu;
import com.fit.service.WxAccountMenuService;
import com.fit.service.WxApiTokenService;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2026/2/26
 */
@Slf4j
@Controller
@RequestMapping("/admin/wx/menu")
public class WechatAccountMenuController {

    /**
     * 菜单类型-需要设置key的菜单类型
     **/
    public static final List<String> MENU_NEED_KEY = Arrays.asList("click", "scancode_push", "scancode_waitmsg", "pic_sysphoto", "pic_photo_or_album", "pic_weixin", "location_select");

    private static String PREFIX = "/admin/wx/accountMenu/";

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAccountMenuService service;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    public String index() {
        return PREFIX + "menu";
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("account", tokenService.getCurrentAccount());
        List<WxAccountMenu> menus = service.findList(map);
        return AjaxResult.success(WechatUtil.getMenuJson(menus));
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public AjaxResult save(String menus) {
        JSONArray jsons = JSONArray.parseArray(menus);
        //每次先行删除公众号所有菜单
        String currentAccount = tokenService.getCurrentAccount();
        Map<String, Object> param = new HashMap<>();
        param.put("account", currentAccount);
        this.service.deleteBySQL("delete from `wx_account_menu` where `account`=#{params.account}", param);
        if (!jsons.isEmpty()) {
            for (int i = 0; i < jsons.size(); i++) {
                JSONObject json = jsons.getJSONObject(i);
                if (null != json) {
                    WxAccountMenu accountMenu = new WxAccountMenu();
                    accountMenu.setAccount(currentAccount);
                    accountMenu.setName(json.getString("name"));
                    accountMenu.setSort(i + 1);
                    accountMenu.setParentId((long) 0);
                    if (json.containsKey("type")) {
                        accountMenu.setMtype(json.getString("type"));
                        //判断是否设置key
                        if (MENU_NEED_KEY.contains(json.getString("type"))) {
                            accountMenu.setEventType("fix");
                            accountMenu.setMsgType(json.getString("msgType"));
                            accountMenu.setMsgId(json.getString("msgId"));
                        }
                    }
                    if (json.containsKey("url")) {
                        accountMenu.setUrl(json.getString("url"));
                    }
                    if (json.containsKey("media_id")) {
                        accountMenu.setMsgId(json.getString("media_id"));
                    }
                    accountMenu.setCreateTime(new Date());
                    //保存
                    this.service.save(accountMenu);
                    //判断是否有sub_button
                    if (json.containsKey("sub_button")) {
                        JSONArray buttons = json.getJSONArray("sub_button");
                        if (!buttons.isEmpty()) {
                            long pid = accountMenu.getId();
                            for (int j = 0; j < buttons.size(); j++) {
                                json = buttons.getJSONObject(j);
                                accountMenu = new WxAccountMenu();
                                accountMenu.setAccount(currentAccount);
                                accountMenu.setParentId(pid);
                                accountMenu.setName(json.getString("name"));
                                accountMenu.setSort(j + 1);
                                if (json.containsKey("type")) {
                                    accountMenu.setMtype(json.getString("type"));
                                    //判断是否设置key
                                    if (MENU_NEED_KEY.contains(json.getString("type"))) {
                                        accountMenu.setEventType("fix");
                                        accountMenu.setMsgType(json.getString("msgType"));
                                        accountMenu.setMsgId(json.getString("msgId"));
                                    }
                                }
                                if (json.containsKey("url")) {
                                    accountMenu.setUrl(json.getString("url"));
                                }
                                if (json.containsKey("media_id")) {
                                    accountMenu.setMsgId(json.getString("media_id"));
                                }
                                accountMenu.setCreateTime(new Date());
                                this.service.save(accountMenu);
                            }
                        }
                    }
                }
            }
        }
        return AjaxResult.success();
    }
}