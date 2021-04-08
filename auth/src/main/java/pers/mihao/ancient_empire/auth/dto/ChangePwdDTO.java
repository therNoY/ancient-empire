package pers.mihao.ancient_empire.auth.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

import javax.validation.constraints.NotBlank;


public class ChangePwdDTO extends ApiRequestDTO {
    @NotBlank(message = "确认密码不能为空")
    private String surePassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    @NotBlank(message = "原密码不能为空")
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
