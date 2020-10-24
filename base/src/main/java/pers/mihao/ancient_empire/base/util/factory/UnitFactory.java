package pers.mihao.ancient_empire.base.util.factory;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\23 0023 21:37
 */
public class UnitFactory {

    public static UnitLevelMesService levelService = null;
    public static UnitMesService unitService = null;

    static {
        levelService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        unitService = ApplicationContextHolder.getBean(UnitMesService.class);
    }


    public static final Unit createUnit(Integer id, Site site) {
        return createUnit(id, site.getRow(), site.getColumn());
    }

    public static final Unit createUnit(Integer id, Integer row, Integer column) {
        return createUnit(id.toString(), row, column);
    }

    public static final Unit createUnit(String id, Integer row, Integer column) {
        UnitLevelMes levelMes = levelService.getUnitLevelMes(id, 1);
        UnitMes unitMes = unitService.getById(id);
        Unit unit = new Unit();
        unit.setColumn(column);
        unit.setRow(row);
        unit.setLife(AppUtil.getArrayByInt(levelMes.getMaxLife()));
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
