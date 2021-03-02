package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

public class ReqInitMapDto extends ApiRequestDTO {
    @DecimalMin(value = "5", message = "最小人口大于5")
    private Integer maxPop;
    @DecimalMin(value = "500", message = "初始金额要大于500")
    private Integer money;
    @NotEmpty(message = "地图Id 不能为空")
    private String mapId;
    @NotEmpty(message = "队伍不能为空")
    private List<ReqArmy> armyList;

    /* 游戏的类型 遭遇战单机 多人游戏 故事模式 */
    private String gameType;

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

    public List<ReqArmy> getArmyList() {
        return armyList;
    }

    public void setArmyList(List<ReqArmy> armyList) {
        this.armyList = armyList;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }


    public static class ReqArmy extends Army{
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
