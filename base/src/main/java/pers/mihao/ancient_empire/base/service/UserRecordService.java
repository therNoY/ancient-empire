package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.UserRecord;

public interface UserRecordService {
    String initMapRecord(ReqInitMapDto reqInitMapDto);

    void initMap();

    UserRecord getRecordById(String uuid);

    boolean saveRecord(ReqSaveRecordDto saveRecordDto);

    boolean saveTempRecord(String uuid);
}
