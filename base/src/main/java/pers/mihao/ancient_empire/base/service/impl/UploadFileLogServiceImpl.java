package pers.mihao.ancient_empire.base.service.impl;

import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.entity.UploadFileLog;
import pers.mihao.ancient_empire.base.dao.UploadFileLogDAO;
import pers.mihao.ancient_empire.base.service.UploadFileLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.util.StringUtil;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-01-06
 */
@Service
public class UploadFileLogServiceImpl extends ServiceImpl<UploadFileLogDAO, UploadFileLog> implements UploadFileLogService {

    @Override
    public String saveLog(String originalFilename, long size) {
        UploadFileLog uploadFileLog = new UploadFileLog();
        uploadFileLog.setFileSize((int) size);
        uploadFileLog.setFileRealName(originalFilename);
        uploadFileLog.setFileId(StringUtil.getUUID());
        uploadFileLog.setUploadUser(LoginUserHolder.getUserId());
        uploadFileLog.setUploadTime(LocalDateTime.now());
        save(uploadFileLog);
        return uploadFileLog.getFileId();
    }
}
