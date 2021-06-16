package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * 初始化进队
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 22:10
 */
public class InitMapDTO extends ApiRequestDTO {
    /**
     * 最大人口
     */
    private Integer maxPop;
    /**
     * 初始金额
     */
    private Integer money;
    /**
     * 使用地图
     */
    private String mapId;
    /**
     * 军地配置
     */
    private List<ArmyConfig> armyList;

    /**
     * 游戏的类型 遭遇战单机 多人游戏 故事模式
     */
    private String gameType;

    /**
     * 玩家映射
     */
    private Map<String, String> player;

    public Integer getMaxPop() {
        return maxPop;
    }

    public void setMaxPop(Integer maxPop) {
        this.maxPop = maxPop;
    }

    public String getMapId() {
        return mapId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public List<ArmyConfig> getArmyList() {
        return armyList;
    }

    public void setArmyList(List<ArmyConfig> armyList) {
        this.armyList = armyList;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public Map<String, String> getPlayer() {
        return player;
    }

    public void setPlayer(Map<String, String> player) {
        this.player = player;
    }
}
