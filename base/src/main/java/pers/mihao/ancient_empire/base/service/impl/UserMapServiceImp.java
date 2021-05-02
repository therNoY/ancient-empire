package pers.mihao.ancient_empire.base.service.impl;

import static pers.mihao.ancient_empire.common.constant.CatchKey.USER_CREATE_MAP;
import static pers.mihao.ancient_empire.common.constant.CatchKey.USER_MAP;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.enums.UserEnum;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dao.UserMapDAO;
import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.common.annotation.redis.NotGenerator;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.base.dao.UserMapAttentionDao;

@Service
public class UserMapServiceImp extends ComplexKeyServiceImpl<UserMapDAO, UserMap> implements UserMapService {

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
    UserMapAttentionDao userMapAttentionDao;

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
    public IPage<BaseMapInfoVO> getUserCreateMapWithPage(Integer id, @NotGenerator ApiConditionDTO apiConditionDTO) {
        List<UserMap> userMap = userMapDAO.selectUserCreateMapWithPage(apiConditionDTO);
        List<BaseMapInfoVO> resultMap = userMap.stream().map(e->{
            BaseMapInfoVO infoVO = new BaseMapInfoVO();
            BeanUtil.copyValueByGetSet(e, infoVO);
            infoVO.setMapId(e.getUuid());
            infoVO.setCreateTime(DateUtil.formatDataTime(e.getCreateTime()));
            return infoVO;
        }).collect(Collectors.toList());

        return IPageHelper.toPage(resultMap, apiConditionDTO);
    }





    /**
     * 保存临时地图
     *
     * @param userMap
     */
    @Override
    @Transactional
    public void saveUserTempMap(UserMap userMap, Integer userId) {
        // 1.删除临时的地图
        UserMap map = userMapDAO.getFirstByCreateUserIdAndUnSave(userId);
        if (map != null) {
            userMap.setUuid(map.getUuid());
            userMap.setCreateTime(LocalDateTime.now());
            userMap.setMapName("temp");
            userMap.setCreateUserId(map.getCreateUserId());
            userMap.setUnSave(BaseConstant.YES);
            updateUserMapById(userMap);
        }
    }

    /**
     * 获取优化后的地形
     * 如果要画的是海 但是在一块陆地上画就需要 返回一个池塘
     * 如果要画的是陆地 但是要在海里画 就返回一个小岛
     * <p>
     * 思路 九宫格模型
     * 当前需要绘画的是九宫格的中心 然后 左上 到右下 一次是 1到8  编号
     *
     * @param reqSimpleDrawing
     * @return
     */
    @Override
    public Map<Integer, String> getSimpleDrawing(ReqSimpleDrawing reqSimpleDrawing) {
        Map<Integer, String> simpleDrawings = new HashMap<>(16);
        simpleDrawings.put(reqSimpleDrawing.getIndex(), reqSimpleDrawing.getType());

        Integer index = reqSimpleDrawing.getIndex();
        String type = reqSimpleDrawing.getType();
        Integer column = reqSimpleDrawing.getColumn();
        List<Region> regionList = reqSimpleDrawing.getRegionList();
        regionList.get(index).setType(reqSimpleDrawing.getType());

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
        userMap.setCreateUserId(AuthUtil.getUserId());
        userMap.setCreateTime(LocalDateTime.now());
        saveOrUpdate(userMap);
        removeUserMapCatch(userMap);
    }

    @Override
    public UserMap getUserMapByName(String mapName) {
        UserMap userMap = userMapDAO.getFirstByCreateUserIdAndMapName(AuthUtil.getUserId(), mapName);
        return userMap;
    }

    @Override
    @CacheEvict(USER_MAP)
    public void deleteMapById(String id) {
        UserMap userMap = userMapDAO.selectById(id);
        removeUserMapCatch(userMap);
        userMapDAO.deleteByCreateUserIdAndUuid(AuthUtil.getUserId(), id);
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
        if (userMap.getCreateUserId() != UserEnum.ADMIN.getId() || !GameTypeEnum.ENCOUNTER.type().equals(
            userMap.getType())) {
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
    public UserMap getUserDraftEditMap(Integer userId) {
        return userMapDAO.getFirstByCreateUserIdAndUnSave(userId);
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
        List<UserMap> userMaps = userMapAttentionDao.getUserDownloadMapWithPage(apiConditionDTO);
        List<BaseMapInfoVO> resultMap = userMaps.stream().map(e->{
            BaseMapInfoVO infoVO = new BaseMapInfoVO();
            BeanUtil.copyValueByGetSet(e, infoVO);
            infoVO.setMapId(e.getUuid());
            infoVO.setCreateTime(DateUtil.formatDataTime(e.getCreateTime()));
            return infoVO;
        }).collect(Collectors.toList());

        return IPageHelper.toPage(resultMap, apiConditionDTO);
    }


    @Override
    public List<UserMap> getWorldMapList(ApiConditionDTO apiConditionDTO) {
        // TODO
        return null;
    }


    private void removeUserMapCatch(UserMap userMap){
        userMapService.removeMapCatch(userMap.getUuid());
        userMapService.removeUserMapCatch(userMap.getCreateUserId());
        if (GameTypeEnum.ENCOUNTER.type().equals(userMap.getType())) {
            userMapService.delEncounterMapsCatch();
        }
    }

    @Override
    @CacheEvict(USER_CREATE_MAP)
    public void removeUserMapCatch(Integer createUserId) {}

    @Override
    @CacheEvict(USER_MAP)
    public void removeMapCatch(String uuid){}

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
