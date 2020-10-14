package pers.mihao.ancient_empire.core.manger.handler;


import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 处理点击不能行动的单位
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\9\29 0029 22:36
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_UN_ACTIVE_UNIT}
 */
public class ClickUnActiveUnitHandler extends CommonHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理点击不能行动的单位
     *
     * @param gameEvent
     */
    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        if (stateIn(StatusMachineEnum.WILL_ATTACH)) {
            // 如果此时状态是准备攻击 判断是否是准备攻击单位
            if (siteInArea(gameEvent.getInitiateSite(), gameContext.getWillAttachArea())) {
                Pair<Integer, Unit> pair = getUnitFromMapBySite(gameEvent.getInitiateSite());
                if (!pair.getValue().isDone() && !currArmy().getCamp().equals(record().getArmyList().get(pair.getKey()).getCamp())) {
                    // 点击在攻击范围内的敌方单位 展示攻击确定圈
                    commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ATTACH_POINT, gameEvent.getInitiateSite());
                    gameContext.setBeAttachUnit(pair.getValue());
                }
            } else {
                // 点击其他区域的单位就返回
                commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ACTION, ExtMes.ACTIONS, gameContext.getActions());
                gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            }
        }else if (stateIn(StatusMachineEnum.MOVE_DONE)) {
            // 点击其他区域的单位就返回
            gameContext.setStatusMachine(StatusMachineEnum.NO_CHOOSE);
            commandStream().toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite(), getCurrUnitIndex());
        }else {
            changeCurrPoint(gameEvent.getInitiateSite());

            Pair<Integer, UnitInfo> unitMes = changeCurrUnit(gameEvent.getInitiateSite());

            changeCurrBgColor(record().getArmyList().get(unitMes.getKey()).getColor());

            changeCurrRegion(gameEvent.getInitiateSite());

            if (gameContext.getStatusMachine().equals(StatusMachineEnum.SHOW_MOVE_AREA) ||
                    gameContext.getStatusMachine().equals(StatusMachineEnum.MOVING)) {
                commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            } else if (gameContext.getStatusMachine().equals(StatusMachineEnum.MOVE_DONE)) {
                commandStream().toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite());
            }

            gameContext.setStatusMachine(StatusMachineEnum.NO_CHOOSE);
        }
    }


}
