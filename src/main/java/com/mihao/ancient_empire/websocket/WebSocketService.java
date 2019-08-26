package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSocketService {

    @Autowired
    UserRecordService userRecordService;

    /**
     * 获取单位的移动范围
     * @param unitIndex
     * @return
     */
    public List<Position> getMoveArea(String uuid, ReqUnitIndexDto unitIndex) {
        // 1.获取record
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        return null;
    }
}
