package pers.mihao.ancient_empire.base.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UploadFileLogService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.FileUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 控制文件上传
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Controller
public class FileUploadDownloadController {

    Logger log = LoggerFactory.getLogger(this.toString());

    @Autowired
    UploadFileLogService uploadFileLogService;

    @Autowired
    UnitMesService unitMesService;

    @Value("${file.img.upload.path}")
    String filePathHead;

    /**
     * 模板的文件夹
     */
    private static final String TEMP_PATH = "template";
    /**
     * 单位的路径
     */
    private static final String UNIT_PATH = "unit";
    /**
     * 文件格式
     */
    private static final String FILE_SUFFIX = ".png";

    /**
     * 标准文件格式
     */
    private static final int IMG_SIZE = 24;

    /**
     * 默认为10K
     */
    private static int bufferSize = 10240;

    /**
     * 上传文件 可以上传到模板
     *
     * @param file
     * @param id 模板Id或者单位Id
     * @return
     */
    @ResponseBody
    @RequestMapping("/upload/{type}/{id}")
    public RespJson singleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String id, @PathVariable String type) {

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
        if (width != IMG_SIZE || height != IMG_SIZE) {
            return RespUtil.error("图片的宽或者高度需要是" + IMG_SIZE + "px");
        }

        try {
            String fileName = uploadFileLogService.saveLog(file.getOriginalFilename(), file.getSize()) + FILE_SUFFIX;
            byte[] bytes = file.getBytes();

            // 获取文件保存的文件
            if (type.equals(TEMP_PATH)) {
                // 保存模板文件
                String fileRealPath = StringUtil
                    .joinWith(File.separator, filePathHead, TEMP_PATH, id, fileName);
                FileUtil.createFile(fileRealPath, bytes);
                return RespUtil.successResJson(id + File.separator + fileName);

            } else if (type.equals(UNIT_PATH)) {
                // TODO 保存单位文件
                return null;
            }
            return null;
        } catch (IOException e) {
            log.error("", e);
            return RespUtil.error("上传失败，请联系管理员");
        }
    }

    /**
     * 根据路径和文件名字下载文件
     *
     * @param type
     * @param id
     * @param fileName
     * @param response
     */
    @RequestMapping("/img/ae/{type}/{id}/{fileName}")
    public void downloadFile(@PathVariable String type, @PathVariable String fileName, @PathVariable String id,
        HttpServletResponse response, HttpServletRequest request) {

        response.setContentType("image/png");
        // 缓存的key
        String catchKey = id + File.separator + fileName;
        String catchTime;
        if (StringUtil.isNotBlack(catchTime = request.getHeader("If-Modified-Since"))) {
            if (isNotExpired(catchTime, catchKey) && catchKey.equals(request.getHeader("If-None-Match"))) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        String fileRealPath, fileRealName;
        if (type.equals(UNIT_PATH)) {
            // 获取单位的图片根据Id获取
            String realName = fileName.split("\\.")[0];
            String fileNameSuffix = null, unitId;
            if (realName.contains(CommonConstant.UNDER_LINE)) {
                fileNameSuffix = CommonConstant.UNDER_LINE + realName.split(CommonConstant.UNDER_LINE)[1];
                unitId = realName.split(CommonConstant.UNDER_LINE)[0];
            } else {
                unitId = realName;
            }
            UnitMes unitMes = unitMesService.getUnitMesById(Integer.valueOf(unitId));
            fileRealName = fileNameSuffix == null ? unitMes.getImgIndex() : unitMes.getImgIndex() + fileNameSuffix;
            fileRealName = fileRealName + FILE_SUFFIX;
        } else {
            fileRealName = fileName;
        }
        fileRealPath = StringUtil.joinWith(File.separator, filePathHead, type, id, fileRealName);
        File file = new File(fileRealPath);
        if (file.exists()) {
            try {
                returnFile(response, file);
            } catch (IOException e) {
                log.error("返回文件异常", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    /**
     * 请求http返回文件 会使用前端缓存
     *
     * @param response
     * @param file
     * @throws IOException
     */
    @KnowledgePoint("Http请求资源的前端缓存的使用")
    private void returnFile(HttpServletResponse response, File file)
        throws IOException {

        // 缓存的key
        String catchKey = file.getParentFile().getName() + File.separator + file.getName();

        try (
            FileInputStream fis = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(fis)) {
            long totalSize = fis.available();

            response.addHeader("Content-Disposition", "inline;" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.addHeader("Content-Length", String.valueOf(totalSize));
            response.addHeader("Pragma", "none");
            response.setHeader("Last-Modified", DateUtil.getDataTime());
            response.setHeader("ETag", catchKey);
            response.setStatus(HttpServletResponse.SC_OK);
            byte[] temp = new byte[bufferSize];
            int size;
            while ((size = in.read(temp)) != -1) {
                outputStream.write(temp, 0, size);
            }
            outputStream.flush();
        } finally {
        }
    }

    /**
     * 根据资源Id和缓存时间判断资源是否过期
     * @param catchTime
     * @return
     */
    private static boolean isNotExpired(String catchTime, String catchKey) {
        return true;
    }
}