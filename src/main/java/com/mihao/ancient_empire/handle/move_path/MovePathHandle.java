package com.mihao.ancient_empire.handle.move_path;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MovePathHandle {


    static Logger log = LoggerFactory.getLogger(MovePathHandle.class);

    public static List<PathPosition> getMovePath(ReqMoveDto moveDto) {
        // 1.获取移动路径
        Position currP = moveDto.getCurrentPoint();
        Position aimP = moveDto.getAimPoint();
        List<Position> positions = moveDto.getPositions();
        List<Position> path = null;

        // 2.如果两点本来就是可达的直接返回
        if (isReach(currP, aimP)) {
            path = new ArrayList<>();
            path.add(new Position(currP));
            path.add(new Position(aimP));
            return toPathPosition(path);
        }

        // 3.判断是不是简单路径 最多有一个弯道
        path = getEasyPath(currP, aimP, positions);

        // 4.现在不是简单路径了 是复杂路径至少需要两个弯道 需要一个中转点
        if (path == null) {
            // 1. 首先找可以简单到达终点的所有点
            List<Position> easyPaths = new ArrayList<>();
            for (Position position : positions) {
                if (isEasyPath(position, aimP, positions)) {
                    easyPaths.add(position);
                }
            }
            List<Position> realEasyPaths = new ArrayList<>();
            // 2.找到cp 能简单到的
            for (Position position : easyPaths) {
                if (isEasyPath(currP, position, positions)) {
                    realEasyPaths.add(position);
                }
            }
            if (realEasyPaths.size() == 0) {
                log.error("超级复杂的路径出现了{}");
                return toPathPosition(path);
            }
            // 3.找到所有简单路径中 真正最小的
            int smallIndex = 0;
            Position smallP = realEasyPaths.get(0);
            int smallPath = getTotalPathLength(currP, smallP, aimP);
            for (int i = 1; i < realEasyPaths.size(); i++) {
                Position transition = realEasyPaths.get(i);
                if (smallPath > getTotalPathLength(currP, transition, aimP)) {
                    smallIndex = i;
                    smallPath = getTotalPathLength(currP, transition, aimP);
                }
            }
            Position transP = realEasyPaths.get(smallIndex);
            // 4. 先求出当前点到中转的路径 在求出中转到目标的路径
            path = getEasyPath(currP, transP, positions);
            path.remove(path.size() - 1);
            List<Position> secondPath = getEasyPath(transP, aimP, positions);
            path.addAll(secondPath);
        }

        // 5. 返回需要转折点的关键点
        List<PathPosition> turningPoint = new ArrayList<>();
        turningPoint.add(new PathPosition(path.get(0)));
        for (int i = 1; i < path.size(); i++) {
            Position p = path.get(i);
            if (p.getRow() == currP.getRow() || p.getColumn() == currP.getColumn()) {
                continue;
            }
            currP = path.get(i - 1);
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
    private static List<Position> getEasyPath(Position currP, Position aimP, List<Position> positions) {
        List<Position> path = null;
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
    private static boolean isEasyPath(Position cp, Position ap, List<Position> positions) {
        if (isHorizonPath(cp, ap, positions) || isVerticalPath(cp, ap, positions)) {
            return true;
        }
        return false;
    }

    // 先水平走 再上下走 是否能直达 简单到达
    private static boolean isHorizonPath(Position cp, Position ap, List<Position> positions) {
        Position currP = new Position(cp);
        Position aimP = new Position(ap);
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
    private static boolean isVerticalPath(Position cp, Position ap, List<Position> positions) {
        Position aimP = new Position(ap);
        Position currP = new Position(cp);
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
    private static List<Position> getHorizonPath(Position cp, Position ap, List<Position> positions) {
        Position currP = new Position(cp);
        Position aimP = new Position(ap);
        List<Position> path = new ArrayList<>();
        path.add(new Position(currP));
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
            path.add(new Position(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setColumn(currP.getColumn() - columnInc);
                break;
            }
        }
        if (currP.getColumn() == aimP.getColumn()) {
            while (currP.getRow() != aimP.getRow()) {
                currP.setRow(currP.getRow() + rowInc);
                path.add(new Position(currP));
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
    private static List<Position> getVerticalPath(Position cp, Position ap, List<Position> positions) {
        Position aimP = new Position(ap);
        Position currP = new Position(cp);
        List<Position> path = new ArrayList<>();
        path.add(new Position(currP));
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
            path.add(new Position(currP));
            if (!isContent(positions, currP.getRow(), currP.getColumn())) {
                currP.setRow(currP.getRow() - rowInc);
                break;
            }
        }
        if (currP.getRow() == aimP.getRow()) {
            while (currP.getColumn() != aimP.getColumn()) {
                currP.setColumn(currP.getColumn() + columnInc);
                path.add(new Position(currP));
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
    private static boolean isReach(Position currP, Position aimP) {
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
    private static boolean isContent(List<Position> positions, int row, int column) {
        for (Position p : positions) {
            if (p.getRow() == row && p.getColumn() == column) {
                return true;
            }
        }
        return false;
    }

    // 获取三点的之间的距离
    private static int getTotalPathLength(Position currP, Position transP, Position aimP) {
        return Math.abs(currP.getRow() - transP.getRow()) + Math.abs(currP.getColumn() - transP.getColumn())
                + Math.abs(transP.getRow() - aimP.getRow()) + Math.abs(transP.getColumn() - aimP.getColumn());
    }

    // 将List<Position> 转成List<PathPosition>
    private static List<PathPosition> toPathPosition(List<Position> path) {
        List<PathPosition> pathPositions = new ArrayList<>();
        // 将路径长度放进去
        for (int i = 0; i < path.size() - 1; i++) {
            PathPosition p = new PathPosition(path.get(i));
            p.setLength(getPositionLength(path.get(i), path.get(i + 1)));
            pathPositions.add(p);
        }
        pathPositions.add(new PathPosition(path.get(path.size() - 1)));
        return pathPositions;
    }

    private static int getPositionLength(Position p1, Position p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

    private static int getPathPositionLength(PathPosition p1, PathPosition p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

}
