package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespSimpleDrawing;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.base.vo.UserMapVo;

import java.util.List;

public interface UserMapService extends MapService{
    List<UserMap> getUserMap();

    void saveTempMap(UserMap userMap);

    List<RespSimpleDrawing> getSimpleDrawing(ReqSimpleDrawing reqSimpleDrawing);

    String getType(String type);

    void saveMap(UserMap userMap);

    UserMap getUserMapByName(String mapName);

    void deleteMapById(String id);

    void updateMap(UserMap userMap);

    List<BaseMapInfoVO> getEncounterMaps();

    List<String> getInitArmy(String uuid);

    UserMap getEncounterMapById(String uuid);

    /**
     * 根据Id获取用户地图
     * @return
     */
    UserMapVo getUserMapById(String uuid);
}
