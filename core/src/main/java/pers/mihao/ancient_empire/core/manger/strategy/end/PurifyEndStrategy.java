package pers.mihao.ancient_empire.core.manger.strategy.end;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
import pers.mihao.ancient_empire.core.dto.LifeChangeDTO;
import pers.mihao.ancient_empire.core.dto.UnitDeadDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;

import java.util.List;

/**
 * 返回净化光环的handle
 */
public class PurifyEndStrategy extends EndStrategy {

    Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * 净化者的buff数值
     */
    private static String ELF_BUFF_ = "unitMes.elf.buff.";

    private static UnitLevelMesService levelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);


    /**
     * 净化光环的效果
     * 1. 亡灵军队 减少血量
     * 2. 友军不是满血 就加血
     * 3. 友军状态如果有负面 就清除
     *
     * @param affectUnits
     * @param endUnitDTO
     * @param record
     * @return
     */
    @Override
    protected EndUnitDTO warpEndResult(List<Pair<Integer, Integer>> affectUnits, EndUnitDTO endUnitDTO, UserRecord record) {
        Unit unit;
        Army army;
        Integer armyIndex;
        Integer unitIndex;
        UnitLevelMes levelMes;
        List<Ability> abilityList;
        // 净化者的buff能力
        Integer buff = AppConfig.getInt(ELF_BUFF_ + record.getCurrUnit().getLevel());
        for (Pair<Integer, Integer> pair : affectUnits) {
            unitIndex = pair.getValue();
            armyIndex = pair.getKey();
            army = record.getArmyList().get(armyIndex);
            unit = army.getUnits().get(unitIndex);
            abilityList = abilityService.getUnitAbilityList(unit.getTypeId());
            UnitStatusInfoDTO statusInfoDTO = new UnitStatusInfoDTO(armyIndex, unitIndex);
            boolean isChange = false;
            // 解除deBuff
            if (unit.getStatus() != null
                    && army.getCamp().equals(record.getCurrCamp())
                    && EnumUtil.valueOf(StateEnum.class, unit.getStatus()).isDeBuff) {
                isChange = true;
                statusInfoDTO.setStatus(StateEnum.NORMAL.type());
            }

            if (abilityList.contains(AbilityEnum.UNDEAD.ability())) {
                // 亡灵净化
                int life = AppUtil.getUnitLife(unit);
                if (life <= buff) {
                    // 亡灵死亡
                    log.info("亡灵：{} 被净化者净化死亡", unit);
                    endUnitDTO.getLifeChangeList().add(new LifeChangeDTO(AppUtil.getArrayByInt(-1, life), unit));
                    endUnitDTO.getUnitDeadDTOList().add(new UnitDeadDTO(armyIndex, unitIndex));
                } else {
                    endUnitDTO.getLifeChangeList().add(new LifeChangeDTO(AppUtil.getArrayByInt(-1, buff), unit));
                    isChange = true;
                    statusInfoDTO.setLife(AppUtil.getArrayByInt(life - buff));
                }
            }else if (army.getCamp().equals(record.getCurrCamp())) {
                // 残血友军回血
                levelMes = levelMesService.getUnitLevelMes(unit.getTypeId().toString(), unit.getLevel());
                int unitLife = AppUtil.getUnitLife(unit);
                int maxRestore = levelMes.getMaxLife() - unitLife;
                if (maxRestore != 0) {
                    int restore = Math.min(maxRestore, buff);
                    LifeChangeDTO restoreLife = new LifeChangeDTO(AppUtil.getArrayByInt(10, restore), unit);
                    endUnitDTO.getLifeChangeList().add(restoreLife);
                    isChange = true;
                    statusInfoDTO.setLife(AppUtil.getArrayByInt(restore + unitLife));
                }
            }
            if (isChange) {
                endUnitDTO.getUnitStatusInfoDTOS().add(statusInfoDTO);
            }
        }
        return endUnitDTO;
    }
}
