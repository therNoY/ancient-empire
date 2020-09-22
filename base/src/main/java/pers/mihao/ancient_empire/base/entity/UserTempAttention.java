package pers.mihao.ancient_empire.base.entity;

import java.io.Serializable;

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
    private Integer userId;

    /**
     * 模板ID
     */
    private Integer templateId;

    /**
     * 评价
     */
    private Integer templateStart;

    /**
     * 评论
     */
    private String templateComment;

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
