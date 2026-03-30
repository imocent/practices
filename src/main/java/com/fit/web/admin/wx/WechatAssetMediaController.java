package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.WxAccount;
import com.fit.entity.WxAssetMedia;
import com.fit.enums.MsgType;
import com.fit.enums.WechatAPI;
import com.fit.service.WxAffairService;
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
    private WxAffairService affairService;
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
            bean.setMediaType(MsgType.getTypeByName(bean.getFileName()));
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
            affairService.syncDelMaterials(ids);
            this.service.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    @PostMapping("/syncMaterial")
    @ResponseBody
    public Object syncMaterial() {
        affairService.syncAllMaterials();
        return AjaxResult.success();
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
                entity.setFileName(saveFile.getString("fileName"));
                entity.setFileSize(saveFile.getLong("fileSize"));
                entity.setFileSuffix(saveFile.getString("fileSuffix"));
                entity.setFilePath(saveFile.getString("filePath"));
                entity.setRealname(saveFile.getString("realname"));
                entity.setMediaType(MsgType.getTypeByName(entity.getFileName()));
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
    public Object uploadFile(HttpServletRequest request, MultipartFile file) throws Exception {
        if (null == file) {
            return AjaxResult.error("没有文件上传");
        }
        Map<String, Object> map = WebUtil.getRequestMap(request);
        String type = map.get("type").toString();
        WxAssetMedia bean = this.service.queryByKey("wx_asset_media", "realname", file.getOriginalFilename());
        JSONObject fileObj = new JSONObject();
        if (null == bean) {
            JSONObject saveFile = toSaveFile(file);
            fileObj.put("filename", saveFile.getString("fileName"));
            fileObj.put("media", saveFile.getString("filePath"));
            fileObj.put("type", type);
            if (type.equals("video")) {
                JSONObject desc = new JSONObject();
                desc.put("title", map.get("title"));
                desc.put("introduction", map.get("introduction"));
                fileObj.put("description", desc);
            }
            bean = saveFile.toJavaObject(WxAssetMedia.class);
            bean.setAccount(tokenService.getCurrentAccount());
            bean.setCreateTime(new Date());
            bean.setMediaType(type);
            JSONObject call;
            String token = tokenService.getCurrentToken();
            if (map.containsKey("news")) {
                call = WechatUtil.apiPostCall(WechatAPI.MATERIAL_UPLOAD_IMG.format(token), fileObj);
                if (call != null && call.containsKey("url")) {
                    bean.setUrl(call.getString("url"));
                }
            } else {
                call = WechatUtil.apiPostCall(WechatAPI.MATERIAL_ADD.format(token, type), fileObj);
                if (call != null && call.containsKey("media_id")) {
                    bean.setMediaId(call.getString("media_id"));
                    bean.setUrl(saveFile.getString("filePath"));
                }
            }
            this.service.save(bean);
        }
        if (bean != null) {
            fileObj.clear();
            fileObj.put("filename", bean.getFileName());
            fileObj.put("size", bean.getFileSize());
            fileObj.put("suffix", bean.getFileSuffix());
            fileObj.put("src", bean.getFilePath());
            fileObj.put("realname", bean.getRealname());
            fileObj.put("url", bean.getUrl().isEmpty() ? bean.getFilePath() : bean.getUrl());
            fileObj.put("mediaId", bean.getMediaId());
            return AjaxResult.success(fileObj);
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
            json.put("fileName", fullFileName);
            json.put("filePath", filePath + fullFileName);  // 相对路径，用于前端展示
            json.put("fileSize", file.getSize());
            json.put("fileSuffix", extension);
            json.put("realname", realName);  // 可选：返回原始文件名
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