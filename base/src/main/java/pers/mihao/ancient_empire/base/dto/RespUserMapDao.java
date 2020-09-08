package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.entity.mongo.UserMap;

public class RespUserMapDao {

    public RespUserMapDao() {
    }

    public RespUserMapDao(List<UnitMes> unitMesList, List<RegionMes> regionMes, List<UserMap> userMaps, UserMap unSaveMap, UserSetting userSetting) {
        this.unitMesList = unitMesList;
        this.regionMes = regionMes;
        this.userMaps = userMaps;
        this.unSaveMap = unSaveMap;
        this.userSetting = userSetting;
    }

    // 1.获取可用单位信息
    private List<UnitMes> unitMesList;
    // 2.获取可用地形信息
    private List<RegionMes> regionMes;
    // 3.获取用户拥有的地图
    private List<UserMap> userMaps;
    // 3.1 用户未保存的地图
    private UserMap unSaveMap;
    // 4.获取初始化地图信息
    private UserSetting userSetting;

    public List<UnitMes> getUnitMesList() {
        return unitMesList;
    }

    public void setUnitMesList(List<UnitMes> unitMesList) {
        this.unitMesList = unitMesList;
    }

    public List<RegionMes> getRegionMes() {
        return regionMes;
    }

    public void setRegionMes(List<RegionMes> regionMes) {
        this.regionMes = regionMes;
    }

    public List<UserMap> getUserMaps() {
        return userMaps;
    }

    public void setUserMaps(List<UserMap> userMaps) {
        this.userMaps = userMaps;
    }

    public UserMap getUnSaveMap() {
        return unSaveMap;
    }

    public void setUnSaveMap(UserMap unSaveMap) {
        this.unSaveMap = unSaveMap;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }
}
