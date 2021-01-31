package pers.mihao.ancient_empire.core.manger.strategy.end;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 单位行动结束后的handle
 */
public class EndStrategy extends AbstractStrategy<EndStrategy> {


    public static EndStrategy instance = null;

    protected static AbilityService abilityService = ApplicationContextHolder.getBean(AbilityService.class);


    public static EndStrategy getInstance() {
        if (instance == null) {
            instance = new EndStrategy();
        }
        return instance;
    }

    Logger log = LoggerFactory.getLogger(EndStrategy.class);

    /**
     * 获取结束移动的结果
     * @param record
     * @return
     */
    public EndUnitDTO getEndUnitResult(CommonHandler commonHandler){
        UserRecord record = commonHandler.record();
        List<Pair<Integer, Integer>> affectUnits = getAffectUnit(commonHandler.record());
        EndUnitDTO endUnitDTO = new EndUnitDTO();
        endUnitDTO.setLifeChangeList(new ArrayList<>());
        endUnitDTO.setUnitDeadDTOList(new ArrayList<>());
        endUnitDTO.setUnitStatusInfoDTOS(new ArrayList<>());

        getAbilityStrategy(record.getCurrUnit().getAbilities())
                .forEach(endStrategy -> endStrategy.warpEndResult(affectUnits, endUnitDTO, record));

//        poissonEndDesLife(commonHandler, record, endUnitDTO);

        return endUnitDTO;
    }

    /**
     * 中毒者回合结束掉血
     * @param commonHandler
     * @param record
     * @param endUnitDTO
     */
    private void poissonEndDesLife(CommonHandler commonHandler, UserRecord record, EndUnitDTO endUnitDTO) {
        if (StateEnum.POISON.type().equals(record.getCurrUnit().getStatus())) {
            int descLife = commonHandler.getGameContext().getPoisonDesLife();
            log.info("单位中毒需要减少生命:{}", descLife);
            endUnitDTO.getLifeChangeList().add(new LifeChangeDTO(AppUtil.getArrayByInt(-1, descLife), record.getCurrUnit()));
            int lastLife = AppUtil.getIntByIntegers(record.getCurrUnit().getLife());
            if (lastLife < descLife) {
                log.info("当前中毒单位死亡");
                UnitDeadDTO unitDeadDTO = new UnitDeadDTO(commonHandler.currUnitArmyIndex());
                endUnitDTO.getUnitDeadDTOList().add(unitDeadDTO);
            }else {
                UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(commonHandler.currUnitArmyIndex());
                unitStatusInfoDTO.setUpdateCurr(true)
                        .setLife(AppUtil.getArrayByInt(lastLife - descLife));
                endUnitDTO.getUnitStatusInfoDTOS().add(unitStatusInfoDTO);
            }
        }
    }


    /**
     * 获取军队的index 和 单位的index
     * @param record
     * @return
     */
    public List<Pair<Integer, Integer>> getAffectUnit(UserRecord record){
        List<Pair<Integer, Integer>> affectUnits = new ArrayList<>();
        for (int j = 0; j < record.getArmyList().size(); j++) {
            Army army = record.getArmyList().get(j);
            List<Unit> units = army.getUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                if (AppUtil.isAround(record.getCurrUnit(), unit)) {
                    affectUnits.add(new Pair<>(j, i));
                }
            }
        }
        return affectUnits;
    }
}
