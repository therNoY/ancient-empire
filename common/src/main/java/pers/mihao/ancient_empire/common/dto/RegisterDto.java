package pers.mihao.ancient_empire.common.dto;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import pers.mihao.ancient_empire.common.annotation.Length;

public class RegisterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String userName;
    @Email(message = "邮箱格式错误")
    private String email;
    @NotBlank(message = "密码不能为空")
    @Length(min = 5, message = "密码长度不能低于5位")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
