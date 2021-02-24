package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.dto.MovePathDTO;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 *
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

    /**
     * 获取 两点的最小消耗
     *
     * @param startSite
     * @param aimSite
     * @return
     */
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
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex - column, unitInfo, record);
                // 更新上面的节点
            }
            if (lastAddIndex % column > 0) {
                // 更新右边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex - 1, unitInfo, record);
            }
            if (lastAddIndex % column != 1) {
                // 更新左边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex + 1, unitInfo, record);
            }
            if (graphSize - lastAddIndex > column) {
                // 更新下面边的点
                updateIndexValue(visitPath, lastVisitIndex, lastAddIndex, lastAddIndex + column, unitInfo, record);
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
        movePathDTO.setPositionList(pathPositions);
        return movePathDTO;
    }

    private void updateIndexValue(int[] visitPath, int[] lastVisitIndex, int lastAddIndex, int compareIndex,
        UnitInfo unitInfo, UserRecord record) {
        int deplete;
        deplete = getDepleteByIndex(unitInfo, record, compareIndex);
        // 更新上面的节点
        if (visitPath[lastAddIndex] + deplete < visitPath[compareIndex]) {
            visitPath[compareIndex] = visitPath[lastAddIndex] + deplete;
            lastVisitIndex[compareIndex] = lastAddIndex;
        }
    }

    private int getDepleteByIndex(UnitInfo unitInfo, UserRecord record, int index) {
        return getRegionDepleteByUnitInfo(unitInfo, record, index);
    }


    public List<PathPosition> getMovePath(Site startSite, Site endSite, List<Site> moveArea) {
        // 1.获取移动路径
        List<Site> path;

        // 2.如果两点本来就是可达的直接返回
        if (isReach(startSite, endSite)) {
            path = new ArrayList<>();
            path.add(new Site(startSite));
            path.add(new Site(endSite));
            return toPathPosition(path);
        }

        // 3.判断是不是简单路径 最多有一个弯道
        path = getEasyPath(startSite, endSite, moveArea);

        // 4.现在不是简单路径了 是复杂路径至少需要两个弯道 需要一个中转点
        if (path == null) {
            // 1. 首先找可以简单到达终点的所有点
            List<Site> easyPaths = new ArrayList<>();
            for (Site position : moveArea) {
                if (isEasyPath(position, endSite, moveArea)) {
                    easyPaths.add(position);
                }
            }
            List<Site> realEasyPaths = new ArrayList<>();
            // 2.找到cp 能简单到的
            for (Site position : easyPaths) {
                if (isEasyPath(startSite, position, moveArea)) {
                    realEasyPaths.add(position);
                }
            }
            if (realEasyPaths.size() == 0) {
                log.error("超级复杂的路径出现了{}");
                return toPathPosition(path);
            }
            // 3.找到所有简单路径中 真正最小的
            int smallIndex = 0;
            Site smallP = realEasyPaths.get(0);
            int smallPath = getTotalPathLength(startSite, smallP, endSite);
            for (int i = 1; i < realEasyPaths.size(); i++) {
                Site transition = realEasyPaths.get(i);
                if (smallPath > getTotalPathLength(startSite, transition, endSite)) {
                    smallIndex = i;
                    smallPath = getTotalPathLength(startSite, transition, endSite);
                }
            }
            Site transP = realEasyPaths.get(smallIndex);
            // 4. 先求出当前点到中转的路径 在求出中转到目标的路径
            path = getEasyPath(startSite, transP, moveArea);
            path.remove(path.size() - 1);
            List<Site> secondPath = getEasyPath(transP, endSite, moveArea);
            path.addAll(secondPath);
        }

        // 5. 返回需要转折点的关键点
        List<PathPosition> turningPoint = new ArrayList<>();
        turningPoint.add(new PathPosition(path.get(0)));
        for (int i = 1; i < path.size(); i++) {
            Site p = path.get(i);
            if (p.getRow().equals(startSite.getRow()) || p.getColumn().equals(startSite.getColumn())) {
                continue;
            }
            startSite = path.get(i - 1);
            turningPoint.add(new PathPosition(path.get(i - 1)));
        }
        turningPoint.add(new PathPosition(path.get(path.size() - 1)));

        return turningPoint;
    }

    /**
     * 获取简单的路径
     *
     * @param currP
     * @param aimP
     * @param positions
     */
    private List<Site> getEasyPath(Site currP, Site aimP, List<Site> positions) {
        List<Site> path = null;
        // 设置先跑路径比较长的
        if (Math.abs(currP.getRow() - aimP.getRow()) <= Math.abs(currP.getColumn() - aimP.getColumn())) {
            path = getHorizonPath(currP, aimP, positions);
            if (path == null) {
                path = getVerticalPath(currP, aimP, positions);
            }
        } else {
            path = getVerticalPath(currP, aimP, positions);
            if (path == null) {
                path = getHorizonPath(currP, aimP, positions);
            }
        }
        return path;
    }

    /**
     * 判断是否是简单到达
     *
     * @param cp
     * @param ap
     * @param positions
     * @return
     */
    private boolean isEasyPath(Site cp, Site ap, List<Site> positions) {
        if (isHorizonPath(cp, ap, positions) || isVerticalPath(cp, ap, positions)) {
            return true;
        }
        return false;
    }

    // 先水平走 再上下走 是否能直达 简单到达
    private boolean isHorizonPath(Site cp, Site ap, List<Site> positions) {
        Site currP = new Site(cp);
        Site aimP = new Site(ap);
        int columnInc = 1; // 书平增量
        if (currP.getColumn() > aimP.getColumn()) {
            columnInc = -1;
        }
        int rowInc = 1; // 书平增量
        if (currP.getRow() > aimP.getRow()) {
            rowInc = -1;
        }
        // 先水平走 再上下走
        while (!currP.getColumn().equals(aimP.getColumn())) {
            currP.setColumn(currP.getColumn() + columnInc);
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setColumn(currP.getColumn() - columnInc);
                break;
            }
        }
        if (currP.getColumn().equals(aimP.getColumn())) {
            while (!currP.getRow().equals(aimP.getRow())) {
                currP.setRow(currP.getRow() + rowInc);
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow().equals(aimP.getRow()) && currP.getColumn().equals(aimP.getColumn())) {
            return true;
        }
        return false;
    }

    // 先上下走 再水平走 是否能直达 简单到达
    private boolean isVerticalPath(Site cp, Site ap, List<Site> positions) {
        Site aimP = new Site(ap);
        Site currP = new Site(cp);
        int columnInc = 1; // 书平增量
        if (currP.getColumn() > aimP.getColumn()) {
            columnInc = -1;
        }
        int rowInc = 1; // 书平增量
        if (currP.getRow() > aimP.getRow()) {
            rowInc = -1;
        }
        // 先上下走 再水平走
        while (!currP.getRow().equals(aimP.getRow())) {
            currP.setRow(currP.getRow() + rowInc);
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setRow(currP.getRow() - rowInc);
                break;
            }
        }
        if (currP.getRow().equals(aimP.getRow())) {
            while (!currP.getColumn().equals(aimP.getColumn())) {
                currP.setColumn(currP.getColumn() + columnInc);
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow().equals(aimP.getRow()) && currP.getColumn().equals(aimP.getColumn())) {
            return true;
        }
        return false;
    }

    // 先水平走 再上下走 是否能直达 简单到达 并获取路径
    private List<Site> getHorizonPath(Site cp, Site ap, List<Site> positions) {
        Site currP = new Site(cp);
        Site aimP = new Site(ap);
        List<Site> path = new ArrayList<>();
        path.add(new Site(currP));
        int columnInc = 1; // 书平增量
        if (currP.getColumn() > aimP.getColumn()) {
            columnInc = -1;
        }
        int rowInc = 1; // 书平增量
        if (currP.getRow() > aimP.getRow()) {
            rowInc = -1;
        }
        // 先水平走 再上下走
        while (!currP.getColumn().equals(aimP.getColumn())) {
            currP.setColumn(currP.getColumn() + columnInc);
            path.add(new Site(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setColumn(currP.getColumn() - columnInc);
                break;
            }
        }
        if (currP.getColumn().equals(aimP.getColumn())) {
            while (!currP.getRow().equals(aimP.getRow())) {
                currP.setRow(currP.getRow() + rowInc);
                path.add(new Site(currP));
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow().equals(aimP.getRow()) && currP.getColumn().equals(aimP.getColumn())) {
            return path;
        }
        return null;
    }

    // 先上下走 再水平走 是否能直达 简单到达 并获取路径
    private List<Site> getVerticalPath(Site cp, Site ap, List<Site> positions) {
        Site aimP = new Site(ap);
        Site currP = new Site(cp);
        List<Site> path = new ArrayList<>();
        path.add(new Site(currP));
        int columnInc = 1; // 书平增量
        if (currP.getColumn() > aimP.getColumn()) {
            columnInc = -1;
        }
        int rowInc = 1; // 书平增量
        if (currP.getRow() > aimP.getRow()) {
            rowInc = -1;
        }
        // 先上下走 再水平走
        while (!currP.getRow().equals(aimP.getRow())) {
            currP.setRow(currP.getRow() + rowInc);
            path.add(new Site(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setRow(currP.getRow() - rowInc);
                break;
            }
        }
        if (currP.getRow().equals(aimP.getRow())) {
            while (!currP.getColumn().equals(aimP.getColumn())) {
                currP.setColumn(currP.getColumn() + columnInc);
                path.add(new Site(currP));
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow().equals(aimP.getRow()) && currP.getColumn().equals(aimP.getColumn())) {
            return path;
        }
        return null;
    }

    /**
     * 判断两点是否可达
     *
     * @param currP
     * @param aimP
     * @return
     */
    private boolean isReach(Site currP, Site aimP) {
        if (Math.abs(currP.getRow() - aimP.getRow()) + Math.abs(currP.getColumn() - aimP.getColumn()) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含
     *
     * @param positions
     * @param row
     * @param column
     * @return
     */
    private boolean isContent(List<Site> positions, int row, int column) {
        for (Site p : positions) {
            if (p.getRow() == row && p.getColumn() == column) {
                return true;
            }
        }
        return false;
    }

    // 获取三点的之间的距离
    private int getTotalPathLength(Site currP, Site transP, Site aimP) {
        return Math.abs(currP.getRow() - transP.getRow()) + Math.abs(currP.getColumn() - transP.getColumn())
            + Math.abs(transP.getRow() - aimP.getRow()) + Math.abs(transP.getColumn() - aimP.getColumn());
    }

    // 将List<Position> 转成List<PathPosition>
    private List<PathPosition> toPathPosition(List<Site> path) {
        List<PathPosition> pathPositions = new ArrayList<>();
        // 将路径长度放进去
        for (int i = 0; i < path.size(); i++) {
            PathPosition p = new PathPosition(path.get(i));
            pathPositions.add(p);
        }
        return pathPositions;
    }

    protected int getRegionDeplete(GameMap gameMap, int index) {
        // 获取上面地形的type
        String type = gameMap.getRegions().get(index).getType();
        RegionMes regionMes = regionMesService.getRegionByType(type);
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
    public int getRegionDepleteByUnitInfo(UnitInfo unitInfo, UserRecord record, int index) {
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


}

