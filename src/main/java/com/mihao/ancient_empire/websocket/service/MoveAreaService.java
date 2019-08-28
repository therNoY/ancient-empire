package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.RegionMesService;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MoveAreaService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public ApplicationContext ac;
    private UserRecord userRecord = null;
    private int armyIndex;
    private int index;
    private Army army = null;
    private Unit unit = null;
    private List<BaseSquare> regionList = null;
    private int mapRow;
    private int mapColumn;
    private RegionMesService regionMesService;
    private UnitLevelMesService unitLevelMesService;

    public MoveAreaService(UserRecord userRecord, ReqUnitIndexDto unitIndex, ApplicationContext ac) {
        this.userRecord = userRecord;
        this.armyIndex = unitIndex.getArmyIndex();
        this.index = unitIndex.getIndex();
        this.army = userRecord.getArmyList().get(unitIndex.getArmyIndex());
        this.unit = this.army.getUnits().get(unitIndex.getIndex());
        this.regionList = this.userRecord.getInitMap().getRegions();
        this.mapRow = this.userRecord.getInitMap().getRow();
        this.mapColumn = this.userRecord.getInitMap().getColumn();
        this.ac = ac;
        this.regionMesService = ac.getBean(RegionMesService.class);
        this.unitLevelMesService = ac.getBean(UnitLevelMesService.class);
    }


    public List<Position> getMovePosition() {
        log.info("需要查询的单位移动返回 {}", unit);
        int speed = unitLevelMesService.getSpeedByUnit(unit.getType(), unit.getLevel());
        List<Position> positions = new ArrayList<>();
        getWalkPosition(positions, new Position(unit.getRow(), unit.getColumn(), speed, -1));
        //  去掉有友方单位的可移动地方 感觉不过滤也可以
        return positions;
    }

    public void getWalkPosition(List<Position> positions, Position position) {
        // 只有当前节点剩余移动力大于1的时候才能找
        if (position.getLastMove() <= 0) {
            return;
        }
        // 1.判断上面的节点
        if (position.getRow() > 1 && position.getDirection() != Event.DOWN) {
            Position nPosition = new Position(position.getRow() - 1, position.getColumn());
            // 判断上面有没有单位
            if (!isHaveEnemy(nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.UP);
                        positions.add(nPosition);
                        getWalkPosition(positions, nPosition);
                    }
                }
            }
        }
        // 2.判断右边的节点
        if (position.getColumn() < this.mapColumn && position.getDirection() != Event.LEFT) {
            Position nPosition = new Position(position.getRow(), position.getColumn() + 1);
            // 判断上面有没有单位
            if (!isHaveEnemy(nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.RIGHT);
                        positions.add(nPosition);
                        getWalkPosition(positions, nPosition);
                    }

                }
            }

        }
        // 3.判断下面的节点
        if (position.getRow() < this.mapRow && position.getDirection() != Event.UP) {
            Position nPosition = new Position(position.getRow() + 1, position.getColumn());
            // 判断上面有没有单位
            if (!isHaveEnemy(nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.DOWN);
                        positions.add(nPosition);
                        getWalkPosition(positions, nPosition);
                    }
                }
            }
        }
        // 4.判断左面的节点
        if (position.getColumn() > 1 && position.getDirection() != Event.RIGHT) {
            Position nPosition = new Position(position.getRow(), position.getColumn() - 1);
            // 判断上面有没有单位
            if (!isHaveEnemy(nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.LEFT);
                        positions.add(nPosition);
                        getWalkPosition(positions, nPosition);
                    }
                }
            }
        }
    }

    // 判断上面有没有 敌方单位
    public boolean isHaveEnemy(int row, int column) {
        String color = army.getColor();
        for (Army a : userRecord.getArmyList()) {
            if (!a.getColor().equals(color)) {
                for (Unit u : a.getUnits()) {
                    if (!u.isDead() && u.getRow() == row && u.getColumn() == column) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 获取上面的地形 的消耗
    public int getRegionDeplete(int row, int column) {
        // 获取上面地形的type
        int index = (row - 1) * mapColumn + column - 1;
        String type = regionList.get(index).getType();
        RegionMes regionMes = regionMesService.getRegionByType(type);
        if (regionMes == null)
            throw new MyException("服务器错误");
        return regionMes.getDeplete();
    }
}
