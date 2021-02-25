package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.core.dto.MovePathDTO;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动路径计算 计算两点之间的最短移动路径 核心迪杰斯特拉算法
 */
public class MovePathStrategy extends AbstractStrategy<MovePathStrategy> {


    Logger log = LoggerFactory.getLogger(MovePathStrategy.class);

    private static MovePathStrategy instance = null;

    public static MovePathStrategy getInstance() {
        if (instance == null) {
            instance = new MovePathStrategy();
        }
        return instance;
    }

    @KnowledgePoint("使用迪杰斯特拉算法计算'无向有权图'两点的最短路径")
    public MovePathDTO getUnitMovePath(Site startSite, Site aimSite, UserRecord record, UnitInfo unitInfo) {
        GameMap gameMap = record.getGameMap();
        MovePathDTO movePathDTO;
        int graphSize = gameMap.getRegions().size();
        int startIndex = getRegionIndexBySite(startSite, gameMap), endIndex = getRegionIndexBySite(aimSite, gameMap);
        // 保存路径
        boolean[] isVisit = new boolean[graphSize];
        int[] visitPath = new int[graphSize];
        int[] lastVisitIndex = new int[graphSize];
        // 1.初始化
        for (int i = 0; i < graphSize; i++) {
            visitPath[i] = Integer.MAX_VALUE;
            lastVisitIndex[i] = -1;
        }
        visitPath[startIndex] = 0;
        isVisit[startIndex] = true;

        int lastAddIndex = startIndex, column = gameMap.getColumn();

        while (!isVisit[endIndex]) {
            // 2. update 更新新加入的节点 相连的点 更新最短路径
            if (lastAddIndex >= column) {
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex - column, unitInfo, record, endIndex);
                // 更新上面的节点
            }
            if (lastAddIndex % column > 0) {
                // 更新左边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex - 1, unitInfo, record, endIndex);
            }
            if ((lastAddIndex + 1) % column != 0) {
                // 更新右边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex + 1, unitInfo, record, endIndex);
            }
            if (graphSize - lastAddIndex > column) {
                // 更新下面边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex + column, unitInfo, record, endIndex);
            }

            // 3. scan 从未访问过的节点找到最小的路径
            int minPath = Integer.MAX_VALUE;
            for (int i = 0; i < graphSize; i++) {
                if (!isVisit[i]) {
                    if (visitPath[i] < minPath) {
                        minPath = visitPath[i];
                        lastAddIndex = i;
                    }
                }
            }

            // 4. 将找到的点作为新的点加入
            isVisit[lastAddIndex] = true;
        }

        movePathDTO = new MovePathDTO();
        movePathDTO.setDeplete(visitPath[endIndex]);

        int tempIndex = endIndex;
        List<Integer> visitIndex = new ArrayList<>();
        visitIndex.add(tempIndex);
        while (tempIndex != startIndex) {
            // visit
            visitIndex.add(lastVisitIndex[tempIndex]);
            tempIndex = lastVisitIndex[tempIndex];
        }
        List<PathPosition> pathPositions = new ArrayList(visitIndex.size());
        for (int i = visitIndex.size() - 1; i >= 0; i--) {
            pathPositions.add(new PathPosition(getSiteByRegionIndex(visitIndex.get(i), gameMap)));
        }

        // 返回需要转折点的关键点
        List<PathPosition> turningPoint = new ArrayList<>();
        turningPoint.add(new PathPosition(startSite));
        for (int i = visitIndex.size() - 2; i >= 0; i--) {
            Site p = getSiteByRegionIndex(visitIndex.get(i), gameMap);
            if (p.getRow().equals(startSite.getRow()) || p.getColumn().equals(startSite.getColumn())) {
                continue;
            }
            startSite = getSiteByRegionIndex(visitIndex.get(i + 1), gameMap);
            turningPoint.add(new PathPosition(startSite));
        }
        turningPoint.add(new PathPosition(getSiteByRegionIndex(visitIndex.get(0), gameMap)));
        // 将路径长度放进去
        for (int i = 0; i < turningPoint.size() - 1; i++) {
            PathPosition p = turningPoint.get(i);
            p.setLength(getPathPositionLength(p, turningPoint.get(i + 1)));
        }

        movePathDTO.setPositionList(turningPoint);
        return movePathDTO;
    }

    private void updateIndexValue(int[] visitPath, int[] lastVisitIndex, int lastAddIndex, int compareIndex,
        UnitInfo unitInfo, UserRecord record, int endIndex) {
        int deplete;
        if (endIndex == compareIndex) {
            // 如果该点就是目标点 就是0
            deplete = 0;
        }else {
            deplete = getDepleteByIndex(unitInfo, record, compareIndex);
        }
        // 更新上面的节点
        if (deplete != Integer.MAX_VALUE && visitPath[lastAddIndex] + deplete < visitPath[compareIndex]) {
            visitPath[compareIndex] = visitPath[lastAddIndex] + deplete;
            lastVisitIndex[compareIndex] = lastAddIndex;
        }
    }

    private int getDepleteByIndex(UnitInfo unitInfo, UserRecord record, int index) {
        return getRegionDepleteByUnitInfo(unitInfo, record, index);
    }


    protected int getRegionDeplete(GameMap gameMap, int index) {
        // 获取上面地形的type
        String type = gameMap.getRegions().get(index).getType();
        RegionMes regionMes  = regionMesService.getRegionByTypeFromLocalCatch(type);
        return regionMes.getDeplete();
    }

    /**
     * 获取单位在地图上的某一点需要消耗的移动力
     *
     * @param unitInfo
     * @param record
     * @param index
     * @return
     */
    private int getRegionDepleteByUnitInfo(UnitInfo unitInfo, UserRecord record, int index) {
        int minDeplete, temDep;
        if (isHaveEnemy(record, getSiteByRegionIndex(index, record.getGameMap()))) {
            return Integer.MAX_VALUE;
        }
        minDeplete = getRegionDeplete(record.getGameMap(), index);
        for (MovePathStrategy strategy : getAbilityStrategy(unitInfo.getAbilities())) {
            temDep = strategy.getRegionDeplete(record.getGameMap(), index);
            minDeplete = Math.min(minDeplete, temDep);
        }
        return minDeplete;
    }

    private int getPathPositionLength(PathPosition p1, PathPosition p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

}

