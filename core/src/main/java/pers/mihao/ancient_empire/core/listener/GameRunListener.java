package pers.mihao.ancient_empire.core.listener;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;
import pers.mihao.ancient_empire.core.robot.ActionIntention;

/**
 * 游戏运行时的监听
 * @Author mihao
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
     * 回合开始钩子
     * @param currArmy
     */
    void beforeRoundStart(Army currArmy);

    /**
     * 回合结束 当前军队回合结束
     * @param army
     */
    void onRoundEnd(Army army);

    /**
     * 有单位死亡
     * @param armyIndex
     * @param unitInfo
     */
    void onUnitDead(Integer armyIndex, UnitInfo unitInfo);

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
     * @param unitStatusInfoDTOS
     * @param stream
     */
    void onUnitStatusChange(List<UnitStatusInfoDTO> unitStatusInfoDTOS, Stream stream);

    /**
     * 过滤可以购买的单位
     * @param respUnitMes
     * @return
     */
    List<UnitInfo> filterUnit(List<UnitInfo> respUnitMes);

    /**
     * 单位占领地形
     * @param currUnit
     * @param region
     */
    void onOccupied(UnitInfo currUnit, Region region);


    /**
     * 监听机器人的选择行动
     * @param unitInfo
     * @param actionList
     * @return
     */
    ActionIntention chooseAction(UnitInfo unitInfo, List<ActionIntention> actionList);
}
