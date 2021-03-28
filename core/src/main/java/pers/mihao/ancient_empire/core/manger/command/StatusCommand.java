package pers.mihao.ancient_empire.core.manger.command;

/**
 * 状态变化command
 * @version 1.0
 * @auther mihao
 * @date 2021\3\23 0023 21:40
 */
public class StatusCommand extends AbstractCommand{

    public static final String SUCCESS = "200";

    private String openStatus;

    private String openId;

    private String message;

    public StatusCommand() {
        this.openStatus = SUCCESS;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
