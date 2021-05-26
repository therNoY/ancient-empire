package pers.mihao.ancient_empire.auth.dto;

/**
 * @author mihao
 */
public class RespAuthDAO {

    private Integer userId;
    private String userName;
    private String password;
    private String token;

    public RespAuthDAO(String userName, String password, String token) {
        this.userName = userName;
        this.password = password;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
