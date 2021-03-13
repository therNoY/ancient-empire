package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * 后端发出的命令
 * @Author mh32736
 * @Date 2020/9/10 17:42
 */
public interface Command {

    /**
     * 是否有序
     * @return
     */
    Integer getOrder();

    /**
     * 是否同步
     * @return
     */
    Boolean isAsync();


    SendTypeEnum getSendTypeEnum();

}
