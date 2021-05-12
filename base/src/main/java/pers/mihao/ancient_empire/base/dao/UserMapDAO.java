package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import pers.mihao.ancient_empire.base.dto.CountSumDTO;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * userMapDao
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
     *
     * @param apiConditionDTO
     * @return
     */
    List<BaseMapInfoVO> getEncounterMapsWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 条件查询用户地图
     *
     * @param apiConditionDTO
     * @return
     */
    List<BaseMapInfoVO> selectUserCreateMapWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 获取地图的最大版本
     * @param type
     * @return
     */
    Integer getMaxVersionByMapType(String type);

    /**
     * 获取地图的收藏信息
     * @param mapType
     * @return
     */
    CountSumDTO selectCountStartByMapType(String mapType);

    /**
     * 获取最近编辑的地图
     * @param userId
     * @return
     */
    UserMap getLastEditMapById(Integer userId);
}
