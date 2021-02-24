package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.core.manger.command.GameCommand;

/**
 * 游戏事件监听器
 * @Author mh32736
 * @Date 2021/2/23 9:46
 */
public interface GameMangerListener {

    /**
     * 添加准备发送的游戏命令 返回false 不添加
     * @param command
     * @return
     */
    boolean onGameCommandAdd(GameCommand command);

    /**
     * 处理单位可能升级
     * @param command
     */
    void onUnitLevelUp(GameCommand command);
}
