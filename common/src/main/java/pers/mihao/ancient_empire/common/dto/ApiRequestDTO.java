package pers.mihao.ancient_empire.common.dto;

import java.io.Serializable;

/**
 * 基础请求类
 * @version 1.0
 * @author mihao
 * @date 2021\1\11 0011 21:18
 */
public class ApiRequestDTO implements Serializable {

    private Integer userId;

    private String language;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "ApiRequestDTO{" +
                "userId=" + userId +
                '}';
    }
}
