package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.entity.UploadFileLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2021-01-06
 */
public interface UploadFileLogService extends IService<UploadFileLog> {

    /**
     * 保存文件上传日志
     * @param originalFilename
     * @param size
     * @return 文件ID
     */
    String saveLog(String originalFilename, long size);
}
