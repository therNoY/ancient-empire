package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.common.bo.record_dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

public interface UserRecordService {
    String initMapRecord(ReqInitMapDto reqInitMapDto);

    void initMap();

    UserRecord getRecordById(String uuid);

    boolean saveRecord(ReqSaveRecordDto saveRecordDto);

    boolean saveTempRecord(String uuid);
}
