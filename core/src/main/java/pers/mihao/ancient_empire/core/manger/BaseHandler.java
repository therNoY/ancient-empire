package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTransferService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.robot.RobotManger;

/**
 * 基础能力类
 * @Author mh32736
 * @Date 2021/2/3 13:07
 */
public abstract class BaseHandler implements Handler {

    protected static RegionMesService regionMesService;
    protected static UnitMesService unitMesService;
    protected static UnitLevelMesService unitLevelMesService;
    protected static UnitTransferService unitTransferService;
    protected static UserRecordService userRecordService;
    protected static AbilityService abilityService;
    protected static RobotManger robotManger;

    static {
        regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
        unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);
        abilityService = ApplicationContextHolder.getBean(AbilityService.class);
        unitLevelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        unitTransferService = ApplicationContextHolder.getBean(UnitTransferService.class);
        userRecordService = ApplicationContextHolder.getBean(UserRecordService.class);
        robotManger = ApplicationContextHolder.getBean(RobotManger.class);
    }


    /**
     * 基础方法 在指定军队中 指定位置获取单位
     *
     * @param army
     * @param site
     * @return
     */
    protected Unit getUnitFromArmyBySite(Army army, Site site) {
        for (Unit unit : army.getUnits()) {
            if (AppUtil.siteEquals(unit, site)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 获取两点的距离
     *
     * @param row
     * @param column
     * @param row2
     * @param column2
     * @return
     */
    protected int getSiteLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

    protected int getSiteLength(Site site, Site site2) {
        return getSiteLength(site.getRow(), site.getColumn(), site2.getRow(), site2.getColumn());
    }



    /**
     * 根据颜色判断是否是同一个阵营的
     *
     * @param color
     * @return
     */
    protected boolean colorIsCamp(UserRecord record, String color) {
        return currArmy(record).getCamp() == getCampByColor(record, color);
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    public Army currArmy(UserRecord record) {
        return record.getArmyList().get(record.getCurrArmyIndex());
    }

    protected int getCampByColor(UserRecord record, String color) {
        for (Army army : record.getArmyList()) {
            if (army.getColor().equals(color)) {
                return army.getCamp();
            }
        }
        return -1;
    }

    // 判断上面有没有 敌方单位
    public boolean isHaveEnemy(UserRecord userRecord, int row, int column) {
        Integer camp = userRecord.getArmyList().get(userRecord.getCurrArmyIndex()).getCamp();
        for (Army a : userRecord.getArmyList()) {
            if (!a.getCamp().equals(camp)) {
                for (Unit u : a.getUnits()) {
                    if (!u.isDead() && u.getRow() == row && u.getColumn() == column) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 判断上面有没有 敌方单位
    public boolean isHaveFriend(UserRecord userRecord, int row, int column) {
        Integer camp = userRecord.getArmyList().get(userRecord.getCurrArmyIndex()).getCamp();
        for (Army a : userRecord.getArmyList()) {
            if (a.getCamp().equals(camp)) {
                for (Unit u : a.getUnits()) {
                    if (!u.isDead() && u.getRow() == row && u.getColumn() == column) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
