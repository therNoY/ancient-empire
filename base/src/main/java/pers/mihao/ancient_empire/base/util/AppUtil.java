package pers.mihao.ancient_empire.base.util;

import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.common.vo.AeException;

import java.util.List;

/**
 * 和业务有关的工具类 方便修改
 */
public class AppUtil {

    /**
     * 根据地图的 region 的index 和map的column 获取position
     *
     * @param index  region的index
     * @param column map的column
     * @return
     */
    public static Site getSiteByMapIndex(int index, int column) {
        return new Site(index / column + 1, index % column + 1);
    }

    /**
     * 根据Record 和 color  获取当前军队
     *
     * @param record
     * @param color
     * @return
     */
    public static Army getArmyByColor(UserRecord record, String color) {
        List<Army> armyList = record.getArmyList();
        Army cArmy = null;
        for (Army army : armyList) {
            if (color.equals(army.getColor())) {
                cArmy = army;
                break;
            }
        }
        if (cArmy == null) {
            throw new AeException("record 记录错误 根据当前军队颜色找军队");
        }
        return cArmy;
    }


    /**
     * 通过行列获取地图上的位置
     *
     * @return
     */
    public static BaseSquare getRegionByPosition(List<Region> regions, int row, int column, Integer mapColumn) {
        return regions.get((row - 1) * mapColumn - 1 + column);
    }


    /**
     * 获取单位的位置
     *
     * @param cUnit
     * @return
     */
    public static Position getPosition(Unit cUnit) {
        return new Position(cUnit.getRow(), cUnit.getColumn());
    }


    /**
     * 获取当前的军队
     *
     * @param userRecord
     * @return
     */
    public static Army getCurrentArmy(UserRecord userRecord) {
        return userRecord.getArmyList().get(userRecord.getCurrArmyIndex());
    }

    /**
     * 通过位置判断该位置是不是友军
     */
    public static boolean isFriend(UserRecord record, Site site, Integer camp) {
        return getUnitByPosition(record, site, camp) == null ? false : true;
    }

    /**
     * 得到指定阵营的单位
     *
     * @param record
     * @param site
     * @param camp
     * @return
     */
    public static Unit getUnitByPosition(UserRecord record, Site site, Integer camp) {
        Unit unit = null;
        for (Army army : record.getArmyList()) {
            if (army.getCamp().equals(camp)) {
                unit = getUnitByPosition(army, site);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return unit;
    }

    /**
     * 从军队中找给出位置的单位
     *
     * @param army
     * @param site
     * @return
     */
    public static Unit getUnitByPosition(Army army, Site site) {
        for (Unit unit : army.getUnits()) {
            if (AppUtil.getPosition(unit).equals(site)) {
                return unit;
            }
        }
        return null;
    }


    /**
     * 将int 转成 Integer[]
     */
    public static Integer[] getArrayByInt(int num) {
        if (num <= 0) {
            return new Integer[]{0};
        }
        char[] chars = String.valueOf(num).toCharArray();
        Integer[] integers = new Integer[chars.length];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.valueOf(chars[i] + "");
        }
        return integers;
    }

    /**
     * 将int 转成 Integer[] 指定前缀
     * @param n 前缀
     * @param num
     * @return
     */
    public static Integer[] getArrayByInt(int n, int num) {
        char[] chars = String.valueOf(num).toCharArray();
        Integer[] integers = new Integer[chars.length + 1];
        integers[0] = n;
        for (int i = 1; i < integers.length; i++) {
            integers[i] = Integer.valueOf(chars[i - 1] + "");
        }
        return integers;
    }

    public static int getIntByArray(Integer[] life) {

        if (life != null && life.length > 0) {
            double sum = 0;
            int multiply = 1;
            for (int i = 0; i < life.length; i++) {
                if (i == 0) {
                    if (life[i] == 10) {
                        continue;
                    }else if (life[i] == -1) {
                        multiply = -1;
                        continue;
                    }
                }
                sum = (life[i] * Math.pow(10, life.length - 1 - i) + sum);
            }
            return (int) sum * multiply;
        }
        return 0;
    }

    /**
     * 判断两点是否是直接挨着的
     *
     * @param currP
     * @param aimP
     * @return
     */
    public static boolean isReach(Site currP, Site aimP) {
        if (getLength(currP, aimP) == 1) {
            return true;
        }
        return false;
    }


    public static int getLength(Site s1, Unit s2) {
        return Math.abs(s1.getRow() - s2.getRow()) + Math.abs(s1.getColumn() - s2.getColumn());
    }


    public static int getLength(Site s1, Site s2) {
        return getLength(s1.getRow(), s2.getRow(), s1.getColumn(), s2.getColumn());
    }

    public static int getLength(int row1, int row2, int column1, int column2) {
        return Math.abs(row1 - row2) + Math.abs(column1 - column2);
    }

    /**
     * 判断一个单位是否在另一个单位的影响范围内
     * 包括自己
     *
     * @return
     */
    public static boolean isAround(Site s1, Site s2) {
        return AppUtil.getLength(s1, s2) <= 2;
    }


    public static int getRegionIndex(GameMap map, Site region) {
        return getRegionIndex(map.getColumn(), region);
    }

    public static int getRegionIndex(Integer mapColumn, Site region) {
        int index = (region.getRow() - 1) * mapColumn + region.getColumn() - 1;
        return index;
    }

    /**
     * 判断单位是否有指挥官
     *
     * @param army
     * @return
     */
    public static boolean hasLoad(Army army) {
        for (Unit unit : army.getUnits()) {
            if (unit.getType().equals(UnitEnum.LORD.type())) {
                return true;
            }
        }
        return false;
    }

    public static Unit getUnitId(UserRecord record, String uuid) {
        Army army = AppUtil.getCurrentArmy(record);
        for (Unit unit : army.getUnits()) {
            if (unit.getId().equals(uuid)) {
                return unit;
            }
        }
        return null;
    }

    // ****************************************新的方法***********************************************

    /**
     * 比较位置是否相等
     * @param Site
     * @param site
     * @return
     */
    public static boolean siteEquals(Site Site, Site site) {
        return Site.getRow().equals(site.getRow()) && Site.getColumn().equals(site.getColumn());
    }

    /**
     * 获取系统消息前缀
     * @return
     */
    public static String getSystemMessagePrefix(){
        switch (LoginUserHolder.getLanguage()) {
            case EN:
                return getMessagePrefix("System Message");
            case ZH:
            default:
                return getMessagePrefix("系统消息");
        }

    }

    public static String getMessagePrefix(String message){
        return "【" + message + "】";
    }
}
