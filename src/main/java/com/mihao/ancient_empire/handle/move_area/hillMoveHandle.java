package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class hillMoveHandle extends MoveAreaHandle {

    public hillMoveHandle(UserRecord userRecord, ReqUnitIndexDto unitIndex, ApplicationContext ac) {
        super(userRecord, unitIndex, ac);
    }

    /**
     * 判断如果是山就返回 1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(int row, int column) {
        int index = (row - 1) * mapColumn + column - 1;
        if (regionList.get(index).getType().equals(RegionEnum.STONE.getType())) {
            return 1;
        }else {
            return super.getRegionDeplete(row, column);
        }
    }
}
