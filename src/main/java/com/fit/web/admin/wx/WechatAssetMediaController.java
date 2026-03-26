package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.WxAccount;
import com.fit.entity.WxAssetMedia;
import com.fit.enums.MsgType;
import com.fit.enums.WechatAPI;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxAssetMediaService;
import com.fit.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
     * 添加编辑页面
     */
    @GetMapping("/view")
    public String view(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            WxAssetMedia bean = this.service.getByObjId(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "view";
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

    @GetMapping("/images")
    public String images() {
        return PREFIX + "images";
    }

    @GetMapping("/video")
    public String video() {
        return PREFIX + "video";
    }

    /**
     * 添加素材
     *
     * @param file
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/uploadAsset")
    public Object uploadAsset(MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("没有文件上传");
        }
        try {
            WxAssetMedia entity = new WxAssetMedia();
            entity.setRealname(file.getOriginalFilename());
            List<WxAssetMedia> list = this.service.findList(entity);
            if (list.isEmpty()) {
                entity = new WxAssetMedia();
                JSONObject saveFile = toSaveFile(file);
                entity.setAccount(tokenService.getCurrentAccount());
                entity.setCreateTime(new Date());
                entity.setFilename(saveFile.getString("filename"));
                entity.setMediaType(MsgType.getTypeByName(entity.getFilename()));
                entity.setRealname(saveFile.getString("realname"));
                entity.setSize(saveFile.getLong("size"));
                entity.setSuffix(saveFile.getString("suffix"));
                this.service.save(entity);
                return AjaxResult.success();
            } else {
                return AjaxResult.error("文件已存在");
            }
        } catch (NullPointerException e) {
            return AjaxResult.error("获取公众号信息失败");
        } catch (IOException e) {
            return AjaxResult.error("保存文件失败");
        }
    }

    /**
     * 添加永久素材(图片和视频)
     *
     * @param file
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/upload")
    public Object uploadFile(HttpServletRequest request, MultipartFile file, String type, String news) throws Exception {
        if (null == file) {
            return AjaxResult.error("没有文件上传");
        }
        JSONObject saveFile = toSaveFile(file);
        JSONObject fileObj = new JSONObject();
        fileObj.put("media", saveFile.getString("path"));
        fileObj.put("type", type);
        fileObj.put("filename", saveFile.getString("filename"));
        String token = tokenService.getCurrentToken();
        JSONObject call;
        if (news == null) {
            call = WechatUtil.apiPostCall(WechatAPI.ADD_MATERIAL.format(token, type), fileObj);
            if (call != null && call.containsKey("media_id")) {
                fileObj.clear();
                fileObj.put("filename", saveFile.getString("filename"));
                fileObj.put("realname", saveFile.getString("realname"));
                fileObj.put("size", saveFile.getLong("size"));
                fileObj.put("suffix", saveFile.getString("suffix"));
                fileObj.put("src", saveFile.getString("path"));
                fileObj.put("url", call.getString("url"));
                fileObj.put("mediaId", call.getString("media_id"));
                return AjaxResult.success(fileObj);
            }
        } else {
            call = WechatUtil.apiPostCall(WechatAPI.UPLOAD_MATERIAL_IMG.format(token), fileObj);
            if (call != null && call.containsKey("url")) {
                fileObj.clear();
                fileObj.put("url", call.getString("url"));
                return AjaxResult.success(fileObj);
            }
        }
        return AjaxResult.error("上传微信永久素材失败");
    }

    private JSONObject toSaveFile(MultipartFile file) throws IOException {
        JSONObject json = new JSONObject();
        if (!file.isEmpty()) {
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
            json.put("filename", fullFileName);
            json.put("realname", realName);  // 可选：返回原始文件名
            json.put("path", filePath + fullFileName);  // 相对路径，用于前端展示
            json.put("suffix", extension);
            json.put("size", file.getSize());
        }

        return json;
    }

    public String getExtension(String trueName) {
        if (trueName == null || trueName.lastIndexOf(".") == -1) {
            return ""; // 没有后缀名
        }
        return trueName.substring(trueName.lastIndexOf("."));
    }
}