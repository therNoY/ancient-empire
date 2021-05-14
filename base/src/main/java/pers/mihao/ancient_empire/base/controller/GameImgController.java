package pers.mihao.ancient_empire.base.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pers.mihao.ancient_empire.base.dto.CreateUnitImgDTO;
import pers.mihao.ancient_empire.base.dto.NewUnitImgDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UploadFileLogService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.FileUtil;
import pers.mihao.ancient_empire.common.util.ImgUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

/**
 * <p>
 * 控制文件上传
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Controller
public class GameImgController {

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
    private static final String TEMPLATE_PATH = "template";

    /**
     * 临时单位文件
     */
    private static final String TEMPORARY_PATH = "temporary";
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
     * 新加的单位 id -1
     */
    public static final String NO_UNIT = "-1";

    /**
     * 属性文件的前缀
     */
    public static final String COLOR_PREFIX = "unit.color.";

    /**
     * 临时文件前缀
     */
    public static final String TEMPORARY_FILE_PREFIX = "t_";

    /**
     * 上传文件 可以上传到模板
     *
     * @param file
     * @param id   模板Id或者单位Id
     * @return
     */
    @ResponseBody
    @RequestMapping("/upload/{type}/{id}")
    public String singleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String id,
        @PathVariable String type) {

        // 验证是否是图片
        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.info("", e);
            throw new AeException("上传的文件不符合");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        if (width != IMG_SIZE || height != IMG_SIZE) {
            throw new AeException("图片的宽或者高度需要是" + IMG_SIZE + "px");
        }

        try {
            String fileId = uploadFileLogService.saveLog(file.getOriginalFilename(), file.getSize());
            String fileName = fileId + FILE_SUFFIX;
            byte[] bytes = file.getBytes();

            // 获取文件保存的文件
            if (type.equals(TEMPLATE_PATH)) {
                // 保存模板文件
                String fileRealPath = StringUtil
                    .joinWith(File.separator, filePathHead, TEMPLATE_PATH, id, fileName);
                FileUtil.createFile(fileRealPath, bytes);
                return id + File.separator + fileName;

            } else if (type.equals(UNIT_PATH)) {
                // 保存的是单位文件
                if (NO_UNIT.equals(id)) {
                    // 保存模板文件
                    String fileRealPath = StringUtil
                        .joinWith(File.separator, filePathHead, UNIT_PATH, TEMPORARY_PATH, fileName);
                    FileUtil.createFile(fileRealPath, bytes);
                    return fileId;
                }
                return null;
            }
            return null;
        } catch (IOException e) {
            log.error("", e);
            throw new AeException("上传失败，请联系管理员");
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
        if (type.equals(UNIT_PATH) && !fileName.startsWith(TEMPORARY_FILE_PREFIX) && !id.equals(TEMPORARY_PATH)) {
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

    @RequestMapping("/api/unitMes/img/create")
    @ResponseBody
    public NewUnitImgDTO createUnitImg(@RequestBody CreateUnitImgDTO createUnitImgDTO) {
        List<String> imgList = createUnitImgDTO.getImgList();
        // 验证图片正确
        verificationImg(imgList);

        NewUnitImgDTO newUnitImgDTO = new NewUnitImgDTO();

        String img1 = imgList.get(0);
        String img2 = imgList.get(1);

        String img1Id = createOtherColorUnitImg(img1 + FILE_SUFFIX);
        String img2Id = createOtherColorUnitImg(img2 + FILE_SUFFIX);

        newUnitImgDTO.setImg1(img1Id.split("\\.")[0]);
        newUnitImgDTO.setImg2(img2Id.split("\\.")[0]);

        return newUnitImgDTO;
    }

    /**
     * 验证图片是否正常
     *
     * @param imgList
     */
    private void verificationImg(List<String> imgList) {

    }

    /**
     * 根据图片创建其他颜色的图片
     *
     * @param img1
     * @return
     */
    private String createOtherColorUnitImg(String img1) {
        // 新的临时文件Id加上标识便于清理垃圾文件
        String tempFileId = TEMPORARY_FILE_PREFIX + img1;
        // 文件真实地址
        String fileRealPath = StringUtil
            .joinWith(File.separator, filePathHead, UNIT_PATH, TEMPORARY_PATH, img1);
        String newFilePath;
        File imgFile = new File(fileRealPath);
        // 原本的文件
        ColorEnum baseColor = ColorEnum.BLUE;
        Collection<String> baseColorValue = getColorValue(baseColor.type());
        Collection<String> replaceColorValue;
        for (ColorEnum c : ColorEnum.values()) {
            newFilePath = StringUtil
                .joinWith(File.separator, filePathHead, UNIT_PATH, c.type(), tempFileId);
            if (!c.equals(baseColor)) {
                // 先替换颜色 生成新的文件夹
                replaceColorValue = getColorValue(c.type());
                try {
                    ImgUtil.replaceColor(baseColorValue, replaceColorValue, imgFile, newFilePath);
                } catch (IOException e) {
                    log.error("", e);
                    throw new AeException("复制文件错误");
                }
            } else {
                // 直接复制文件
                try {
                    if (Files.exists(Paths.get(newFilePath))) {
                        Files.delete(Paths.get(newFilePath));
                    }
                    Files.copy(Paths.get(fileRealPath), Paths.get(newFilePath));
                } catch (IOException e) {
                    log.error("", e);
                    throw new AeException("文件复制错误");
                }
            }
        }

        return tempFileId;
    }

    /**
     * 根据资源Id和缓存时间判断资源是否过期
     *
     * @param catchTime
     * @return
     */
    private static boolean isNotExpired(String catchTime, String catchKey) {
        return true;
    }

    /**
     * 从配置文件中获取颜色的值
     *
     * @param color
     * @return
     */
    private Collection<String> getColorValue(String color) {
        Map<String, String> value = AppConfig.getMapByPrefix(COLOR_PREFIX + color);
        return value.values();
    }

    public void delTempFile() {
        log.info("删除文件开始===");
        File file = new File(StringUtil.joinWith(File.separator, filePathHead, UNIT_PATH, TEMPORARY_PATH));
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                f.deleteOnExit();
                log.info("删除文件{}", f.getAbsolutePath());
            }
        }

        for (ColorEnum colorEnum : ColorEnum.values()) {
            File unitFilePath = new File(
                StringUtil.joinWith(File.separator, filePathHead, UNIT_PATH, colorEnum.type()));

            if (unitFilePath.isDirectory()) {
                for (File unitF : unitFilePath.listFiles()) {
                    if (unitF.getName().startsWith(TEMPORARY_FILE_PREFIX)) {
                        unitF.deleteOnExit();
                        log.info("删除文件{}", unitF.getAbsolutePath());
                    }
                }
            }

        }

    }

    /**
     * 将单位的图片名字转成ID
     *
     * @param newUploadImg
     * @param id
     */
    public void moveUnitImgName2Id(NewUnitImgDTO newUploadImg, Integer id) throws IOException {

        File img1, img2;
        for (ColorEnum color : ColorEnum.values()) {
            img1 = new File(StringUtil
                .joinWith(File.separator, filePathHead, UNIT_PATH, color.type(), newUploadImg.getImg1() + FILE_SUFFIX));

            img2 = new File(StringUtil
                .joinWith(File.separator, filePathHead, UNIT_PATH, color.type(), newUploadImg.getImg2() + FILE_SUFFIX));

            if (!img1.exists() || !img2.exists()) {
                throw new AeException("图片资源不存在" + newUploadImg);
            }

            // 设置新的文件名字
            Files.copy(Paths.get(img1.getAbsolutePath()),
                Paths.get(img1.getParent() + File.separator + id + FILE_SUFFIX));
            Files.copy(Paths.get(img1.getAbsolutePath()),
                Paths.get(img1.getParent() + File.separator + id + "_2" + FILE_SUFFIX));

            // 生成结束图片
            ImgUtil.changeImg2Bw(img2, img1.getParent() + File.separator + id + "_3" + FILE_SUFFIX);
        }
    }
}