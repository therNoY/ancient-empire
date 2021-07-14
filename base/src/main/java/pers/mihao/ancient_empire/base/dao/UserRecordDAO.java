package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;


/**
 * @author mh
 */
public interface UserRecordDAO extends BaseMapper<UserRecord> {

    /**
     * 通过用户id和recordName获取
     *
     * @param id
     * @param name
     * @return
     */
    UserRecord getFirstByCreateUserIdAndRecordName(Integer id, String name);

    /**
     * 分页查询用户记录
     *
     * @param apiConditionDTO
     * @return
     */
    List<UserRecord> listUserRecordWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 删除其他为保存的记录
     *
     * @param uuid
     * @param userId
     */
    void delOtherUnSave(@Param("uuid") String uuid, @Param("userId") Integer userId);
}
