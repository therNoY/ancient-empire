package pers.mihao.ancient_empire.auth.dto;

import java.io.Serializable;

/**
 * @Author mihao
 * @Date 2021/5/23 11:19
 */
public class WeChatSourceInfoDTO implements Serializable {

    /**
     * 手机号加密
     */
    private String encryptedData;

    private String iv;

    /**
     * 微信登录code
     */
    private String code;

    /**
     * 用户信息加密数据
     */
    private String userInfoEncrypted;

    private String userInfoIv;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserInfoEncrypted() {
        return userInfoEncrypted;
    }

    public void setUserInfoEncrypted(String userInfoEncrypted) {
        this.userInfoEncrypted = userInfoEncrypted;
    }

    public String getUserInfoIv() {
        return userInfoIv;
    }

    public void setUserInfoIv(String userInfoIv) {
        this.userInfoIv = userInfoIv;
    }
}
