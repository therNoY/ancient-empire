package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.MapShowWithConfigDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveMap;
import pers.mihao.ancient_empire.base.dto.ReqDoPaintingDTO;
import pers.mihao.ancient_empire.base.dto.RespUserMapDTO;
import pers.mihao.ancient_empire.base.dto.UserMapDraftDTO;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.service.AeSequenceService;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.common.vo.RespJson;

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
    @Autowired
    AeSequenceService aeSequenceService;


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
     * 获取优化后的绘图type
     */
    @PostMapping("/api/userMap/simpleDrawing")
    public RespJson getSimpleDrawing(@RequestBody ReqDoPaintingDTO reqDoPaintingDTO) {
        Map<Integer, String> simpleDrawings = userMapService.getSimpleDrawing(reqDoPaintingDTO);
        return RespUtil.successResJson(simpleDrawings);
    }

    /**
     * TODO 地图回退恢复功能 实现思路通过simpleDrawing 记录修改的结果 第一次修改记录两次 之后每次修改都只记录一次
     */
    @PostMapping("/api/userMap/getHistoryMap")
    public RespJson getHistoryMap(@RequestBody ReqDoPaintingDTO reqDoPaintingDTO) {
        return RespUtil.successResJson();
    }

    /**
     * 获取用户创建地图列表
     *
     * @return
     */
    @PostMapping("/api/userMap/list")
    public RespJson getUserCreateMap(@RequestBody ApiConditionDTO condition) {
        // 获取用户拥有的地图
        IPage<BaseMapInfoVO> userAllMaps = userMapService.getUserCreateMapWithPage(condition);
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
     * 修改地图基本信息 不修改版本
     *
     * @param userMap
     * @return
     */
    @PostMapping("/api/userMap/changeBaseInfo")
    public RespJson changeMapBaseInfo(@RequestBody UserMap userMap) {
        UserMap map = userMapService.getUserMapById(userMap.getUuid());
        if (!map.getCreateUserId().equals(AuthUtil.getUserId())) {
            throw new AncientEmpireException("权限不足");
        }
        map.setMapName(userMap.getMapName());
        map.setShare(userMap.getShare());
        map.setUpdateTime(LocalDateTime.now());
        userMapService.updateUserMapById(userMap);
        return RespUtil.successResJson();
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
    public RespJson saveUserMap(@RequestBody ReqSaveMap reqSaveMap) {
        userMapService.saveUserMap(reqSaveMap);
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
    @PostMapping("/encounterMap")
    public RespJson getEncounterMap(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<BaseMapInfoVO> encounterMaps = userMapService.getEncounterMaps(apiConditionDTO);
        return RespUtil.successPageResJson(encounterMaps);
    }

    /**
     * 获取可以下子的地图
     *
     * @param apiConditionDTO
     * @return
     */
    @PostMapping("/api/worldMap/list")
    public RespJson getWorldMapList(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<BaseMapInfoVO> mapList = userMapService.getWorldMapList(apiConditionDTO);
        return RespUtil.successPageResJson(mapList);
    }

    /**
     * 获取遭遇战地图的 可选则的设定 包括 初始金额,最大金额 初始人口,最大人口, 所有队伍
     */
    @GetMapping("/encounter/initSetting")
    public RespJson getEncounterInitSetting(@RequestParam String uuid) {
        List<String> colors = userMapService.getInitArmy(uuid);
        return RespUtil.successResJson(colors);
    }

    /**
     * 获取故事模式地图
     *
     * @return
     */
    @GetMapping("/map/store/list")
    public RespJson getStoreMapList() {
        List<UserMap> userMaps = userMapService.getStoreMapList();
        return RespUtil.successResJson(userMaps);
    }

    /**
     * 获取最后编辑的地图
     *
     * @param requestDTO
     * @return
     */
    @RequestMapping("/api/userMap/lastEdit")
    public RespJson getLastEditMap(@RequestBody ApiRequestDTO requestDTO) {
        Integer templateId;
        UserTemplate userTemplate;

        UserSetting userSetting = userSettingService.getUserSettingById(requestDTO.getUserId());
        UserMap userMap = userMapService.getLastEditMapById(requestDTO.getUserId());

        if (userMap != null) {
            templateId = userMap.getTemplateId();
            userTemplate = userTemplateService.getById(templateId);
        } else {
            // 没有指定模板 使用默认模板
            templateId = userSetting.getMapInitTempId();
            userTemplate = userTemplateService.getById(templateId);
            userMap = new UserMap();
            userMap.setCreateUserId(requestDTO.getUserId());
            userMap.setRow(userSetting.getMapInitRow());
            userMap.setColumn(userSetting.getMapInitColumn());
            setMapBaseInfo(userSetting, templateId, userMap);
            userMapService.saveMap(userMap);
        }

        RespUserMapDTO userMapDao = getRespUserMapDTO(userTemplate, userSetting, userMap);
        return RespUtil.successResJson(userMapDao);
    }

    private RespUserMapDTO getRespUserMapDTO(UserTemplate userTemplate, UserSetting userSetting, UserMap userMap) {
        // 1.获取可用单位信息
        List<UnitMes> unitMesList = unitMesService.getEnableUnitByTempId(userTemplate.getId().toString());
        // 2.获取可用地形信息
        List<RegionMes> regionMes = regionMesService.getEnableRegionByTempId(userTemplate.getId());
        RespUserMapDTO userMapDao = new RespUserMapDTO(unitMesList, regionMes, userMap, userTemplate);
        userMapDao.setUserSetting(userSetting);
        return userMapDao;
    }


    /**
     * @param userMap
     * @param result
     * @return
     */
    @PostMapping("/api/userMap/createDraftMap")
    public RespJson createDraftMap(@RequestBody UserMapDraftDTO userMapDraftDTO) {
        UserSetting userSetting = userSettingService.getUserSettingById(userMapDraftDTO.getUserId());
        if (userMapDraftDTO.getTemplateId() == null) {
            userMapDraftDTO.setTemplateId(userSetting.getMapInitTempId());
        }
        if (userMapDraftDTO.getMapRow() == null) {
            userMapDraftDTO.setMapRow(userSetting.getMapInitRow());
        }
        if (userMapDraftDTO.getMapColumn() == null) {
            userMapDraftDTO.setMapColumn(userSetting.getMapInitColumn());
        }

        // 没有指定模板 使用默认模板
        Integer templateId = userMapDraftDTO.getTemplateId();
        UserTemplate userTemplate = userTemplateService.getById(templateId);
        UserMap userMap = new UserMap();
        userMap.setMapName(userMapDraftDTO.getMapName());
        userMap.setCreateUserId(userMapDraftDTO.getUserId());
        userMap.setRow(userMapDraftDTO.getMapRow());
        userMap.setColumn(userMapDraftDTO.getMapColumn());
        setMapBaseInfo(userSetting, templateId, userMap);
        userMapService.saveMap(userMap);

        RespUserMapDTO userMapDao = getRespUserMapDTO(userTemplate, userSetting, userMap);
        return RespUtil.successResJson(userMapDao);
    }

    private void setMapBaseInfo(UserSetting userSetting, Integer templateId, UserMap userMap) {
        userMap.setCreateTime(LocalDateTime.now());
        userMap.setUpdateTime(LocalDateTime.now());
        userMap.setMapType(BaseConstant.MAP_TYPE + aeSequenceService.getNewIdByType(BaseConstant.USER_MAP_SEQ));
        userMap.setUuid(StringUtil.getUUID());
        userMap.setVersion(0);
        userMap.setStatus(VersionConstant.DRAFT);
        userMap.setTemplateId(templateId);
        List<Region> list = new ArrayList<>();
        Region region;
        for (int i = 0; i < userMap.getRow() * userMap.getColumn(); i++) {
            region = new Region();
            region.setType(userSetting.getMapInitRegionType());
            list.add(region);
        }
        userMap.setRegions(list);
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
