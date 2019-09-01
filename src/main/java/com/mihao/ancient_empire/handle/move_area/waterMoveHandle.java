package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.springframework.context.ApplicationContext;

public class waterMoveHandle extends MoveAreaHandle {

    public waterMoveHandle(UserRecord userRecord, ReqUnitIndexDto unitIndex, ApplicationContext ac) {
        super(userRecord, unitIndex, ac);
    }

    /**
     * 判断如果是水就返回 1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(int row, int column) {
        int index = (row - 1) * mapColumn + column - 1;
        String type = regionList.get(index).getType();
        if (type.startsWith(RegionEnum.SEA.getType()) || type.startsWith(RegionEnum.BANK.getType())) {
            return 1;
        }else {
            return super.getRegionDeplete(row, column);
        }
    }
}
