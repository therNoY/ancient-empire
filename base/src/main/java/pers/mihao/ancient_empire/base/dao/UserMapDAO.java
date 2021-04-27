package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * @author mihao
 */
public interface UserMapDAO extends BaseMapper<UserMap> {

    /**
     * 根据userId获取userMap
     *
     * @param id
     * @return
     */
    List<UserMap> findByCreateUserId(Integer id);

    /**
     * 获取用户的草稿地图
     *
     * @param userId
     * @return
     */
    UserMap getFirstByCreateUserIdAndUnSave(Integer userId);

    /**
     * 通过用户id和地图名字获取
     *
     * @param id
     * @param name
     * @return
     */
    UserMap getFirstByCreateUserIdAndMapName(Integer id, String name);


    /**
     * 通过用户的id删除
     *
     * @param id
     * @param uuid
     */
    void deleteByCreateUserIdAndUuid(Integer id, String uuid);

    /**
     * 获取遭遇战地图
     * @return
     */
    List<BaseMapInfoVO> getEncounterMaps();

    /**
     * 条件查询用户地图
     * @param apiConditionDTO
     * @return
     */
    List<UserMap> selectUserCreateMapWithPage(ApiConditionDTO apiConditionDTO);

}
