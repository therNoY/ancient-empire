package pers.mihao.ancient_empire.auth.dto;

import pers.mihao.ancient_empire.common.annotation.Length;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

public class ReqUserDto extends ApiRequestDTO {
    private String userName;
    @Length(min = 5, message = "密码太短")
    private String password;

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
