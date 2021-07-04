package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * @Author mihao
 * @Date 2021/4/6 11:32
 */
public class ShowDialogCommand extends AbstractCommand{

    private String dialogType;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDialogType() {
        return dialogType;
    }

    public void setDialogType(String dialogType) {
        this.dialogType = dialogType;
    }
}
