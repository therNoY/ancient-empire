package pers.mihao.ancient_empire.base.util;

import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.common.bo.ws_dto.RespAction;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.constant.UnitEnum;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.IntegerUtil;

/**
 * 和业务有关的工具类 方便修改
 */
public class AppUtil {

    static Logger log = LoggerFactory.getLogger(AuthUtil.class);

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
        if (cArmy == null)
            throw new MyException("record 记录错误 根据当前军队颜色找军队");
        return cArmy;
    }

    /**
     * 根据Record 和 color  获取当前军队
     *
     * @param record
     * @param index
     * @return
     */
    public static Army getArmyByIndex(UserRecord record, Integer index) {
        return record.getArmyList().get(index);
    }

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Unit unit) {
        return getRegionByPosition(record.getInitMap().getRegions(), unit.getRow(), unit.getColumn(), record.getInitMap().getColumn());
    }

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Site site) {
        return getRegionByPosition(record.getInitMap().getRegions(), site.getRow(), site.getColumn(), record.getInitMap().getColumn());
    }

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, int row, int column) {
        return getRegionByPosition(record.getInitMap().getRegions(), row, column, record.getInitMap().getColumn());
    }

    /**
     * 通过行列获取地图上的位置
     *
     * @return
     */
    public static BaseSquare getRegionByPosition(List<BaseSquare> regions, int row, int column, Integer mapColumn) {
        return regions.get((row - 1) * mapColumn - 1 + column);
    }

    /**
     * 根据 单位的action  和 最重点的位置渲染 单位action 图标的位置
     */
    public static List<RespAction> addActionAim(List<String> actionList, Position cUnit) {
        List<RespAction> respActions = new ArrayList<>();
        for (String s : actionList) {
            respActions.add(new RespAction(s, (float) cUnit.getRow(), (float) cUnit.getColumn()));
        }
        return respActions;
    }

    /**
     * 根据 单位的action  和 最重点的位置渲染 单位action 图标的位置
     */
    public static List<RespAction> addActionPosition(List<String> actionList, Position cUnit) {

        List<RespAction> respActions = new ArrayList<>();

        if (actionList.size() == 1) {
            // 只有一个action 显示在下面
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() + 1));
            action.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action);

        } else if (actionList.size() == 2) {
            // 2个action一个显示在上面 一个显示下面
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() - 1));
            action.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action);

            RespAction action2 = new RespAction(actionList.get(1));
            action2.setRow((float) (cUnit.getRow() + 1));
            action2.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action2);
        } else if (actionList.size() == 3) {
            // 3个显示个3交
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() - 1));
            action.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action);

            RespAction action2 = new RespAction(actionList.get(1));
            action2.setRow((float) (cUnit.getRow() + 1));
            action2.setColumn((float) (cUnit.getColumn() + 0.84));
            respActions.add(action2);

            RespAction action3 = new RespAction(actionList.get(2));
            action3.setRow((float) (cUnit.getRow() + 1));
            action3.setColumn((float) (cUnit.getColumn() - 0.84));
            respActions.add(action3);
        } else if (actionList.size() == 4) {
            // 4个显示
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() - 1));
            action.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action);

            RespAction action2 = new RespAction(actionList.get(1));
            action2.setRow((float) (cUnit.getRow() + 1));
            action2.setColumn((float) (cUnit.getColumn() + 0.16));
            respActions.add(action2);

            RespAction action3 = new RespAction(actionList.get(2));
            action3.setRow((float) (cUnit.getRow() + 0.16));
            action3.setColumn((float) (cUnit.getColumn() - 1));
            respActions.add(action3);

            RespAction action4 = new RespAction(actionList.get(3));
            action4.setRow((float) (cUnit.getRow() + 0.16));
            action4.setColumn((float) (cUnit.getColumn() + 1));
            respActions.add(action4);
        } else {
            log.error("不可能存在的Action");
        }

        return respActions;
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
     * 获取第几个单位
     *
     * @param record
     * @param index
     * @return
     */
    public static Unit getUnitByIndex(UserRecord record, Integer index) {
        Army army = getCurrentArmy(record);
        return army.getUnits().get(index);
    }


    /**
     * 获取当前的军队 index
     *
     * @param userRecord
     * @return
     */
    public static Integer getCurrentArmyIndex(UserRecord userRecord) {
        List<Army> armyList = userRecord.getArmyList();
        Army cArmy = null;
        for (int i = 0; i < armyList.size(); i++) {
            cArmy = armyList.get(i);
            if (userRecord.getCurrColor().equals(cArmy.getColor())) {
                return i;
            }
        }
        throw new MyException("错误的军队状态");
    }

    /**
     * 获取当前的军队
     *
     * @param userRecord
     * @return
     */
    public static Army getCurrentArmy(UserRecord userRecord) {
        return getArmyByColor(userRecord, userRecord.getCurrColor());
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
            if (army.getCamp() == camp) {
                unit = getUnitByPosition(army, site);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return unit;
    }

    /**
     * 得到不在指定阵营的单位
     *
     * @param record
     * @param site
     * @param camp
     * @return
     */
    public static Unit getUnitByPositionNotIn(UserRecord record, Site site, Integer camp) {
        Unit unit = null;
        for (Army army : record.getArmyList()) {
            if (army.getCamp() != camp) {
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
     * 获取单位的攻击（大于最小 小于最大）
     */
    public static int getAttachNum(UnitLevelMes levelMes) {
        int min = levelMes.getMinAttack();
        int max = levelMes.getMaxAttack();
        int att = IntegerUtil.getRandomIn(min, max);
        log.info("获取{} 和 {} 攻击的中间值 {}", min, max, att);
        return att;
    }

    /**
     * 获取单位的生命 int[] -> int
     *
     * @param unit
     * @return
     */
    public static int getUnitLeft(Unit unit) {
        Integer[] life = unit.getLife();
        return getIntByIntegers(life);
    }

    public static int getIntByIntegers(Integer[] life) {

        if (life != null && life.length > 0) {
            if (life[0] == -1) {
                return 0;
            } else {
                double sum = 0;
                for (int i = 0; i < life.length; i++) {
                    sum = (life[i] * Math.pow(10, life.length - 1 - i) + sum);
                }
                return (int) sum;
            }
        }
        return 0;
    }

    /**
     * 将int 转成 Integer[]
     */
    public static Integer[] getArrayByInt(int num) {
        char[] chars = String.valueOf(num).toCharArray();
        Integer[] integers = new Integer[chars.length];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.valueOf(chars[i] + "");
        }
        return integers;
    }

    /**
     * 将int 转成 Integer[] 指定前缀
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

    public static int getLength(Unit s1, Unit s2) {
        return Math.abs(s1.getRow() - s2.getRow()) + Math.abs(s1.getColumn() - s2.getColumn());
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
     *
     * @return
     */
    public static boolean isAround(Site s1, Site s2) {

        int length = AppUtil.getLength(s1, s2);
        if (length > 2) {
            return false;
        } else if (length == 2) {
            if (s1.getRow() == s2.getRow() && s1.getColumn() == s2.getColumn()) {
                return false;
            }
        }
        return true;
    }


    public static int getRegionIndex(UserRecord record, Site region) {
        return getRegionIndex(record.getInitMap(), region);
    }

    public static int getRegionIndex(GameMap map, Site region) {
        return getRegionIndex(map.getColumn(), region);
    }

    public static int getRegionIndex(Integer mapColumn, Site region) {
        int index = (region.getRow() - 1) * mapColumn + region.getColumn() - 1;
        return index;
    }

    /**
     * 获取同盟的所有颜色
     *
     * @param record
     * @return
     */
    public static List<String> getCampColors(UserRecord record) {
        List<String> strings = new ArrayList<>();
        Army cArmy = getCurrentArmy(record);
        for (Army army : record.getArmyList()) {
            if (army.getCamp() == cArmy.getCamp()) {
                strings.add(army.getColor());
            }
        }
        return strings;
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

    public static Integer getUnitIndex(Unit unit, Army army) {

        for (int i = 0; i < army.getUnits().size(); i++) {
            if (unit.getId().equals(army.getUnits().get(i).getId())) {
                return i;
            }
        }

        return -1;
    }

    public static int getHpRecover(BaseSquare square) {
        RegionEnum regionEnum = EnumUtil.valueOf(RegionEnum.class, square.getType());
        switch (regionEnum) {
            case RegionEnum.TOWN:
            case RegionEnum.CASTLE:
                return 20;
            case RegionEnum.STOCK:
            case RegionEnum.TEMPLE:
            case RegionEnum.SEA:
            case RegionEnum.SEA_HOUSE:
                return 15;
            case RegionEnum.REMAINS2:
                return 35;
            default:
                return 0;
        }
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
    public static boolean isUnitsContentSite(List<Unit> units, Site site) {
        for (Unit u: units) {
            if (u.getRow().equals(site.getRow()) && u.getColumn().equals(site.getColumn())) {
                return true;
            }
        }
        return false;
    }

}
