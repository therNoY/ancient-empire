package pers.mihao.ancient_empire.base.controller;


import io.jsonwebtoken.lang.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespUserMapDTO;
import pers.mihao.ancient_empire.base.entity.*;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.*;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.base.vo.UserMapVo;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import javax.websocket.server.PathParam;

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
    UserTemplateService userTemplateService;

    /**
     * 获取编辑地图时需要获取的初始数据
     * FIXME 增加了模板配置这个要改
     * @return
     */
    @GetMapping("/api/userMap/init/{id}")
    public RespJson getInitUserMap(@PathParam("id") String templateId) {
        UserTemplate userTemplate = userTemplateService.getById(templateId);
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
        RespUserMapDTO userMapDao = new RespUserMapDTO(unitMesList, regionMes, userMaps, unSaveMap, userTemplate);
        return RespUtil.successResJson(userMapDao);
    }

    /**
     * 用于用户保存临时地图
     * @param userMap
     * @return
     */
    @PostMapping("/api/userMap/saveTemp")
    public RespJson saveTempMap(@RequestBody UserMap userMap) {
        if (Collections.isEmpty(userMap.getRegions())) {
            return RespUtil.error(40010);
        }
        userMapService.saveTempMap(userMap);
        return RespUtil.successResJson();
    }

    /**
     * 获取优化后的绘图type
     */
    @PostMapping("/api/userMap/simpleDrawing")
    public RespJson getSimpleDrawing(@RequestBody ReqSimpleDrawing reqSimpleDrawing) {
        List<RespSimpleDrawing> simpleDrawings = userMapService.getSimpleDrawing(reqSimpleDrawing);
        String type = userMapService.getType(reqSimpleDrawing.getType());
        return RespUtil.successResJson("data", simpleDrawings, "key", type);
    }

    /**
     * 获取用户地图列表
     * @return
     */
    @GetMapping("/api/userMap/list")
    public RespJson getUserMap() {
        // 3.获取用户拥有的地图
        List<UserMap> userAllMaps = userMapService.getUserMap();
        List<UserMap> userMaps = userAllMaps.stream()
                .filter(userMap -> userMap.isUnSave() == false)
                .collect(Collectors.toList());
        return RespUtil.successResJson(userMaps);
    }

    /**
     * 获取用户地图列表
     * @return
     */
    @GetMapping("/api/userMap/{id}")
    public RespJson getUserMap(@PathVariable("id") String id) {
        // 3.获取用户拥有的地图
        UserMapVo map = userMapService.getUserMapById(id);
        return RespUtil.successResJson(map);
    }


    /**
     * 保存用户地图
     */
    @PostMapping("/api/userMap")
    public RespJson saveUserMap(@RequestBody @Validated UserMap userMap, BindingResult result) {
        UserMap map = userMapService.getUserMapByName(userMap.getMapName());
        if (map != null){
            return RespUtil.error(42000);
        }
        // 管理员创建地图默认是遭遇战地图
        if (AuthUtil.getAuthId().equals(1)) {
            userMap.setType(GameTypeEnum.ENCOUNTER.type());
            userMapService.saveMap(userMap);
        }else {
            userMapService.saveMap(userMap);
        }
        return RespUtil.successResJson();
    }


    /**
     * 删除用户地图
     * @return
     */
    @DeleteMapping("/api/userMap/{id}")
    public RespJson deleteUserMap(@PathVariable("id") String id) {
        userMapService.deleteMapById(id);
        return RespUtil.successResJson();
    }

    /**
     * 获取遭遇战地图
     * 遭遇战地图定义 admin 创建 type 是 encounter
     */
    @GetMapping("/encounterMap")
    public RespJson getEncounterMap() {
        List<BaseMapInfoVO> encounterMaps = userMapService.getEncounterMaps();
        return RespUtil.successResJson(encounterMaps);
    }

    /**
     * 获取遭遇战地图的 可选则的设定
     * // 包括 初始金额,最大金额 初始人口,最大人口, 所有队伍
     */
    @GetMapping("/encounter/initSetting")
    public RespJson getEncounterInitSetting(@RequestParam String uuid) {
        List<String> colors = userMapService.getInitArmy(uuid);
        return RespUtil.successResJson(colors);
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
        return RespUtil.successResJson();
    }

}
