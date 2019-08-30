package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.websocket.service.MoveAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebSocketService implements ApplicationContextAware {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;

    private ApplicationContext ac;

    /**
     * 获取单位的移动范围
     * @param unitIndex
     * @return
     */
    public List<Position> getMoveArea(String uuid, ReqUnitIndexDto unitIndex) {
        // 1.获取record
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        // 2.构造一个帮助类对象
        MoveAreaService moveAreaService = new MoveAreaService(userRecord, unitIndex, ac);
        List<Position> positions = moveAreaService.getMovePosition();
        return positions;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    /**
     * 获取移动路径
     * @param moveDto
     * @return
     */
    public List<Position> getMovePath(ReqMoveDto moveDto) {
        // 获取移动路径
        Position currP = moveDto.getCurrentPoint();
        Position aimP = moveDto.getAimPoint();
        List<Position> positions = moveDto.getPositions().stream()
                .distinct().collect(Collectors.toList());
        List<Position> path = null;

        // 如果两点本来就是可达的直接返回
        if (isReach(currP, aimP)){
            path = new ArrayList<>();
            path.add(new Position(currP));
            path.add(new Position(aimP));
            return path;
        }

        // 判断是不是简单路径 最多有一个弯道
        path = getEasyPath(currP, aimP, positions);

        // 现在不是简单路径了 是复杂路径至少需要两个弯道 需要一个中转点
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
                return path;
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
        return path;
    }

    /**
     * 获取简单的路径
     * @param currP
     * @param aimP
     * @param positions
     */
    private List<Position> getEasyPath(Position currP, Position aimP, List<Position> positions) {
        List<Position> path = null;
        if (Math.abs(currP.getRow() - aimP.getRow()) >= Math.abs(currP.getColumn() - aimP.getColumn())) {
            path = getHorizonPath(currP, aimP, positions);
            if (path == null) {
                path = getVerticalPath(currP, aimP, positions);
            }
        }else {
            path = getVerticalPath(currP, aimP, positions);
            if (path == null) {
                path = getHorizonPath(currP, aimP, positions);
            }
        }
        return path;
    }

    /**
     * 判断是否是简单到达
     * @param cp
     * @param ap
     * @param positions
     * @return
     */
    private boolean isEasyPath (Position cp, Position ap, List<Position> positions) {
        if (isHorizonPath(cp, ap ,positions) || isVerticalPath(cp, ap ,positions)) {
            return true;
        }
        return false;
    }

    // 先水平走 再上下走 是否能直达 简单到达
    private boolean isHorizonPath (Position cp, Position ap, List<Position> positions){
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
    private boolean isVerticalPath (Position cp, Position ap, List<Position> positions) {
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
    private List<Position> getHorizonPath (Position cp, Position ap, List<Position> positions){
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
    public List<Position> getVerticalPath (Position cp, Position ap, List<Position> positions) {
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
     * @param currP
     * @param aimP
     * @return
     */
    boolean isReach(Position currP, Position aimP) {
        if (Math.abs(currP.getRow() - aimP.getRow()) + Math.abs(currP.getColumn() - aimP.getColumn()) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含
     * @param positions
     * @param row
     * @param column
     * @return
     */
    boolean isContent(List<Position> positions, int row, int column) {
        for (Position p : positions) {
            if (p.getRow() == row && p.getColumn() == column) {
                return true;
            }
        }
        return false;
    }

    // 获取三点的之间的距离
    int getTotalPathLength(Position currP, Position transP, Position aimP) {
        return Math.abs(currP.getRow() - transP.getRow()) + Math.abs(currP.getColumn() - transP.getColumn())
                + Math.abs(transP.getRow() - aimP.getRow()) + Math.abs(transP.getColumn() - aimP.getColumn());
    }
}
