package pers.mihao.ancient_empire.common.constant;

/**
 * 缓存的常量
 *
 * @author hspcadmin
 */
public class CacheKey {

    public static final String ENABLE_UNIT = "enableUnit";

    public static final String ENABLE_REGION = "enableRegion";
    // 绑定name
    public static final String USER_INFO = "userInfo";
    // 绑定ID
    public static final String USER = "user";
    // 用户设定
    public static final String USER_SETTING = "userSetting";

    // 遭遇战地图
    public static final String USER_MAP = "userMap";

    // 遭遇战地图
    public static final String ENCOUNTER_MAP = "encounterMap";

    // 用户记录
    public static final String USER_RECORD = "userRecord";

    // 地形信息
    public static final String REGION_MES = "regionMes";

    // 地形信息
    public static final String UNIT_MES = "unitMes";

    // 单位能力
    public static final String UNIT_ABILITY = "unitAbility";

    // 单位等级能力
    public static final String UNIT_LEVEL_MES = "unitLevelMes";

    /**
     * 单位的最新版本
     */
    public static final String UNIT_MAX_VERSION = "unitMaxVersion";

    public static final String MAP_MAX_VERSION = "mapMaxVersion";
    /**
     * 模板的最新版本
     */
    public static final String TEMPLATE_MAX_VERSION = "templateMaxVersion";

    /**
     * 单位详细信息 包含单等级信息， 基础信息， 能力信息
     */
    public static final String UNIT_INFO = "unitInfo";

    // 模板可以购买的单位
    public static final String TEMPLATE_CAN_BUY_UNITS = "templateCanBuyUnits";

    public static final String BUFF = "buff";


    /* 用户模板 */
    public static final String USER_TEMP = "userTemp";

    public static String getKey(String key) {
        return key + "::";
    }

}
