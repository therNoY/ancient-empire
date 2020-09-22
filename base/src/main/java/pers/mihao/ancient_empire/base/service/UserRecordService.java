package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;

/**
 * 用户存档接口
 */
public interface UserRecordService extends MapService{
    /**
     * 根据地图生成一个存档
     * @param reqInitMapDto
     * @return
     */
    UserRecord initMapRecord(ReqInitMapDto reqInitMapDto, UserMap userMap);

    void initMap();

    UserRecord getRecordById(String uuid);

    boolean saveRecord(ReqSaveRecordDto saveRecordDto);

    boolean saveTempRecord(String uuid);
}
