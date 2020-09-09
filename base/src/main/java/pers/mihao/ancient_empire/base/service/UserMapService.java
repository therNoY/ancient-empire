package pers.mihao.ancient_empire.base.service;

import java.util.List;
import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespSimpleDrawing;
import pers.mihao.ancient_empire.base.entity.UserMap;

public interface UserMapService {
    List<UserMap> getUserMap();

    void saveTempMap(UserMap userMap);

    List<RespSimpleDrawing> getSimpleDrawing(ReqSimpleDrawing reqSimpleDrawing);

    String getType(String type);

    void saveMap(UserMap userMap);

    UserMap getUserMapByName(String mapName);

    void deleteMapById(String id);

    void updateMap(UserMap userMap);

    List<UserMap> getEncounterMaps();

    List<String> getInitArmy(String uuid);

    UserMap getEncounterMapById(String uuid);
}
