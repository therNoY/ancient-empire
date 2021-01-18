package pers.mihao.ancient_empire.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pers.mihao.ancient_empire.base.service.UploadFileLogService;
import pers.mihao.ancient_empire.base.util.UploadPathUtil;
import pers.mihao.ancient_empire.common.util.FileUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController
public class UploadFileController {

    Logger log = LoggerFactory.getLogger(this.toString());

    @Autowired
    UploadFileLogService uploadFileLogService;

    @PostMapping("/uploadTemplateImg")
    public RespJson singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("template_id") String templateId) {

        if (StringUtil.isBlack(templateId)) {
            return RespUtil.error("模板Id不能为空");
        }

        // 验证是否是图片
        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.info("", e);
            return RespUtil.error("上传的文件不符合");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        if (width != 24 || height != 24) {
            return RespUtil.error("图片的宽或者高度需要是24px");
        }

        try {
            String fileName = uploadFileLogService.saveLog(file.getOriginalFilename(), file.getSize()) + ".jpg";
            byte[] bytes = file.getBytes();
            FileUtil.createFile(UploadPathUtil.getUploadTemplatePath(templateId) + fileName, bytes);
            return RespUtil.successResJson(templateId + File.separator + fileName);
        } catch (IOException e) {
            log.error("", e);
            return RespUtil.error("上传失败，请联系管理员");
        }
    }


}