package pers.mihao.ancient_empire.common.dto;

import javax.validation.constraints.NotBlank;
import pers.mihao.ancient_empire.common.annotation.LogField;

public class LoginDTO {

    @NotBlank(message = "用户名/邮箱不能为空")
    @LogField
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 免密
     */
    private String fromApp;

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

    public String getFromApp() {
        return fromApp;
    }

    public void setFromApp(String fromApp) {
        this.fromApp = fromApp;
    }
}
