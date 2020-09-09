package pers.mihao.ancient_empire.core.util;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.IntegerUtil;
import pers.mihao.ancient_empire.common.vo.MyException;
import pers.mihao.ancient_empire.core.dto.RespAction;

/**
 * @Author mh32736
 * @Date 2020/9/8 20:46
 */
public class GameCoreHelper {

    static Logger log = LoggerFactory.getLogger(GameCoreHelper.class);

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Unit unit) {
        return getRegionByPosition(record.getGameMap().getRegions(), unit.getRow(), unit.getColumn(),
            record.getGameMap().getColumn());
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
            throw new MyException("不可能存在的Action");
        }

        return respActions;
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
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Site site) {
        return getRegionByPosition(record.getGameMap().getRegions(), site.getRow(), site.getColumn(),
            record.getGameMap().getColumn());
    }

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, int row, int column) {
        return getRegionByPosition(record.getGameMap().getRegions(), row, column, record.getGameMap().getColumn());
    }

    public static int getRegionIndex(UserRecord record, Site region) {
        return getRegionIndex(record.getGameMap(), region);
    }

    public static int getRegionIndex(GameMap map, Site region) {
        return getRegionIndex(map.getColumn(), region);
    }

    public static int getRegionIndex(Integer mapColumn, Site region) {
        int index = (region.getRow() - 1) * mapColumn + region.getColumn() - 1;
        return index;
    }

    /**
     * 通过行列获取地图上的位置
     *
     * @return
     */
    public static BaseSquare getRegionByPosition(List<BaseSquare> regions, int row, int column, Integer mapColumn) {
        return regions.get((row - 1) * mapColumn - 1 + column);
    }


    public static int getHpRecover(BaseSquare square) {
        RegionEnum regionEnum = EnumUtil.valueOf(RegionEnum.class, square.getType());
        switch (regionEnum) {
            case TOWN:
            case CASTLE:
                return 20;
            case STOCK:
            case TEMPLE:
            case SEA:
            case SEA_HOUSE:
                return 15;
            case REMAINS2:
                return 35;
            default:
                return 0;
        }
    }



}
