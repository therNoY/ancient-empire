package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.dto.InitMapDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDTO;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * 用户存档接口
 */
public interface UserRecordService extends IService<UserRecord> {
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

    /**
     * 保存record
     * @param saveRecordDto
     * @return
     */
    boolean saveRecord(ReqSaveRecordDTO saveRecordDto);

    /**
     * 保存临时地图
     * @param uuid
     * @return
     */
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
    IPage<UserRecord> listUserRecordWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 移除缓存
     * @param id
     */
    void removeCatch(String id);

    /**
     * 删除其他为保存的单机记录 一个用户只能保存一个单机
     * @param uuid
     */
    void delOtherUnSaveStandRecord(String uuid, Integer userId);

    /**
     * 删除用户保存记录
     * @param uuid
     * @param userId
     */
    void delById(String uuid, Integer userId);
}
