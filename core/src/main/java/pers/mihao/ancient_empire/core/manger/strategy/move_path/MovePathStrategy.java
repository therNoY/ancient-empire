package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MovePathStrategy extends AbstractStrategy {


    Logger log = LoggerFactory.getLogger(MovePathStrategy.class);

    private static MovePathStrategy instance = null;

    public static MovePathStrategy getInstance() {
        if (instance == null) {
            instance = new MovePathStrategy();
        }
        return instance;
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
            if (p.getRow() == startSite.getRow() || p.getColumn() == startSite.getColumn()) {
                continue;
            }
            startSite = path.get(i - 1);
            turningPoint.add(new PathPosition(path.get(i - 1)));
        }
        turningPoint.add(new PathPosition(path.get(path.size() - 1)));

        // 6. 将路径长度放进去
        for (int i = 0; i < turningPoint.size() - 1; i++) {
            PathPosition p = turningPoint.get(i);
            p.setLength(getPathPositionLength(p, turningPoint.get(i + 1)));
        }
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
        while (currP.getColumn() != aimP.getColumn()) {
            currP.setColumn(currP.getColumn() + columnInc);
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setColumn(currP.getColumn() - columnInc);
                break;
            }
        }
        if (currP.getColumn() == aimP.getColumn()) {
            while (currP.getRow() != aimP.getRow()) {
                currP.setRow(currP.getRow() + rowInc);
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow() == aimP.getRow() && currP.getColumn() == aimP.getColumn()) {
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
        while (currP.getRow() != aimP.getRow()) {
            currP.setRow(currP.getRow() + rowInc);
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setRow(currP.getRow() - rowInc);
                break;
            }
        }
        if (currP.getRow() == aimP.getRow()) {
            while (currP.getColumn() != aimP.getColumn()) {
                currP.setColumn(currP.getColumn() + columnInc);
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow() == aimP.getRow() && currP.getColumn() == aimP.getColumn()) {
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
        while (currP.getColumn() != aimP.getColumn()) {
            currP.setColumn(currP.getColumn() + columnInc);
            path.add(new Site(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setColumn(currP.getColumn() - columnInc);
                break;
            }
        }
        if (currP.getColumn() == aimP.getColumn()) {
            while (currP.getRow() != aimP.getRow()) {
                currP.setRow(currP.getRow() + rowInc);
                path.add(new Site(currP));
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow() == aimP.getRow() && currP.getColumn() == aimP.getColumn()) {
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
        while (currP.getRow() != aimP.getRow()) {
            currP.setRow(currP.getRow() + rowInc);
            path.add(new Site(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setRow(currP.getRow() - rowInc);
                break;
            }
        }
        if (currP.getRow() == aimP.getRow()) {
            while (currP.getColumn() != aimP.getColumn()) {
                currP.setColumn(currP.getColumn() + columnInc);
                path.add(new Site(currP));
                if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                    break;
                }
            }
        }
        if (currP.getRow() == aimP.getRow() && currP.getColumn() == aimP.getColumn()) {
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
        for (int i = 0; i < path.size() - 1; i++) {
            PathPosition p = new PathPosition(path.get(i));
            p.setLength(getSiteLength(path.get(i), path.get(i + 1)));
            pathPositions.add(p);
        }
        pathPositions.add(new PathPosition(path.get(path.size() - 1)));
        return pathPositions;
    }



    private int getPathPositionLength(PathPosition p1, PathPosition p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

}
