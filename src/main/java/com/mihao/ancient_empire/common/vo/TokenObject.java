package com.mihao.ancient_empire.common.vo;

// @Component
public class TokenObject {
    /**客户端id 保存登录的信息*/
    private String clientId;
    /**base64加密*/
    private String base64Secret;
    /**用户名*/
    private String name;
    /**到期时间*/
    private long expiresSecond;
    /**管理员名称*/
    private String userName;
    /**管理员id*/
    private Integer aId;
    /**职能*/
    private String role;
    /**项目名称*/
    private String project;
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getBase64Secret() {
        return base64Secret;
    }
    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getExpiresSecond() {
        return expiresSecond;
    }
    public void setExpiresSecond(long expiresSecond) {
        this.expiresSecond = expiresSecond;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Integer getaId() {
        return aId;
    }
    public void setaId(Integer aId) {
        this.aId = aId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
}