package pers.mihao.ancient_empire.core.manger.handler;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * 点击可以移动的单位处理
 * @Author mh32736
 * @Date 2020/9/17 9:52
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_ACTIVE_UNIT}
 */
public class ClickActiveUnitHandler extends CommonHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    // 点击可以移动的单位
    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        if (stateIn(StatusMachineEnum.WILL_ATTACH)) {
            // 点击其他区域的单位就返回
            commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ACTION, ExtMes.ACTIONS, gameContext.getActions());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
        }else if (stateIn(StatusMachineEnum.MOVE_DONE)) {
            // 点击其他区域的单位就返回
            gameContext.setStatusMachine(StatusMachineEnum.NO_CHOOSE);
            commandStream().toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite(), getCurrUnitIndex());
        }else {
            changeCurrPoint(gameEvent.getInitiateSite());
            Pair<Integer, UnitInfo> unitMes = changeCurrUnit(gameEvent.getInitiateSite());
            changeCurrBgColor(record().getArmyList().get(unitMes.getKey()).getColor());
            changeCurrRegion(gameEvent.getInitiateSite());
            UnitInfo unitInfo = unitMes.getValue();
            // 判断展示移动区域还是展示行动
            if (unitInfo.getAbilities().contains(AbilityEnum.CASTLE_GET.ability())) {

            } else {
                List<Site> moveArea = MoveAreaStrategy.getInstance().getMoveArea(record(), unitInfo);
                gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_AREA);
                gameContext.setWillMoveArea(moveArea);
                commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, moveArea);
            }
        }
    }

}
