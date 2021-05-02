package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2021-01-06
 */
public class UploadFileLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private String fileId;

    /**
     * 文件真实名字
     */
    private String fileRealName;

    /**
     * 模板Id
     */
    private Integer tempId;

    /**
     * 上传人
     */
    private Integer uploadUser;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 文件大小
     */
    private Integer fileSize;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }
    public Integer getTempId() {
        return tempId;
    }

    public void setTempId(Integer tempId) {
        this.tempId = tempId;
    }
    public Integer getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(Integer uploadUser) {
        this.uploadUser = uploadUser;
    }
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "UploadFileLog{" +
        "fileId=" + fileId +
        ", fileRealName=" + fileRealName +
        ", tempId=" + tempId +
        ", uploadUser=" + uploadUser +
        ", uploadTime=" + uploadTime +
        ", fileSize=" + fileSize +
        "}";
    }
}
