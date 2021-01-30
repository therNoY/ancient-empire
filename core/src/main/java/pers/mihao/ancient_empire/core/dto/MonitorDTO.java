package pers.mihao.ancient_empire.core.dto;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\30 0030 15:39
 */
public class MonitorDTO {

    private String gameId;

    private String currArmy;

    private String currUnit;

    private String currArmyIndex;

    private String currUnitType;

    private String noMap;

    private String all;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getCurrArmy() {
        return currArmy;
    }

    public void setCurrArmy(String currArmy) {
        this.currArmy = currArmy;
    }

    public String getCurrUnit() {
        return currUnit;
    }

    public void setCurrUnit(String currUnit) {
        this.currUnit = currUnit;
    }

    public String getCurrArmyIndex() {
        return currArmyIndex;
    }

    public void setCurrArmyIndex(String currArmyIndex) {
        this.currArmyIndex = currArmyIndex;
    }

    public String getCurrUnitType() {
        return currUnitType;
    }

    public void setCurrUnitType(String currUnitType) {
        this.currUnitType = currUnitType;
    }

    public String getNoMap() {
        return noMap;
    }

    public void setNoMap(String noMap) {
        this.noMap = noMap;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }
}
