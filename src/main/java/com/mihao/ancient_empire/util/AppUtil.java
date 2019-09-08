package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.RespAction;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 和业务有关的工具类 方便修改
 */
public class AppUtil {

    static Logger log = LoggerFactory.getLogger(AuthUtil.class);

    /**
     * 根据地图的 region 的index 和map的column 获取position
     * @param index
     * @param column
     * @return
     */
    public static Position getPositionByMapIndex(int index, int column) {
        return new Position(index / column + 1, index % column + 1);
    }

    /**
     * 根据Record 和 color  获取当前军队
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
        return cArmy;
    }

    /**
     * 根据Record 和 color  获取当前军队
     * @param record
     * @param index
     * @return
     */
    public static Army getArmyByIndex(UserRecord record, Integer index) {
        return record.getArmyList().get(index);
    }

    /**
     *  获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Unit unit) {
        return getRegionByPosition(record.getInitMap().getRegions(), unit.getRow(), unit.getColumn(), record.getInitMap().getColumn());
    }

    /**
     * 通过行列获取地图上的位置
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
            respActions.add(new RespAction(s, (float)cUnit.getRow(), (float)cUnit.getColumn()));
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
            action.setColumn((float)(cUnit.getColumn() + 0.16));
            respActions.add(action);

        } else if (actionList.size() == 2) {
            // 2个action一个显示在上面 一个显示下面
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() - 1));
            action.setColumn((float)(cUnit.getColumn() + 0.16));
            respActions.add(action);

            RespAction action2 = new RespAction(actionList.get(1));
            action2.setRow((float) (cUnit.getRow() + 1));
            action2.setColumn((float)(cUnit.getColumn() + 0.16));
            respActions.add(action2);
        } else if (actionList.size() == 3) {
            // 3个显示个3交
            RespAction action = new RespAction(actionList.get(0));
            action.setRow((float) (cUnit.getRow() - 1));
            action.setColumn((float)(cUnit.getColumn() + 0.16));
            respActions.add(action);

            RespAction action2 = new RespAction(actionList.get(1));
            action2.setRow((float) (cUnit.getRow() + 1));
            action2.setColumn((float)(cUnit.getColumn() + 0.84));
            respActions.add(action2);

            RespAction action3 = new RespAction(actionList.get(2));
            action3.setRow((float) (cUnit.getRow() + 1));
            action3.setColumn((float)(cUnit.getColumn() - 0.84));
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
     * @param cUnit
     * @return
     */
    public static Position getPosition(Unit cUnit) {
        return new Position(cUnit.getRow(), cUnit.getColumn());
    }

    /**
     * 获取第几个单位
     * @param record
     * @param index
     * @return
     */
    public static Unit getUnitByIndex(UserRecord record, Integer index) {
        Army army = getCurrentArmy(record);
        return army.getUnits().get(index);
    }

    /**
     * 获取当前的军队
     * @param userRecord
     * @return
     */
    public static Army getCurrentArmy(UserRecord userRecord) {
        return getArmyByColor(userRecord, userRecord.getCurrColor());
    }

    /**
     * 通过位置判断该位置是不是友军
     */
    public static boolean isFriend(UserRecord record, Position position, Integer camp) {
        return getUnitByPosition(record, position, camp) == null ? false : true;
    }

    /**
     *
     */
    public static Unit getUnitByPosition(UserRecord record, Position position, Integer camp) {
        Unit unit = null;
        for (Army army : record.getArmyList()) {
            if (army.getCamp() == camp) {
                unit = getUnitByPosition(army, position);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return unit;
    }

    /**
     * 从军队中找给出位置的单位
     * @param army
     * @param position
     * @return
     */
    public static Unit getUnitByPosition(Army army, Position position) {
        for (Unit unit : army.getUnits()) {
            if (AppUtil.getPosition(unit).equals(position)) {
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
        int att = (int) (Math.random()*(max - min + 1) + min);
        log.info("获取{} 和 {} 攻击的中间值 {}", min, max, att);
        return att;
    }

    /**
     * 获取单位的生命 int[] -> int
     * @param unit
     * @return
     */
    public static int getUnitLeft(Unit unit) {
        Integer[] life = unit.getLife();
        if (life != null && life.length > 0) {
            if (life[0] == -1) {
                return 0;
            }
            else {
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
     * @param currP
     * @param aimP
     * @return
     */
    public static boolean isReach(Unit currP, Unit aimP) {
        if (Math.abs(currP.getRow() - aimP.getRow()) + Math.abs(currP.getColumn() - aimP.getColumn()) == 1) {
            return true;
        }
        return false;
    }
}
