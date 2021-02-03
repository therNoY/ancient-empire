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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ApiRequestDTO{" +
                "userId=" + userId +
                '}';
    }
}
