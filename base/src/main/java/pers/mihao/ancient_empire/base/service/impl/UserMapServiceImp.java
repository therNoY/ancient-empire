package pers.mihao.ancient_empire.base.service.impl;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.enums.UserEnum;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.dto.ReqSimpleDrawing;
import pers.mihao.ancient_empire.base.dto.RespSimpleDrawing;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.enums.CollectionEnum;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.mongo.dao.UserMapRepository;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.vo.BaseMapInfoVO;
import pers.mihao.ancient_empire.base.vo.UserMapVo;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.jdbc.mongo.MongoUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserMapServiceImp implements UserMapService {

    private static final String SEA = "sea";
    private static final String LAND = "land";
    private static final String BRIDGE = "bridge";
    private static final String BANK = "bank";
    private static final String BANK_ = "bank_";

    @Value("${army.max}")
    Integer maxArmy;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserMapRepository userMapRepository;

    /**
     * 从mongo 中获取用户地图
     * @return
     */
    @Override
    public List<UserMap> getUserMap() {
        Integer id = AuthUtil.getAuthId();
        return userMapRepository.findByCreateUserId(id);
    }


    /**
     * 保存临时地图
     * @param userMap
     */
    @Override
    @Transactional
    public void saveTempMap(UserMap userMap) {
        // 1.删除临时的地图
        userMapRepository.deleteByUnSaveAndCreateUserId(true, AuthUtil.getAuthId());
        // 2.初始化临时数据
        userMap.setCreateUserId(AuthUtil.getAuthId());
        userMap.setUuid(StringUtil.getUUID());
        userMap.setCreateTime(DateUtil.getNow());
        userMap.setMapName("temp");
        userMap.setUnSave(true);
        // 3.保存
        userMapRepository.save(userMap);
    }

    /**
     * 获取优化后的地形
     * 如果要画的是海 但是在一块陆地上画就需要 返回一个池塘
     * 如果要画的是陆地 但是要在海里画 就返回一个小岛
     * <p>
     * 思路 九宫格模型
     * @param reqSimpleDrawing
     * @return
     */
    @Override
    public List<RespSimpleDrawing> getSimpleDrawing(ReqSimpleDrawing reqSimpleDrawing) {
        List<RespSimpleDrawing> simpleDrawings = new ArrayList<>();
        Integer index = reqSimpleDrawing.getIndex();
        String type = reqSimpleDrawing.getType();
        Integer column = reqSimpleDrawing.getColumn();
        List<BaseSquare> regionList = reqSimpleDrawing.getRegionList();
        // 如果是以sea 或者海开头的话
        if (type.startsWith(SEA) || type.startsWith(BANK)) {
            // 画海 获取周围陆地的信息
            Map<Integer, Integer> map = getAroundLandMap(index, regionList, column);
            if (map.size() == 0) {
                return null;
            }
            StringBuffer sb = new StringBuffer(BANK_);
            map.keySet().stream().sorted()
                    .collect(Collectors.toList())
                    .forEach(integer ->  sb.append(integer));
            RespSimpleDrawing simpleDrawing = new RespSimpleDrawing(index, sb.toString());
            simpleDrawings.add(simpleDrawing);
            return simpleDrawings;
        } else if (type.startsWith(BRIDGE)) {

        } else {
            // 剩下就是陆地
            Map<Integer, Integer> map = getAroundSeaMap(index, regionList, column);
            if (map.size() == 0) {
                return null;
            }
            for (Map.Entry entry : map.entrySet()) {
                simpleDrawings.add(new RespSimpleDrawing((Integer) entry.getValue(), SEA));
            }
            return simpleDrawings;
        }
        return null;
    }

    @Override
    public String getType(String type) {
        if (isLand(type)) {
            return LAND;
        }else {
            return SEA;
        }
    }

    /**
     * 保存地图
     * @param userMap
     */
    @Override
    public void saveMap(UserMap userMap) {
        userMap.setUuid(StringUtil.getUUID());
        userMap.setUnSave(false);
        userMap.setCreateUserId(AuthUtil.getAuthId());
        userMap.setCreateTime(DateUtil.getNow());
        userMapRepository.save(userMap);
    }

    @Override
    public UserMap getUserMapByName(String mapName) {
        UserMap userMap = userMapRepository.getFirstByCreateUserIdAndMapName(AuthUtil.getAuthId(), mapName);
        return userMap;
    }

    /**
     * 根据Id 删除地图
     */
    @Override
    public void deleteMapById(String id) {
        // 这里删除的时候要加上userId 防止其他用户删除uuid
        userMapRepository.deleteByCreateUserIdAndUuid(AuthUtil.getAuthId(), id);
    }

    /**
     * 超管 设置地图类型
     * @param userMap
     */
    @Override
    public void updateMap(UserMap userMap) {
        Update update = new Update();
        update.set("type", userMap.getType());
        mongoTemplate.updateFirst(new Query(Criteria.where("uuid").is(userMap.getUuid())), update, CollectionEnum.USER_MAP.type());
    }

    /**
     * 获取所有遭遇战 地图
     * @return
     */
    @Override
    @Cacheable(CatchKey.ENCOUNTER_MAP)
    public List<BaseMapInfoVO> getEncounterMaps() {
        Criteria criteria = new Criteria()
                .and("createUserId").is(UserEnum.ADMIN.getId())
                .and("type").is(GameTypeEnum.ENCOUNTER.type());
        List<BaseMapInfoVO> encounterMaps = MongoUtil.findByCriteria(criteria, BaseMapInfoVO.class);
        return encounterMaps;
    }

    /**
     * 获取遭遇战地图 的所有初始化军队
     * @return
     */
    @Override
    public List<String> getInitArmy(String uuid) {
        UserMap userMap = getEncounterMapById(uuid);
        if (userMap == null) {
            return null;
        }
        List<String> colors = new ArrayList<>();
        List<BaseSquare> regions = userMap.getRegions();
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

    /**
     * 获取遭遇战地图
     * @param uuid
     * @return
     */
    @Override
    public UserMap getEncounterMapById(String uuid) {
        UserMap userMap = userMapRepository.getFirstByUuid(uuid);
        if (userMap.getCreateUserId() != UserEnum.ADMIN.getId() || !GameTypeEnum.ENCOUNTER.type().equals(userMap.getType())) {
            // 不是遭遇地图
            return null;
        }
        return userMap;
    }

    /**
     * 获取用户凡人地图
     * @param uuid
     * @return
     */
    @Cacheable(CatchKey.USER_MAP)
    @Override
    public UserMapVo getUserMapById(String uuid) {
        UserMapVo userMapVo = new UserMapVo();
        UserMap userMap = userMapRepository.getFirstByUuid(uuid);
        BeanUtils.copyProperties(userMap, userMapVo);
        userMapVo.setCastleTitles(findCastleTitle(userMap.getRegions(), userMap.getRow(), userMap.getColumn()));
        return userMapVo;
    }

    /**
     * 找到index 周围的海洋
     *
     * @param index
     * @param regionList
     * @param column
     * @return
     */
    private Map<Integer, Integer> getAroundSeaMap(int index, List<BaseSquare> regionList, int column) {
        Map<Integer, Integer> isSeaMap = new HashMap<>();

        // 1. 判断上面的地形
        int up = index - column;
        // 判断 1 号
        if (up > 0 ) {
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
     * @param index
     * @param regionList
     * @param column
     * @return
     */
    private Map<Integer, Integer> getAroundLandMap(int index, List<BaseSquare> regionList, int column) {
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

    private boolean isSea(String type) {
        return type.startsWith(SEA) || type.startsWith(BANK);
    }

    private boolean isLand(String type) {
        return !type.startsWith(SEA) && !type.startsWith(BRIDGE) && !type.startsWith(BANK);
    }

}
