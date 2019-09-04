package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.RespAction;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    public static BaseSquare getRegionByPosition(UserRecord record, Unit unit) {
        return getRegionByPosition(record.getInitMap().getRegions(), unit.getRow(), unit.getColumn(), record.getInitMap().getColumn());
    }

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
            action4.setColumn((float) (cUnit.getRow() + 1));
            respActions.add(action4);
        } else {
            log.error("不可能存在的Action");
        }

        return respActions;
    }


    public static Position getPosition(Unit cUnit) {
        return new Position(cUnit.getRow(), cUnit.getColumn());
    }
}
