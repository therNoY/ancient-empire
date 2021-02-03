package pers.mihao.ancient_empire.core.util;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.IntegerUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.dto.RespAction;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @Author mh32736
 * @Date 2020/9/8 20:46
 */
public class GameCoreHelper {

    static Logger log = LoggerFactory.getLogger(GameCoreHelper.class);

    private static ThreadLocal<GameContext> gameContextThreadLocal = new ThreadLocal<>();

    public static GameContext getContext() {
        return gameContextThreadLocal.get();
    }

    public static void setContext(GameContext gameContext) {
        gameContextThreadLocal.set(gameContext);
    }

    public static void removeContext() {
        gameContextThreadLocal.remove();
    }

    /**
     * 获得单位的位置
     */
    public static BaseSquare getRegionByPosition(UserRecord record, Unit unit) {
        return getRegionByPosition(record.getGameMap().getRegions(), unit.getRow(), unit.getColumn(),
            record.getGameMap().getColumn());
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
    public static BaseSquare getRegionByPosition(List<Region> regions, int row, int column, Integer mapColumn) {
        return regions.get((row - 1) * mapColumn - 1 + column);
    }

}
