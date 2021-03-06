package pers.mihao.ancient_empire.base.util.factory;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\10\23 0023 21:37
 */
public class UnitFactory {

    public static UnitLevelMesService levelService = null;
    public static UnitMesService unitService = null;

    static {
        levelService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        unitService = ApplicationContextHolder.getBean(UnitMesService.class);
    }

    public static final Unit copyUnit(Unit oldUnit) {
        Unit newUnit = new Unit();
        BeanUtil.copyValueByGetSet(oldUnit, newUnit);
        UnitLevelMes levelMes = levelService.getUnitLevelMes(newUnit.getTypeId(), newUnit.getLevel());
        newUnit.setMaxLife(levelMes.getMaxLife());
        return newUnit;
    }


    public static final Unit createUnit(Integer id, Site site) {
        return createUnit(id, site.getRow(), site.getColumn());
    }


    public static final Unit createUnit(Integer id, Integer row, Integer column) {
        UnitLevelMes levelMes = levelService.getUnitLevelMes(id, 1);
        UnitMes unitMes = unitService.getById(id);
        Unit unit = new Unit();
        unit.setColumn(column);
        unit.setRow(row);
        unit.setLife(levelMes.getMaxLife());
        unit.setMaxLife(levelMes.getMaxLife());
        unit.setExperience(0);
        unit.setDone(false);
        unit.setLevel(0);
        unit.setId(StringUtil.getUUID());
        unit.setDead(false);
        unit.setTypeId(Integer.valueOf(id));
        unit.setType(unitMes.getType());
        return unit;
    }

}
