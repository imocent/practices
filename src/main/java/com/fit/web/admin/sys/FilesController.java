package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysFiles;
import com.fit.service.SysFilesService;
import com.fit.util.DateUtils;
import com.fit.util.WebUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 业务日志控制器
 * @Author AIM
 * @DATE 2019/4/26
 */
@Controller
@RequestMapping("/admin/file")
public class FilesController extends BaseController {

    private static String PREFIX = "/admin/sys/file/";

    @Autowired
    private SysFilesService service;

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
        List<SysFiles> list = service.findList(params);
        int count = service.findCount(params);
        return AjaxResult.tables(count, list);
    }

    /**
     * 上传文件入库
     */
    @ResponseBody
    @RequestMapping("/doUpload")
    public Object doUpload(MultipartFile file) {
        try {
            //获取文件的原始名
            String oldFilename = file.getOriginalFilename();
            //获取文件后缀 .pdf
            String extension = oldFilename.substring(oldFilename.lastIndexOf("."));
            //生成新的文件名
            String prefix = DateUtils.getDateShort() + file.getName();
            //创建临时文件
            File dir = new File(System.getProperty("user.dir") + String.format("/%s/%s/", uploadDir, DateUtils.getDateShort()));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File tmpFile = File.createTempFile(prefix, extension, dir);
            file.transferTo(tmpFile);
            //转换BASE64
            String b64Str = b64EncodeFile(tmpFile);
            SysFiles fileInfo = new SysFiles();
            fileInfo.setFileName(tmpFile.getName());
            fileInfo.setFileMold(getSuffix(extension));
            fileInfo.setFileData(b64Str);
            fileInfo.setCuser((Long) SecurityUtils.getSubject().getPrincipal());
            fileInfo.setCtime(new Date());
            this.service.save(fileInfo);
            return AjaxResult.success("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("上传失败");
        }
    }

    /**
     * 将文件转成base64 字符串
     */
    public static String b64EncodeFile(File file) throws IOException {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.getEncoder().encodeToString(buffer);
    }

    private int getSuffix(String extension) {
        if (extension == null || extension.isEmpty()) {
            return -1;
        }
        String imgPattern = "^(jpg|jpeg|png|gif|bmp|webp|tiff|svg|jfif|ico|heic|heif|avif|raw|cr2|nef)$";
        if (extension.toLowerCase().matches(imgPattern)) {
            return 0;
        } else {
            return 1;
        }
    }
}