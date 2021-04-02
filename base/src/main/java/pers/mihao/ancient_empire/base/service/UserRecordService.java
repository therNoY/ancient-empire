package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.InitMapDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

import java.util.List;

/**
 * 用户存档接口
 */
public interface UserRecordService extends MapService{
    /**
     * 根据地图生成一个存档
     * @param initMapDTO
     * @param userMap
     * @return
     */
    UserRecord initMapRecord(InitMapDTO initMapDTO, UserMap userMap);


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

    /**
     * 根据条件进行分页查询用户信息
     * @param apiConditionDTO
     * @return
     */
    List<UserRecord> listUserRecordWithPage(ApiConditionDTO apiConditionDTO);
}
