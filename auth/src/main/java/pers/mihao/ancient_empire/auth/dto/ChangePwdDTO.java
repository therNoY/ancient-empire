package pers.mihao.ancient_empire.auth.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

import javax.validation.constraints.NotBlank;


/**
 * 修改密码
 */
public class ChangePwdDTO extends ApiRequestDTO {

    @NotBlank(message = "确认密码")
    private String surePassword;
    @NotBlank(message = "新密码")
    private String newPassword;
    @NotBlank(message = "原密码")
    private String oldPassword;

    public String getSurePassword() {
        return surePassword;
    }

    public void setSurePassword(String surePassword) {
        this.surePassword = surePassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
