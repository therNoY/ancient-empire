package pers.mihao.ancient_empire.core.eums;

/**
 * 后端通知前端渲染的命令枚举
 * @Author mh32736
 * @Date 2020/9/10 17:27
 */
public enum GameCommendEnum {
    /**
     * 展示行动
     */
    SHOW_ACTION,

    /**
     * 展示移动区域
     */
    SHOW_MOVE_AREA,

    /**
     * 展示移动路线
     */
    SHOW_MOVE_LINE,

    /**
     * 单位移动
     */
    UNIT_MOVE,

    /**
     * 展示攻击区域
     */
    SHOW_ATTACH_AREA,

    /**
     *
     * 移动攻击指针
     */
    MOVE_ATTACH_POINT,

    /**
     * 移动指针
     * 修改地图展示，修改单位展示
     */
    MOVE_POINT,

    /**
     * 单位攻击的动画
     */
    ATTACH_ACTIVE,

    /**
     * 单位被攻击动画
     */
    BE_ATTACH_ACTIVE,

    /**
     * 改变单位状态
     */
    CHANGE_UNIT_STATUS,

    /**
     * 单位死亡 （冒烟+出现坟墓）
     */
    UNIT_DEAD,

    /**
     * 添加新单位
     */
    ADD_UNIT,

    /**
     * 改变地形
     */
    CHANG_REGION,

    /**
     * 局内信息通知
     */
    SHOW_GAME_NEWS,

    /**
     * 系统信息通知
     */
    SHOW_SYSTEM_NEWS,

    /**
     * 召唤动作
     */
    BE_SUMMON;

}
