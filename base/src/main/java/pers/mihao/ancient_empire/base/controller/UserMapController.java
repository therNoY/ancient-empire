package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.jsonwebtoken.lang.Collections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.MapShowWithConfigDTO;
import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespUserMapDTO;
import pers.mihao.ancient_empire.base.dto.UserMapDraftDTO;
import pers.mihao.ancient_empire.base.entity.*;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.*;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.*;
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
    @Autowired
    UserSettingService userSettingService;


    /**
     * 获取编辑地图时需要获取的初始数据
     *
     * @return
     */
    @GetMapping("/api/userMap/init/{id}")
    public RespJson getInitUserMap(@PathParam("id") String templateId) {
        UserTemplate userTemplate = userTemplateService.getById(templateId);
        // 获取当前用户
        Integer id = AuthUtil.getUserId();
        // 1.获取可用单位信息
        List<UnitMes> unitMesList = unitMesService.getEnableUnitByTempId(id.toString());
        // 2.获取可用地形信息
        List<RegionMes> regionMes = regionMesService.getEnableRegionByTempId(id);
        // 3.获取用户拥有的地图
        List<UserMap> userAllMaps = userMapService.getUserCreateMap(AuthUtil.getUserId());
        List<UserMap> userMaps = userAllMaps.stream()
            .filter(userMap -> BaseConstant.NO.equals(userMap.getUnSave()))
            .collect(Collectors.toList());
        // 3.1 查看是否有未保存的地图
        UserMap unSaveMap = null;
        Optional<UserMap> optional = userAllMaps.stream().findFirst();
        if (optional.isPresent()) {
            unSaveMap = optional.get();
        }
        // 4.获取初始化地图信息
        RespUserMapDTO userMapDao = new RespUserMapDTO(unitMesList, regionMes, unSaveMap, userTemplate);
        return RespUtil.successResJson(userMapDao);
    }

    /**
     * 获取用户的草稿地图 没有就新建一个
     *
     * @return
     */
    @PostMapping("/api/userMap/draft")
    public RespJson getUserDraftEditMap(@RequestBody UserMapDraftDTO requestDTO) {

        Integer templateId;
        UserSetting userSetting = userSettingService.getUserSettingById(requestDTO.getUserId());
        UserTemplate userTemplate;
        UserMap userMap = userMapService.getUserDraftEditMap(requestDTO.getUserId());
        if ((templateId = requestDTO.getTemplateId()) != null && userMap != null) {
            // 使用新的模板 建立新的草稿地图
            userMapService.deleteMapById(userMap.getUuid());
            userMap = null;
        }

        if (userMap != null) {
            templateId = userMap.getTemplateId();
            userTemplate = userTemplateService.getById(templateId);
        } else {
            // 没有指定模板 使用默认模板
            templateId = templateId == null ? userSetting.getMapInitTempId() : templateId;
            userTemplate = userTemplateService.getById(templateId);
            userMap = new UserMap();
            userMap.setCreateTime(LocalDateTime.now());
            userMap.setCreateUserId(requestDTO.getUserId());
            userMap.setRow(userSetting.getMapInitRow());
            userMap.setColumn(userSetting.getMapInitColumn());
            userMap.setUuid(StringUtil.getUUID());
            userMap.setUnSave(BaseConstant.YES);
            userMap.setTemplateId(templateId);
            List<Region> list = new ArrayList<>();
            Region region;
            for (int i = 0; i < userMap.getRow() * userMap.getColumn(); i++) {
                region = new Region();
                region.setType(AppConfig.get("map.init.regionType"));
                list.add(region);
            }
            userMap.setRegions(list);
            userMapService.saveMap(userMap);
        }

        // 1.获取可用单位信息
        List<UnitMes> unitMesList = unitMesService.getEnableUnitByTempId(templateId.toString());
        // 2.获取可用地形信息
        List<RegionMes> regionMes = regionMesService.getEnableRegionByTempId(templateId);
        RespUserMapDTO userMapDao = new RespUserMapDTO(unitMesList, regionMes, userMap, userTemplate);
        userMapDao.setUserSetting(userSetting);
        return RespUtil.successResJson(userMapDao);
    }

    /**
     * 用于用户保存临时地图
     *
     * @param userMap
     * @return
     */
    @PostMapping("/api/userMap/saveTemp")
    public RespJson saveTempMap(@RequestBody UserMap userMap) {
        if (Collections.isEmpty(userMap.getRegions())) {
            return RespUtil.error(40010);
        }
        userMapService.saveUserTempMap(userMap, AuthUtil.getUserId());
        return RespUtil.successResJson();
    }

    /**
     * 获取优化后的绘图type
     */
    @PostMapping("/api/userMap/simpleDrawing")
    public RespJson getSimpleDrawing(@RequestBody ReqSimpleDrawing reqSimpleDrawing) {
        Map<Integer, String> simpleDrawings = userMapService.getSimpleDrawing(reqSimpleDrawing);
        return RespUtil.successResJson(simpleDrawings);
    }

    /**
     * 获取用户创建地图列表
     *
     * @return
     */
    @PostMapping("/api/userMap/list")
    public RespJson getUserCreateMap(@RequestBody ApiConditionDTO condition) {
        // 获取用户拥有的地图
        IPage<BaseMapInfoVO> userAllMaps = userMapService.getUserCreateMapWithPage(condition.getUserId(), condition);
        return RespUtil.successPageResJson(userAllMaps);
    }

    /**
     * 获取用户下载地图
     *
     * @param apiConditionDTO
     * @return
     */
    @PostMapping("/api/userMap/download/list")
    public RespJson getUserDownloadMapList(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<BaseMapInfoVO> mapList = userMapService.getUserDownloadMapWithPage(apiConditionDTO);
        return RespUtil.successPageResJson(mapList);
    }

    /**
     * 获取用户地图列表
     *
     * @return
     */
    @GetMapping("/api/userMap/{id}")
    public RespJson getUserCreateMap(@PathVariable("id") String id) {
        // 3.获取用户拥有的地图
        UserMap map = userMapService.getUserMapById(id);
        return RespUtil.successResJson(map);
    }

    /**
     * 获取用户地图列表
     *
     * @return
     */
    @PostMapping("/api/userMap/withConfig")
    public RespJson getUserMapWithConfig(@RequestBody MapShowWithConfigDTO config) {
        // 3.获取用户拥有的地图
        UserMap map = userMapService.getUserMapById(config.getMapId());
        if (CollectionUtil.isNotEmpty(config.getArmyConfigList())) {
            List<BaseUnit> baseUnits = new ArrayList<>();
            for (BaseUnit baseUnit : map.getUnits()) {
                boolean isRemove = false;
                for (ArmyConfig armyConfig : config.getArmyConfigList()) {
                    if (armyConfig.getColor().equals(baseUnit.getColor()) && armyConfig.getType()
                        .equals(ArmyEnum.NO.type())) {
                        isRemove = true;
                        break;
                    }
                }
                if (!isRemove) {
                    baseUnits.add(baseUnit);
                }
            }
            map.setUnits(baseUnits);
        }
        return RespUtil.successResJson(map);
    }


    /**
     * 保存用户地图
     */
    @PostMapping("/api/userMap")
    public RespJson saveUserMap(@RequestBody @Validated UserMap userMap, BindingResult result) {
        // 管理员创建地图默认是遭遇战地图
        if (AuthUtil.getUserId().equals(1)) {
            // TODO 后面去掉
            userMap.setType(GameTypeEnum.ENCOUNTER.type());
        }
        if (StringUtil.isNotBlack(userMap.getUuid())) {
            UserMap map = userMapService.getUserMapById(userMap.getUuid());
            map.setRegions(userMap.getRegions());
            map.setUnits(userMap.getUnits());
            userMapService.updateUserMapById(map);
        } else {
            UserMap map = userMapService.getUserMapByName(userMap.getMapName());
            if (map != null) {
                return RespUtil.error(42000);
            }
            userMap.setUnSave(BaseConstant.NO);
            userMapService.saveMap(userMap);
        }
        return RespUtil.successResJson();
    }


    /**
     * 删除用户地图
     *
     * @return
     */
    @DeleteMapping("/api/userMap/{id}")
    public RespJson deleteUserMap(@PathVariable("id") String id) {
        userMapService.deleteMapById(id);
        return RespUtil.successResJson();
    }

    /**
     * 获取遭遇战地图 遭遇战地图定义 admin 创建 type 是 encounter
     */
    @GetMapping("/encounterMap")
    public RespJson getEncounterMap() {
        List<BaseMapInfoVO> encounterMaps = userMapService.getEncounterMaps();
        return RespUtil.successResJson(encounterMaps);
    }

    /**
     * 获取可以下子的地图
     * @param apiConditionDTO
     * @return
     */
    @GetMapping("/api/worldMap/list")
    public RespJson getWorldMapList(ApiConditionDTO apiConditionDTO) {
        List<UserMap> mapList = userMapService.getWorldMapList(apiConditionDTO);
        return RespUtil.successResJson(mapList);
    }

    /**
     * 获取遭遇战地图的 可选则的设定 包括 初始金额,最大金额 初始人口,最大人口, 所有队伍
     */
    @GetMapping("/encounter/initSetting")
    public RespJson getEncounterInitSetting(@RequestParam String uuid) {
        List<String> colors = userMapService.getInitArmy(uuid);
        return RespUtil.successResJson(colors);
    }

    @GetMapping("/map/store/list")
    public RespJson getStoreMapList(){
        List<UserMap> userMaps = userMapService.getStoreMapList();
        return RespUtil.successResJson(userMaps);
    }

    /**
     * 超管权限设置全局的 遭遇战地图和 故事模式
     *
     * @param userMap
     * @param result
     * @return
     */
    @PutMapping("/roots/map")
    public RespJson save(UserMap userMap, BindingResult result) {
        userMapService.updateUserMapById(userMap);
        return RespUtil.successResJson();
    }

}
