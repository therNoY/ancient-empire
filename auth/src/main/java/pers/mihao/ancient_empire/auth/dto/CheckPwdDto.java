package pers.mihao.ancient_empire.auth.dto;

import javax.validation.constraints.NotBlank;

public class CheckPwdDto {
    private String userName;
    private String password;
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
