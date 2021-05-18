package pers.mihao.ancient_empire.base.service.impl;

import static pers.mihao.ancient_empire.common.constant.CatchKey.MAP_MAX_VERSION;
import static pers.mihao.ancient_empire.common.constant.CatchKey.USER_MAP;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.enums.UserEnum;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dao.UserMapAttentionDao;
import pers.mihao.ancient_empire.base.dao.UserMapDAO;
import pers.mihao.ancient_empire.base.dto.CountSumDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveMap;
import pers.mihao.ancient_empire.base.dto.ReqDoPaintingDTO;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

@Service
public class UserMapServiceImp extends ServiceImpl<UserMapDAO, UserMap> implements UserMapService {

    private static final String SEA = "sea";
    private static final String LAND = "land";
    private static final String BRIDGE = "bridge";
    private static final String BANK = "bank";
    private static final String BANK_ = "bank_";

    @Value("${army.max}")
    Integer maxArmy;
    @Autowired
    UserMapDAO userMapDAO;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserService userService;
    @Autowired
    UserMapAttentionDao userMapAttentionDao;
    @Autowired
    UserTemplateService userTemplateService;

    /**
     * 从mongo 中获取用户创建的地图
     *
     * @return
     */
    @Override
    public List<UserMap> getUserCreateMap(Integer userId) {
        return userMapDAO.findByCreateUserId(userId).stream()
                .filter(userMap -> BaseConstant.NO.equals(userMap.getUnSave()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户创建的地图 分页
     *
     * @return
     */
    @Override
    public IPage<BaseMapInfoVO> getUserCreateMapWithPage(ApiConditionDTO apiConditionDTO) {
        List<BaseMapInfoVO> userMap = userMapDAO.selectUserCreateMapWithPage(apiConditionDTO);
        setBaseMapInfo(userMap);
        return IPageHelper.toPage(userMap, apiConditionDTO);
    }



    /**
     * 获取优化后的地形
     * 如果要画的是海 但是在一块陆地上画就需要 返回一个池塘
     * 如果要画的是陆地 但是要在海里画 就返回一个小岛
     * <p>
     * 思路 九宫格模型
     * 当前需要绘画的是九宫格的中心 然后 左上 到右下 一次是 1到8  编号
     *
     * @param reqDoPaintingDTO
     * @return
     */
    @Override
    public Map<Integer, String> getSimpleDrawing(ReqDoPaintingDTO reqDoPaintingDTO) {
        Map<Integer, String> simpleDrawings = new HashMap<>(16);
        simpleDrawings.put(reqDoPaintingDTO.getIndex(), reqDoPaintingDTO.getType());

        Integer index = reqDoPaintingDTO.getIndex();
        String type = reqDoPaintingDTO.getType();
        Integer column = reqDoPaintingDTO.getColumn();
        List<Region> regionList = reqDoPaintingDTO.getRegionList();
        regionList.get(index).setType(reqDoPaintingDTO.getType());

        // 判断当前是不是 海
        if (isSea(type)) {
            // 画海 获取周围陆地的信息
            Map<Integer, Integer> map = getAroundLandMap(index, regionList, column);
            if (map.size() == 0) {
                // 周围没有陆地 首先当前设置为sea 不需要有bank
                regionList.get(index).setType(SEA);
                // 优化周围海洋
                optimizationAroundSea(simpleDrawings, index, column, regionList);
            } else {
                // 周围有陆地 首先获取当前的名字
                StringBuffer bankName = new StringBuffer(BANK_);
                map.keySet().stream().sorted()
                        .collect(Collectors.toList())
                        .forEach(integer -> bankName.append(integer));
                simpleDrawings.put(index, bankName.toString());
                regionList.get(index).setType(bankName.toString());
                // 优化周围的海洋
                optimizationAroundSea(simpleDrawings, index, column, regionList);
            }
            return simpleDrawings;
        } else if (type.startsWith(BRIDGE)) {

        } else {
            optimizationAroundSea(simpleDrawings, index, column, regionList);
            return simpleDrawings;
        }
        return simpleDrawings;
    }



    /**
     * 保存地图
     *
     * @param userMap
     */
    @Override
    public void saveMap(UserMap userMap) {
        userMap.setUuid(StringUtil.getUUID());
        userMap.setCreateUserId(LoginUserHolder.getUserId());
        userMap.setCreateTime(LocalDateTime.now());
        saveOrUpdate(userMap);
        removeUserMapCatch(userMap);
    }

    @Override
    public UserMap getUserMapByName(String mapName) {
        UserMap userMap = userMapDAO.getFirstByCreateUserIdAndMapName(LoginUserHolder.getUserId(), mapName);
        return userMap;
    }

    @Override
    public void deleteMapById(String id) {
        UserMap userMap = userMapDAO.selectById(id);
        if (userMap.getStatus().equals(VersionConstant.DRAFT)) {
            removeById(userMap.getUuid());
        }
        QueryWrapper<UserMap> wrapper = new QueryWrapper<>();
        wrapper.eq("map_type", userMap.getMapType());
        List<UserMap> historyMap = userMapDAO.selectList(wrapper);
        for (UserMap map : historyMap) {
            map.setStatus(VersionConstant.DELETE);
            map.setUpdateTime(LocalDateTime.now());
            map.setCreateUserId(map.getCreateUserId() * -1);
            userMapDAO.updateById(map);
        }
        removeUserMapCatch(userMap);
    }

    /**
     * 设置地图类型
     *
     * @param userMap
     */
    @Override
    public void updateUserMapById(UserMap userMap) {
        saveOrUpdate(userMap);
        removeUserMapCatch(userMap);
    }



    /**
     * 获取所有遭遇战 地图
     *
     * @return
     */
    @Override
    public IPage<BaseMapInfoVO> getEncounterMaps(ApiConditionDTO apiConditionDTO) {
        List<BaseMapInfoVO> encounterMaps = userMapDAO.getEncounterMapsWithPage(apiConditionDTO);
        return IPageHelper.toPage(encounterMaps, apiConditionDTO);
    }

    @Override
    public List<String> getInitArmy(String uuid) {
        UserMap userMap = getEncounterMapById(uuid);
        if (userMap == null) {
            return null;
        }
        List<String> colors = new ArrayList<>();
        List<Region> regions = userMap.getRegions();
        List<BaseUnit> units = userMap.getUnits();
        units.forEach(unit -> {
            if (!colors.contains(unit.getColor())) {
                colors.add(unit.getColor());
            }
        });
        if (colors.size() < maxArmy) {
            regions.forEach(baseSquare -> {
                if ((baseSquare.getType().equals("castle") || baseSquare.getType().equals("town")) && !colors.contains(baseSquare.getColor())) {
                    if (Strings.isNotBlank(baseSquare.getColor())) {
                        colors.add(baseSquare.getColor());
                    }
                }
            });
        }
        return colors;
    }

    @Override
    public UserMap getEncounterMapById(String uuid) {
        UserMap userMap = userMapDAO.selectById(uuid);
        if (GameTypeEnum.ENCOUNTER.type().equals(userMap.getType())) {
            // 不是遭遇地图
            return null;
        }
        return userMap;
    }

    /**
     * 查询用户
     *
     * @param uuid
     * @return
     */
    @Cacheable(CatchKey.USER_MAP)
    @Override
    public UserMap getUserMapById(String uuid) {
        return userMapDAO.selectById(uuid);
    }


    @Override
    public List<UserMap> getStoreMapList() {
        QueryWrapper<UserMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "store");
        queryWrapper.orderBy(true , true, "create_time");
        return userMapDAO.selectList(queryWrapper);
    }

    @Override
    public IPage<BaseMapInfoVO> getUserDownloadMapWithPage(ApiConditionDTO apiConditionDTO) {
        List<BaseMapInfoVO> userMaps = userMapAttentionDao.getUserDownloadMapWithPage(apiConditionDTO);
        setBaseMapInfo(userMaps);
        return IPageHelper.toPage(userMaps, apiConditionDTO);
    }

    /**
     * 返回VO
     * @param userMaps
     * @return
     */
    private void setBaseMapInfo(List<BaseMapInfoVO> userMaps) {
        UserMap userMap;
        CountSumDTO countSumDTO;
        for (BaseMapInfoVO infoVO : userMaps) {
            infoVO.setMapId(infoVO.getUuid());
            infoVO.setCreateUserName(userService.getUserById(Math.abs(infoVO.getCreateUserId())).getName());
            infoVO.setTemplateName(userTemplateService.getTemplateById(infoVO.getTemplateId()).getTemplateName());

            if (infoVO.getStartCount() == null) {
                countSumDTO = userMapService.selectCountStartByMapType(infoVO.getMapType());
                infoVO.setStartCount(countSumDTO.getSum() == null ? 0 : countSumDTO.getSum());
                infoVO.setDownLoadCount(countSumDTO.getCount());
            }



            if (infoVO.getMaxVersion() == null) {
                userMap = userMapService.getMaxVersionMapByMapType(infoVO.getMapType());
                if (userMap != null) {
                    infoVO.setMaxVersion(userMap.getVersion());
                }
            }
        }
    }


    @Override
    public IPage<BaseMapInfoVO> getWorldMapList(ApiConditionDTO apiConditionDTO) {
        List<BaseMapInfoVO> userMaps = userMapAttentionDao.getDownloadAbleMapWithPage(apiConditionDTO);
        UserMap userMap;
        for (BaseMapInfoVO mapInfoVO : userMaps) {
            userMap = userMapService.getMaxVersionMapByMapType(mapInfoVO.getMapType());
            BeanUtil.copyValueFromParent(userMap, mapInfoVO);
            mapInfoVO.setMaxVersion(userMap.getVersion());
        }
        setBaseMapInfo(userMaps);
        return IPageHelper.toPage(userMaps, apiConditionDTO);
    }

    @Override
    @Cacheable(CatchKey.MAP_MAX_VERSION)
    public UserMap getMaxVersionMapByMapType(String mapType) {
        Integer version = userMapDAO.getMaxVersionByMapType(mapType);
        QueryWrapper<UserMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("map_type", mapType)
            .eq("version", version);
        List<UserMap> userMaps = userMapDAO.selectList(queryWrapper);
        if (userMaps == null || userMaps.size() == 0) {
            return null;
        }
        return userMaps.get(0);
    }

    @Override
    public CountSumDTO selectCountStartByMapType(String mapType) {
        return userMapDAO.selectCountStartByMapType(mapType);
    }

    @Override
    public UserMap getLastEditMapById(Integer userId) {
        return userMapDAO.getLastEditMapById(userId);
    }

    @Override
    public void saveUserMap(ReqSaveMap reqSaveMap) {
        UserMap userMap = (UserMap) BeanUtil.copyValueToParent(reqSaveMap, UserMap.class);
        // 默认是遭遇战地图
        if (StringUtil.isBlack(userMap.getType())) {
            userMap.setType(GameTypeEnum.ENCOUNTER.type());
        }
        if (StringUtil.isNotBlack(userMap.getUuid())) {
            UserMap map = userMapService.getUserMapById(userMap.getUuid());
            map.setRegions(userMap.getRegions());
            map.setUnits(userMap.getUnits());
            map.setMapName(reqSaveMap.getMapName());
            map.setShare(reqSaveMap.getShare());

            if (map.getStatus().equals(VersionConstant.DRAFT)) {
                // 原本就是草稿状态
                map.setStatus(reqSaveMap.getOptType());
                map.setUpdateTime(LocalDateTime.now());
                userMapService.updateById(map);
            } else if (map.getStatus().equals(VersionConstant.OFFICIAL)) {
                // 原来是正式版本
                map.setVersion(map.getVersion() + 1);
                map.setCreateTime(LocalDateTime.now());
                map.setUpdateTime(LocalDateTime.now());
                map.setUuid(null);
                map.setStatus(reqSaveMap.getOptType());
                userMapService.save(map);
                // 设置新的ID
                map.setUuid(map.getUuid());
            }
            removeUserMapCatch(map);
        } else {
            userMap.setVersion(0);
            userMap.setStatus(reqSaveMap.getOptType());
            userMap.setCreateUserId(LoginUserHolder.getUserId());
            userMap.setCreateTime(LocalDateTime.now());
            userMap.setUpdateTime(LocalDateTime.now());
            userMapService.saveMap(userMap);
            removeUserMapCatch(userMap);
        }

    }

    private void removeUserMapCatch(UserMap userMap){
        userMapService.removeMapCatch(userMap.getUuid());
        if (GameTypeEnum.ENCOUNTER.type().equals(userMap.getType())) {
            userMapService.delEncounterMapsCatch();
        }
        userMapService.removeMaxVersionCatch(userMap.getMapType());
    }

    @Override
    @CacheEvict(USER_MAP)
    public void removeMapCatch(String uuid){}

    @Override
    @CacheEvict(MAP_MAX_VERSION)
    public void removeMaxVersionCatch(String type){}

    @Override
    @CacheEvict(CatchKey.ENCOUNTER_MAP)
    public void delEncounterMapsCatch(){}


    /**
     * 找到index 周围的海洋
     *
     * @param index
     * @param regionList
     * @param column
     * @return
     */
    private Map<Integer, Integer> getAroundSeaMap(int index, List<Region> regionList, int column) {
        Map<Integer, Integer> isSeaMap = new HashMap<>();

        // 1. 判断上面的地形
        int up = index - column;
        // 判断 1 号
        if (up > 0) {
            // 有上
            if (isSea(regionList.get(up).getType())) {
                // 如果上面的是海洋
                isSeaMap.put(2, up);
            }
            // 有左上
            if (up % column != 0) {
                if (isSea(regionList.get(up - 1).getType())) {
                    // 判断左上角是不是海
                    isSeaMap.put(1, up - 1);
                }
            }
            // 有右上
            if ((up + 1) % column != 0) {
                if (isSea(regionList.get(up + 1).getType())) {
                    // 判断左上角是不是海
                    isSeaMap.put(3, up + 1);
                }
            }
        }
        // 2. 判断右边面的地形
        int right = index + 1;
        if ((right) % column != 0) {
            // 有右边
            if (isSea(regionList.get(right).getType())) {
                // 如果右边是陆地
                isSeaMap.put(5, right);
            }
            // 判断有右下
            if ((right + column + 1) < regionList.size()) {
                // 判断有右下
                if (isSea(regionList.get(right + column).getType())) {
                    // 判断右下是海
                    isSeaMap.put(8, right + column);
                }
            }
        }

        // 判断有下
        int down = index + column;
        if (down < regionList.size()) {
            if (isSea(regionList.get(down).getType())) {
                // 如果下面是陆地
                isSeaMap.put(7, down);
            }
            // 判断有左下
            if (down % column != 0) {
                // 判断有右下
                if (isSea(regionList.get(down - 1).getType())) {
                    // 判断左上角是不是陆地
                    isSeaMap.put(6, down - 1);
                }
            }
        }

        // 4.判断有做
        int left = index - 1;
        if (index % column != 0) {
            if (isSea(regionList.get(left).getType())) {
                // 如果左面是陆地
                isSeaMap.put(4, left);
            }
        }

        return isSeaMap;
    }


    /**
     * 获取陆地周围陆地
     *
     * @param index
     * @param regionList
     * @param column
     * @return
     */
    private Map<Integer, Integer> getAroundLandMap(int index, List<Region> regionList, int column) {
        Map<Integer, Integer> isLandMap = new HashMap<>();

        // 1. 判断上面的地形
        int up = index - column;
        if (index >= column) {
            if (isLand(regionList.get(up).getType())) {
                // 如果上面的是陆地
                isLandMap.put(2, up);
            } else {
                // 判断左上
                if (up % column != 0) {
                    if (isLand(regionList.get(up - 1).getType())) {
                        // 判断左上角是不是陆地
                        isLandMap.put(1, up - 1);
                    }
                }
            }
        }

        // 2.判断右边的地形
        int right = index + 1;
        if (right % column != 0) {
            if (isLand(regionList.get(right).getType())) {
                // 如果右边是陆地
                isLandMap.put(5, right);
            } else {
                // 判断右上
                if (!isLandMap.containsKey(2) && right > column) {
                    // 上不是陆地 并且上面还有 才判断
                    if (isLand(regionList.get(right - column).getType())) {
                        // 判断左上角是不是陆地
                        isLandMap.put(3, right - column);
                    }
                }
            }
        }

        // 3.判断下面是不是陆地
        int down = index + column;
        if (down < regionList.size()) {
            if (isLand(regionList.get(down).getType())) {
                // 如果下面是陆地
                isLandMap.put(7, down);
            } else {
                // 判断右下
                if (!isLandMap.containsKey(5) && (down + 1) % column != 0) {
                    // 上不是陆地 并且上面还有 才判断
                    if (isLand(regionList.get(down + 1).getType())) {
                        // 判断左上角是不是陆地
                        isLandMap.put(8, right - column);
                    }
                }
            }
        }

        // 4.判断左边面是不是陆地
        int left = index - 1;
        if (index % column != 0) {
            if (isLand(regionList.get(left).getType())) {
                // 如果左面是陆地
                isLandMap.put(4, left);
                if (isLandMap.containsKey(1)) {
                    isLandMap.remove(1);
                }
            } else {
                // 判断左下
                if (!isLandMap.containsKey(7) && (left + column) < regionList.size()) {
                    // 上不是陆地 并且上面还有 才判断
                    if (isLand(regionList.get(left + column).getType())) {
                        // 判断左上角是不是陆地
                        isLandMap.put(6, left + column);
                    }
                }
            }
        }
        return isLandMap;
    }

    /**
     * 优化周围的海洋
     *
     * @param simpleDrawings
     * @param index
     * @param column
     * @param regionList
     */
    private void optimizationAroundSea(Map<Integer, String> simpleDrawings, Integer index, Integer column, List<Region> regionList) {
        Map<Integer, Integer> seaMap = getAroundSeaMap(index, regionList, column);
        for (Map.Entry<Integer, Integer> entry : seaMap.entrySet()) {
            Map<Integer, Integer> seaLandMap = getAroundLandMap(entry.getValue(), regionList, column);
            if (seaLandMap.size() == 0) {
                if (regionList.get(entry.getValue()).getType().startsWith(BANK)) {
                    simpleDrawings.put(entry.getValue(), SEA);
                    regionList.get(entry.getValue()).setType(SEA);

                }
                continue;
            }
            StringBuffer aroundBankName = new StringBuffer(BANK_);
            seaLandMap.keySet().stream().sorted()
                .collect(Collectors.toList())
                .forEach(integer -> aroundBankName.append(integer));
            simpleDrawings.put(entry.getValue(), aroundBankName.toString());
            regionList.get(entry.getValue()).setType(aroundBankName.toString());
        }
    }

    private boolean isSea(String type) {
        return type.startsWith(SEA) || type.startsWith(BANK);
    }

    private boolean isLand(String type) {
        return !type.startsWith(SEA) && !type.startsWith(BRIDGE) && !type.startsWith(BANK);
    }

}
