package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * 购买单位处理类
 * @Author mh32736
 * @Date 2020/9/17 16:04
 */
public class ClickBuyActionHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        // 验证军队资金
        UnitInfo newUnitInfo = unitMesService.getUnitInfo(gameEvent.getUnitId(), 0);
        if (currArmy().getMoney() < newUnitInfo.getUnitMes().getPrice()) {
            throw new AncientEmpireException("军队购买 资金不足");
        }
        // 验证军队人口
        if (currArmy().getPop() + newUnitInfo.getUnitMes().getPopulation() > record().getMaxPop()) {
            throw new AncientEmpireException("军队购买 超过最大人口");
        }
        // 更新
        Unit newUnit = UnitFactory.createUnit(gameEvent.getUnitId(), currSite());
        addNewUnit(newUnit, record().getCurrArmyIndex());

        // 更新军队信息
        ArmyStatusInfoDTO armyStatusInfoDTO = new ArmyStatusInfoDTO();

        armyStatusInfoDTO.setMoney(currArmy().getMoney() - newUnitInfo.getUnitMes().getPrice());
        armyStatusInfoDTO.setPop(currArmy().getPop() + newUnitInfo.getUnitMes().getPopulation());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_ARMY_INFO, ExtMes.ARMY_INFO, armyStatusInfoDTO);

        BeanUtil.copyValueByGetSet(newUnit, newUnitInfo);
        newUnitInfo.setRegionInfo(getRegionInfoBySite(newUnit.getRow(), newUnit.getColumn()));
        List<Site> moveArea = MoveAreaStrategy.getInstance().getMoveArea(record(), newUnitInfo);
        showMoveArea(moveArea);

        // 判断当前位置
        if(currSite().equals(currUnit())) {
            // 领主购买
            gameContext.setStatusMachine(StatusMachineEnum.MAST_MOVE);
            gameContext.setSubStatusMachine(SubStatusMachineEnum.MAST_MOVE);
        }

        changeCurrUnit(newUnitInfo);
    }
}
