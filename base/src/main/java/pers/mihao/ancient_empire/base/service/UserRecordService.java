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


    /**
     * 根据id获取地图
     * @param uuid
     * @return
     */
    UserRecord getRecordById(String uuid);

    boolean saveRecord(ReqSaveRecordDto saveRecordDto);

    boolean saveTempRecord(String uuid);

    /**
     * 根据ID 删除
     * @param uuid
     */
    void removeById(String uuid);

    /**
     * 保存信息
     * @param record
     */
    void saveRecord(UserRecord record);
}
