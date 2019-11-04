package com.mihao.ancient_empire.service;

import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.dto.record_dto.ReqSaveRecordDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

public interface UserRecordService {
    String initMapRecord(ReqInitMapDto reqInitMapDto);

    void initMap();

    UserRecord getRecordById(String uuid);

    boolean saveRecord(ReqSaveRecordDto saveRecordDto);

    boolean saveTempRecord(String uuid);
}
