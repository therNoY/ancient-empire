package pers.mihao.ancient_empire.core.eums;

/**
 * 子状态机
 * @version 1.0
 * @auther mihao
 * @date 2020\10\28 0028 21:44
 */
public enum  SubStatusMachineEnum {
    /**
     * 初始状态
     */
    INIT,
    /**
     * 必须移动
     */
    MAST_MOVE,

    /**
     * 二次移动
     */
    SECOND_MOVE,
}
