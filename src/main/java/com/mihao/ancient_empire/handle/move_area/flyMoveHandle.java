package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.springframework.context.ApplicationContext;

public class flyMoveHandle extends MoveAreaHandle {

    public flyMoveHandle(UserRecord userRecord, ReqUnitIndexDto unitIndex, ApplicationContext ac) {
        super(userRecord, unitIndex, ac);
    }

    /**
     * 飞行对地形都是1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(int row, int column) {
        return 1;
    }
}
