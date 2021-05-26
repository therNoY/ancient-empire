package pers.mihao.ancient_empire.common.dto;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import pers.mihao.ancient_empire.common.annotation.Length;

public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phone;

    @NotBlank
    private String userName;
    @Email
    private String email;
    @NotBlank
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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
