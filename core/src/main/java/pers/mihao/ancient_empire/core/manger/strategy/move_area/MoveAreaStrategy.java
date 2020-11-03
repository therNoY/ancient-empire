package pers.mihao.ancient_empire.core.manger.strategy.move_area;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.*;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoveAreaStrategy extends AbstractStrategy<MoveAreaStrategy> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static MoveAreaStrategy instance = null;

    public static MoveAreaStrategy getInstance() {
        if (instance == null) {
            instance = new MoveAreaStrategy();
        }
        return instance;
    }

    /**
     * 虚弱剩余move力
     */
    private static final Integer WEAK_LAST_SPEED =  AppConfig.getInt("unitMes.weak.buff.lastSpeed");


    /**
     * 获取移动区域
     *
     * @param userRecord
     * @param unitInfo
     * @return
     */
    public List<Site> getMoveArea(UserRecord userRecord, UnitInfo unitInfo) {
        List<Site> sites = new ArrayList<>();
        int speed = unitInfo.getLevelMes().getSpeed() + 1;
        if (StateEnum.WEAK.type().equals(unitInfo.getStatus())) {
            speed = WEAK_LAST_SPEED + 1;
        }
        int[][] isVisit;
        for (MoveAreaStrategy moveAreaStrategy : getAbilityStrategy(unitInfo.getAbilities())) {
            isVisit = new int[userRecord.getGameMap().getRow()][userRecord.getGameMap().getColumn()];
            if (moveAreaStrategy != null) {
                moveAreaStrategy.getMoveSite(isVisit, unitInfo.getRow(), unitInfo.getColumn(), speed, userRecord, sites);
            }
        }
        if (sites.size() == 0) {
            isVisit = new int[userRecord.getGameMap().getRow()][userRecord.getGameMap().getColumn()];
            getMoveSite(isVisit, unitInfo.getRow(), unitInfo.getColumn(), speed, userRecord, sites);
        }
        return sites.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 获取二次移动区域
     * @param userRecord
     * @param unit
     * @param path
     * @return
     */
    public List<Site> getSecondMoveArea(UserRecord userRecord, UnitInfo unit, List<PathPosition> path) {
        // 2. 判断是否有二次移动
        List<Site> sites = null;
        // 攻击者能力
        UnitLevelMes levelMes;
        for (Ability ability : unit.getAbilities()) {
            if (ability.getType().equals(AbilityEnum.ASSAULT.type())) {
                // 是可以进行二次移动
                levelMes = unit.getLevelMes();
                int lastSpeed = levelMes.getSpeed() - getMoveUseSpeed(userRecord.getGameMap(), path, unit.getAbilities());
                if (lastSpeed > 0) {
                    levelMes.setSpeed(lastSpeed);
                    sites = getMoveArea(userRecord, unit);
                    return sites;
                }
            }
        }
        return sites;
    }


    /**
     * 获取 单位的剩余移动力
     */
    private int getMoveUseSpeed(GameMap map, List<PathPosition> path, List<Ability> abilities) {
        int sum = 0;
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                PathPosition p1 = path.get(i);
                PathPosition p2 = path.get(i + 1);
                sum += getPathPositionLength(map, p1, p2, abilities);
            }
        }
        return sum;
    }

    /**
     *
     * @param p1
     * @param p2
     * @return
     */
    private int getPathPositionLength(GameMap map, PathPosition p1, PathPosition p2, List<Ability> abilities) {
        // 如果是同一行
        int sum = 0;
        if (p1.getRow().equals(p2.getRow())) {
            int minC, maxC;
            if (p1.getColumn() < p2.getColumn()) {
                minC = p1.getColumn(); maxC = p2.getColumn();
            }else {
                minC = p2.getColumn(); maxC = p1.getColumn();
            }
            int minDeplete, temDep;
            for (int i = maxC; i > minC; i--) {
                minDeplete = Integer.MAX_VALUE;
                for (MoveAreaStrategy strategy : getAbilityStrategy(abilities)) {
                    temDep = strategy.getRegionDeplete(map, p1.getRow(), i);
                    minDeplete = Math.min(minDeplete, temDep);
                }
                sum += minDeplete;
            }
        }else {
            int minR, maxR;
            if (p1.getRow() < p2.getRow()) {
                minR = p1.getRow(); maxR = p2.getRow();
            }else {
                minR = p2.getRow(); maxR = p1.getRow();
            }
            int minDeplete, temDep;
            for (int i = maxR; i > minR; i--) {
                minDeplete = Integer.MAX_VALUE;
                for (MoveAreaStrategy strategy : getAbilityStrategy(abilities)) {
                    temDep = strategy.getRegionDeplete(map, p1.getColumn(), i);
                    minDeplete = Math.min(minDeplete, temDep);
                }
                sum += minDeplete;
            }
        }
        return sum;
    }


    /**
     * @param isVisit   初始为-1 值为表示上次访问的时候剩余的移动力
     * @param lastSpeed
     */
    public void getMoveSite(int[][] isVisit, int row, int column, int lastSpeed, UserRecord userRecord, List<Site> sites) {
        GameMap gameMap = userRecord.getGameMap();
        if ((isVisit[row][column] != 0 && isVisit[row][column] >= lastSpeed) || isHaveEnemy(userRecord, row, column)) {
            return;
        }
        // 记录已经访问
        isVisit[row][column] = lastSpeed;
        sites.add(new Site(row, column));
        int deplete;
        if (row - 1 > 0 && lastSpeed - (deplete = getRegionDeplete(gameMap, row - 1, column)) > 0) {
            getMoveSite(isVisit, row - 1, column, lastSpeed - deplete, userRecord, sites);
        }
        if (row + 1 <= gameMap.getRow() && lastSpeed - (deplete = getRegionDeplete(gameMap, row + 1, column)) > 0) {
            getMoveSite(isVisit, row + 1, column, lastSpeed - deplete, userRecord, sites);
        }
        if (column - 1 > 0 && lastSpeed - (deplete = getRegionDeplete(gameMap, row, column - 1)) > 0) {
            getMoveSite(isVisit, row, column - 1, lastSpeed - deplete, userRecord, sites);
        }
        if (column + 1 <= gameMap.getColumn() && lastSpeed - (deplete = getRegionDeplete(gameMap, row, column + 1)) > 0) {
            getMoveSite(isVisit, row, column + 1, lastSpeed - deplete, userRecord, sites);
        }
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

    // 获取上面的地形的消耗
    public int getRegionDeplete(GameMap gameMap, int row, int column) {
        // 获取上面地形的type
        int index = (row - 1) * gameMap.getColumn() + column - 1;
        String type = gameMap.getRegions().get(index).getType();
        RegionMes regionMes = ApplicationContextHolder.getBean(RegionMesService.class).getRegionByType(type);
        if (regionMes == null) {
            throw new AncientEmpireException("服务器错误");
        }
        return regionMes.getDeplete();
    }
}
