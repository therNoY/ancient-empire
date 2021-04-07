package pers.mihao.ancient_empire.core.listener;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;

/**
 * 游戏运行时的监听
 * @Author mh32736
 * @Date 2021/3/31 20:38
 */
public interface GameRunListener {

    /**
     * 游戏开始
     */
    void onGameStart();

    /**
     * 点击提示
     */
    void onClickTip();

    /**
     * 回合开始
     * @param army
     */
    void onRoundStart(Army army);

    /**
     * 有单位死亡
     * @param unitInfo
     */
    void onUnitDead(UnitInfo unitInfo);

    /**
     * 单位结束移动
     * @param unitInfo
     */
    void onUnitDone(UnitInfo unitInfo);

    /**
     * 添加准备发送的游戏命令 返回false 不添加
     * @param command
     * @return
     */
    boolean onGameCommandAdd(GameCommand command);

    /**
     * 处理单位可能升级
     * @param command
     * @param stream
     */
    void onUnitLevelUp(GameCommand command, Stream stream);

}
