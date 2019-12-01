package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.common.util.EnumUtil;
import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.RegionMesService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MoveAreaHandle{

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static MoveAreaHandle handle = null;

    public MoveAreaHandle() {
    }

    /**
     * 根据单位的 能力选择相应的能力处理器
     * @param abilityType
     * @return
     */
    public static MoveAreaHandle initActionHandle(String abilityType) {
        AbilityEnum type = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (type) {
            case FLY:
                return FlyMoveHandle.getInstance();
            case WATER_CLOSE:
                return WaterMoveHandle.getInstance();
            case HILL_CLOSE:
                return HillMoveHandle.getInstance();
            case FOREST_CLOSE:
                return ForestMoveHandle.getInstance();
            default:
                return null;
        }
    }

    public static MoveAreaHandle getDefaultHandle() {
        if (handle == null) {
            return new MoveAreaHandle();
        }
        return handle;
    }

    public List<Position> getMoveArea(UserRecord userRecord, Unit unit, UnitLevelMes levelMes) {
        Army army = AppUtil.getCurrentArmy(userRecord);
        return getMoveArea(userRecord, army, unit, levelMes);
    }

    public List<Position> getMoveArea(UserRecord userRecord, Army army, Unit unit, UnitLevelMes levelMes) {
        log.info("查询普单位{}移动范围", unit.getType());
        int speed = levelMes.getSpeed();
        List<Position> positions = new ArrayList<>();
        positions.add(AppUtil.getPosition(unit));
        getMovePosition(army, userRecord, positions, new Position(unit.getRow(), unit.getColumn(), speed, -1));
        return positions;
    }

    /**
     * 查询普通单位的移动路径
     * @param positions
     * @param position
     */
    public void getMovePosition(Army army, UserRecord userRecord, List<Position> positions, Position position) {
        // 只有当前节点剩余移动力大于1的时候才能找
        if (position.getLastMove() <= 0) {
            return;
        }
        // 1.判断上面的节点
        if (position.getRow() > 1 && position.getDirection() != Event.DOWN) {
            Position nPosition = new Position(position.getRow() - 1, position.getColumn());
            // 判断上面有没有单位
            if (!isHaveEnemy(army, userRecord, nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(userRecord, nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.UP);
                        positions.add(nPosition);
                        getMovePosition(army, userRecord, positions, nPosition);
                    }
                }
            }
        }
        // 2.判断右边的节点
        if (position.getColumn() < userRecord.getInitMap().getColumn() && position.getDirection() != Event.LEFT) {
            Position nPosition = new Position(position.getRow(), position.getColumn() + 1);
            // 判断上面有没有单位
            if (!isHaveEnemy(army, userRecord, nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(userRecord, nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.RIGHT);
                        positions.add(nPosition);
                        getMovePosition(army, userRecord, positions, nPosition);
                    }

                }
            }

        }
        // 3.判断下面的节点
        if (position.getRow() < userRecord.getInitMap().getRow() && position.getDirection() != Event.UP) {
            Position nPosition = new Position(position.getRow() + 1, position.getColumn());
            // 判断上面有没有单位
            if (!isHaveEnemy(army, userRecord, nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(userRecord, nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.DOWN);
                        positions.add(nPosition);
                        getMovePosition(army, userRecord, positions, nPosition);
                    }
                }
            }
        }
        // 4.判断左面的节点
        if (position.getColumn() > 1 && position.getDirection() != Event.RIGHT) {
            Position nPosition = new Position(position.getRow(), position.getColumn() - 1);
            // 判断上面有没有单位
            if (!isHaveEnemy(army, userRecord, nPosition.getRow(), nPosition.getColumn())) {
                int deplete = getRegionDeplete(userRecord, nPosition.getRow(), nPosition.getColumn());
                if (position.getLastMove() - deplete >= 0) {
                    nPosition.setLastMove(position.getLastMove() - deplete);
                    if (!positions.contains(nPosition)) {
                        nPosition.setDirection(Event.LEFT);
                        positions.add(nPosition);
                        getMovePosition(army, userRecord, positions, nPosition);
                    }
                }
            }
        }
    }

    // 判断上面有没有 敌方单位
    public boolean isHaveEnemy(Army army, UserRecord userRecord, int row, int column) {
        Integer camp = army.getCamp();
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

    // 获取上面的地形 的消耗
    public int getRegionDeplete(UserRecord userRecord, int row, int column) {
        // 获取上面地形的type
        int index = (row - 1) * userRecord.getInitMap().getColumn() + column - 1;
        String type = userRecord.getInitMap().getRegions().get(index).getType();
        RegionMes regionMes = ApplicationContextHolder.getBean(RegionMesService.class).getRegionByType(type);
        if (regionMes == null)
            throw new MyException("服务器错误");
        return regionMes.getDeplete();
    }


}
