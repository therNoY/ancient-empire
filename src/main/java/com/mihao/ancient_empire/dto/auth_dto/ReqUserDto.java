package com.mihao.ancient_empire.dto.auth_dto;

import com.mihao.ancient_empire.util.validation.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

public class ReqUserDto {
    @DecimalMin(value = "0", message = "身份过期，请重新登录")
    private Integer id;
    @NotBlank(message = "用户明不能为空")
    private String userName;
    @NotBlank(message = "密码不能为空")
    @Length(min = 5, message = "密码太短")
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
