package com.mihao.ancient_empire.controller;


import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.dto.RespUserMapDao;
import com.mihao.ancient_empire.dto.map_dto.ReqSimpleDrawing;
import com.mihao.ancient_empire.dto.map_dto.RespSimpleDrawing;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.UserSetting;
import com.mihao.ancient_empire.entity.mongo.UserMap;
import com.mihao.ancient_empire.service.RegionMesService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserMapService;
import com.mihao.ancient_empire.service.UserSettingService;
import com.mihao.ancient_empire.util.AuthUtil;
import io.jsonwebtoken.lang.Collections;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户地图管理的Controller
 */
@RestController
public class UserMapController {

    @Autowired
    UserMapService userMapService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    RegionMesService regionMesService;
    @Autowired
    UserSettingService userSettingService;

    /**
     * 获取编辑地图时需要获取的初始数据
     * @return
     */
    @GetMapping("/api/userMap/init")
    public RespJson getInitUserMap() {
        // 获取当前用户
        Integer id = AuthUtil.getAuthId();
        // 1.获取可用单位信息
        List<UnitMes> unitMesList = unitMesService.getEnableUnitByUserId(id);
        // 2.获取可用地形信息
        List<RegionMes> regionMes = regionMesService.getEnableRegionByUserId(id);
        // 3.获取用户拥有的地图
        List<UserMap> userAllMaps = userMapService.getUserMap();
        List<UserMap> userMaps = userAllMaps.stream()
                .filter(userMap -> userMap.isUnSave() == false)
                .collect(Collectors.toList());
        // 3.1 查看是否有未保存的地图
        UserMap unSaveMap = null;
        Optional<UserMap> optional = userAllMaps.stream().filter(userMap -> userMap.isUnSave() == true).findFirst();
        if (optional.isPresent()) {
            unSaveMap = optional.get();
        }
        // 4.获取初始化地图信息
        UserSetting userSetting = userSettingService.getUserSettingById(id);
        RespUserMapDao userMapDao = new RespUserMapDao(unitMesList, regionMes, userMaps, unSaveMap, userSetting);
        return RespHelper.successResJson(userMapDao);
    }

    /**
     * 用于用户保存临时地图
     * @param userMap
     * @return
     */
    @PostMapping("/api/userMap/saveTemp")
    public RespJson saveTempMap(@RequestBody UserMap userMap) {
        if (Collections.isEmpty(userMap.getRegions())) {
            return RespHelper.errResJson(40010);
        }
        userMapService.saveTempMap(userMap);
        return RespHelper.successResJson();
    }

    /**
     * 获取优化后的绘图type
     */
    @PostMapping("/api/userMap/simpleDrawing")
    public RespJson getSimpleDrawing(@RequestBody ReqSimpleDrawing reqSimpleDrawing) {
        List<RespSimpleDrawing> simpleDrawings = userMapService.getSimpleDrawing(reqSimpleDrawing);
        String type = userMapService.getType(reqSimpleDrawing.getType());
        return RespHelper.successResJson("data", simpleDrawings, "key", type);
    }

    /**
     * 获取用户地图
     * @return
     */
    @GetMapping("/api/userMap")
    public RespJson getUserMap() {
        // 3.获取用户拥有的地图
        List<UserMap> userAllMaps = userMapService.getUserMap();
        List<UserMap> userMaps = userAllMaps.stream()
                .filter(userMap -> userMap.isUnSave() == false)
                .collect(Collectors.toList());
        return RespHelper.successResJson(userMaps);
    }

    /**
     * 保存用户地图
     */
    @PostMapping("/api/userMap")
    public RespJson saveUserMap(@RequestBody @Validated UserMap userMap, BindingResult result) {
        UserMap map = userMapService.getUserMapByName(userMap.getMapName());
        if (map != null){
            return RespHelper.errResJson(42000);
        }
        userMapService.saveMap(userMap);
        return RespHelper.successResJson();
    }


    /**
     * 删除用户地图
     * @return
     */
    @DeleteMapping("/api/userMap/{id}")
    public RespJson deleteUserMap(@PathVariable("id") String id) {
        userMapService.deleteMapById(id);
        return RespHelper.successResJson();
    }

    /**
     * 获取遭遇战地图
     * 遭遇战地图定义 admin 创建 type 是 encounter
     */
    @GetMapping("/api/encounterMap")
    public RespJson getEncounterMap() {
        List<UserMap> encounterMaps = userMapService.getEncounterMaps();
        return RespHelper.successResJson(encounterMaps);
    }

    /**
     * 获取遭遇战地图的 可选则的设定
     * // 包括 初始金额,最大金额 初始人口,最大人口, 所有队伍
     */
    @GetMapping("/api/encounter/initSetting")
    public RespJson getEncounterInitSetting(@RequestParam String uuid) {
        List<String> colors = userMapService.getInitArmy(uuid);
        return RespHelper.successResJson(colors);
    }
    /**
     * 超管权限设置全局的 遭遇战地图和 故事模式
     * @param userMap
     * @param result
     * @return
     */
    @PutMapping("/roots/map")
    public RespJson save(UserMap userMap, BindingResult result) {
        userMapService.updateMap(userMap);
        return RespHelper.successResJson();
    }

}
