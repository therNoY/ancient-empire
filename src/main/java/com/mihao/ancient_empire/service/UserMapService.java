package com.mihao.ancient_empire.service;

import com.mihao.ancient_empire.dto.map_dto.ReqSimpleDrawing;
import com.mihao.ancient_empire.dto.map_dto.RespSimpleDrawing;
import com.mihao.ancient_empire.entity.mongo.UserMap;

import java.util.List;

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
