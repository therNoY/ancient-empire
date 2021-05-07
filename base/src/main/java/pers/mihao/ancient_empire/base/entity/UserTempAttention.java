package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public class UserTempAttention implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private Integer userId;

    /**
     * 模板ID
     */
    @TableId
    private Integer templateId;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 评价
     */
    private Integer templateStart;

    /**
     * 评论
     */
    private String templateComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }
    public Integer getTemplateStart() {
        return templateStart;
    }

    public void setTemplateStart(Integer templateStart) {
        this.templateStart = templateStart;
    }
    public String getTemplateComment() {
        return templateComment;
    }

    public void setTemplateComment(String templateComment) {
        this.templateComment = templateComment;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    @Override
    public String toString() {
        return "UserTempAttention{" +
        "userId=" + userId +
        ", templateId=" + templateId +
        ", templateStart=" + templateStart +
        ", templateComment=" + templateComment +
        "}";
    }
}
