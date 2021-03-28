package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.bo.Army;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\3\27 0027 15:45
 */
public class ArmyConfig extends Army {

    /**
     * 军队类型
     */
    private String type;

    /**
     * 玩家名字
     */
    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
