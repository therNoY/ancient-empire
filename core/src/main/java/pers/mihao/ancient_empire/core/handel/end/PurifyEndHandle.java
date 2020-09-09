package pers.mihao.ancient_empire.core.handel.end;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.LifeChange;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;

/**
 * 返回净化光环的handle
 */
public class PurifyEndHandle extends EndHandle {

    private static String ELF_BUFF_ = "unit.elf.buff.";
    private static PurifyEndHandle endHandle = null;
    private static AbilityService abilityService = ApplicationContextHolder.getBean(AbilityService.class);

    public static PurifyEndHandle instance() {
        if (endHandle == null) {
            endHandle = new PurifyEndHandle();
        }
        return endHandle;
    }

    /**
     * 净化光环效果 给周围的友军加血
     *
     * @param respEndResultDto
     * @param record
     * @param cUnit
     * @return
     */
    @Override
    public RespEndResultDto getEndResult(RespEndResultDto respEndResultDto, UserRecord record, Unit cUnit) {

        Integer camp = AppUtil.getCurrentArmy(record).getCamp();

        int buff = Integer.valueOf(AppConfig.get(ELF_BUFF_ + cUnit.getLevel()));

        Map<Integer, List<LifeChange>> lifeChanges = respEndResultDto.getLifeChanges();
        if (lifeChanges == null) {
            lifeChanges = new HashMap<>();
        }

        List<Army> armyList = record.getArmyList();
        for (int j = 0; j < armyList.size(); j++) {
            Army army = armyList.get(j);
            List<Unit> units = army.getUnits();
            List<LifeChange> changeList = lifeChanges.get(army.getColor());
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
                affect:
                {
                    if (!unit.isDead() && AppUtil.isAround(cUnit, unit)) {
                        for (Ability a : abilityList) {
                            if (a.getType().equals(AbilityEnum.UNDEAD.type())) {
                                // 是亡灵 并且在范围内
                                if (changeList == null) {
                                    changeList = new ArrayList<>();
                                }
                                LifeChange lifeChange = new LifeChange(i);
                                int life = AppUtil.getUnitLeft(unit);
                                // 判断单位是否承受不住
                                if (life <= buff) {
                                    lifeChange.setChange(AppUtil.getArrayByInt(-1, life));
                                    lifeChange.setDead(true);
                                    lifeChange.setHaveTomb(false);
                                } else {
                                    lifeChange.setChange(AppUtil.getArrayByInt(-1, buff));
                                    lifeChange.setLastLife(AppUtil.getArrayByInt(life - buff));
                                }
                                changeList.add(lifeChange);
                                break affect;
                            }
                        }

                        if (army.getCamp().equals(camp)) {
                            // 是友军
                            if (changeList == null) {
                                changeList = new ArrayList<>();
                            }
                            boolean isChange = false;
                            LifeChange lifeChange = new LifeChange(i);
                            int life = AppUtil.getUnitLeft(unit);
                            if (unit.getStatus() != null && !unit.getStatus().equals(StateEnum.EXCITED.type())) {
                                lifeChange.setState(StateEnum.NORMAL.type());
                                isChange = true;
                            }
                            if (life < 100) {
                                if (life + buff < 100) {
                                    lifeChange.setChange(AppUtil.getArrayByInt(10, buff));
                                    lifeChange.setLastLife(AppUtil.getArrayByInt(life + buff));
                                } else {
                                    lifeChange.setChange(AppUtil.getArrayByInt(10, 100 - life));
                                    lifeChange.setLastLife(new Integer[]{1, 0, 0});
                                }
                                isChange = true;
                            }
                            if (isChange) {
                                changeList.add(lifeChange);
                            }
                        }
                    }
                }
            }

            lifeChanges.put(j, changeList);
        }

        respEndResultDto.setLifeChanges(lifeChanges);
        return respEndResultDto;
    }
}
