package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * @Author mh32736
 * @Date 2021/4/6 11:32
 */
public class ShowDialogCommand extends AbstractCommand{

    private DialogEnum dialogType;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DialogEnum getDialogType() {
        return dialogType;
    }

    public void setDialogType(DialogEnum dialogType) {
        this.dialogType = dialogType;
    }
}
