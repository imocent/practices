package com.fit.web.admin.wx;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.WxAssetMedia;
import com.fit.enums.MsgType;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxAssetMediaService;
import com.fit.util.BeanUtil;
import com.fit.util.DateUtils;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 图片、语音和视频控制器
 */
@Controller
@RequestMapping("/admin/wx/media")
public class WechatAssetMediaController extends BaseController {

    private static String PREFIX = "/admin/wx/assetMedia/";

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAssetMediaService service;

    /**
     * 列表页面
     */
    @GetMapping("/list")
    public String index() {
        return PREFIX + "list";
    }

    /**
     * 查询列表
     */
    @PostMapping("/list")
    @ResponseBody
    public AjaxResult list(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<WxAssetMedia> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(WxAssetMedia bean) {
        WxAssetMedia entity = this.service.getByObjId(bean.getId());
        if (null == entity) {
            bean.setMediaType(MsgType.getTypeByName(bean.getFilename()));
            bean.setAccount(tokenService.getCurrentAccount());
            bean.setCreateTime(new Date());
            this.service.save(bean);
        } else {
            BeanUtil.copyProperties(bean, entity);
            bean.setUpdateTime(new Date());
            this.service.update(entity);
        }
        return AjaxResult.success();
    }

    /**
     * 删除
     *
     * @param ids 删除ID集合
     */
    @PostMapping("/del")
    @ResponseBody
    public Object del(@RequestParam("ids") List<Long> ids) {
        if (OftenUtil.isNotEmpty(ids)) {
            this.service.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    public String getExtension(String trueName) {
        if (trueName == null || trueName.lastIndexOf(".") == -1) {
            return ""; // 没有后缀名
        }
        return trueName.substring(trueName.lastIndexOf("."));
    }

    @GetMapping("/images")
    public String images() {
        return PREFIX + "images";
    }

    @GetMapping("/video")
    public String video() {
        return PREFIX + "video";
    }

    /**
     * 添加语音\图片\缩略图素材
     *
     * @param file
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/upload")
    public AjaxResult uploadFile(HttpServletRequest request, MultipartFile file) throws Exception {
        if (null == file) {
            return AjaxResult.error("没有文件上传");
        }
        // 原文件名称
        String realName = file.getOriginalFilename();
        // 文件后缀名（带点号，如 .jpg、.mp4）
        String extension = getExtension(realName); // 假设这个方法返回 ".jpg" 格式
        // 系统生成的文件名（不含扩展名）
        String fileNameWithoutExt = String.format("%s_%s", extension.replace(".", ""), System.currentTimeMillis());
        // 完整的文件名（含扩展名）
        String fullFileName = fileNameWithoutExt + extension;
        // 文件上传路径（相对于项目根目录）
        String filePath = String.format("%s/%s/", uploadDir, DateUtils.getDateShort());
        // 创建目录
        File dir = new File(System.getProperty("user.dir") + filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 目标文件
        File targetFile = new File(dir, fullFileName);
        file.transferTo(targetFile);
        String url = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return AjaxResult.success().put("src", filePath + fullFileName)  // 相对路径，用于前端展示
                .put("url", url + filePath + fullFileName).put("suffix", extension)  // 绝对路径，用于后端操作
                .put("filename", fullFileName).put("size", file.getSize()).put("realname", realName);  // 可选：返回原始文件名
    }
}