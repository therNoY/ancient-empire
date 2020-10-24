package pers.mihao.ancient_empire.core.manger.strategy.end;

import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
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

    /**
     * 获取结束移动的结果
     * @param record
     * @return
     */
    public EndUnitDTO getEndUnitResult(UserRecord record){
        List<Pair<Integer, Integer>> affectUnits = getAffectUnit(record);
        EndUnitDTO endUnitDTO = new EndUnitDTO();
        endUnitDTO.setLifeChangeList(new ArrayList<>());
        endUnitDTO.setUnitDeadDTOList(new ArrayList<>());
        endUnitDTO.setUnitStatusInfoDTOS(new ArrayList<>());

        getAbilityStrategy(record.getCurrUnit().getAbilities())
                .forEach(endStrategy -> endStrategy.warpEndResult(affectUnits, endUnitDTO, record));
        return endUnitDTO;
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
