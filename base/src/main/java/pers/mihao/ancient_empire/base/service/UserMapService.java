package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespSimpleDrawing;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.base.vo.UserMapVo;

import java.util.List;
import java.util.Map;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

public interface UserMapService extends MapService{

    /**
     * 获取用户创建的地图
     * @param userId
     * @return
     */
    List<UserMap> getUserCreateMap(Integer userId);

    /**
     * 获取用户创建的地图
     * @param id
     * @return
     */
    List<BaseMapInfoVO> getUserAllMapByUserId(Integer id);

    /**
     * 保存用户的草稿地图
     * @param userMap
     */
    void saveUserTempMap(UserMap userMap, Integer userId);

    /**
     * 获取优化后的画面
     * @param reqSimpleDrawing
     * @return
     */
    Map<Integer, String> getSimpleDrawing(ReqSimpleDrawing reqSimpleDrawing);

    /**
     * 草稿地图保存成设计地图
     * @param userMap
     */
    void saveMap(UserMap userMap);

    UserMap getUserMapByName(String mapName);

    void deleteMapById(String id);

    /**
     * 根据主键更新用户地图
     * @param userMap
     */
    void updateUserMapById(UserMap userMap);

    /**
     * 获取系统遭遇战地图
     * @return
     */
    List<BaseMapInfoVO> getEncounterMaps();

    List<String> getInitArmy(String uuid);

    UserMap getEncounterMapById(String uuid);

    /**
     * 根据Id获取用户地图
     * @return
     */
    UserMapVo getUserMapByUUID(String uuid);

    /**
     * 获取用户的草稿模板
     * @param userId
     * @return
     */
    UserMap getUserDraftEditMap(Integer userId);

    /**
     * 获取用户下载地图
     * @param apiConditionDTO
     * @return
     */
    List<UserMap> getUserDownloadMapList(ApiConditionDTO apiConditionDTO);

    /**
     * 获取可以下载的世界地图
     * @param apiConditionDTO
     * @return
     */
    List<UserMap> getWorldMapList(ApiConditionDTO apiConditionDTO);
}
