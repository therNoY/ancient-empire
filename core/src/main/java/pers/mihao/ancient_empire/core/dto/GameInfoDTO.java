package pers.mihao.ancient_empire.core.dto;

/**
 * @Author mihao
 * @Date 2020/11/3 20:33
 */
public class GameInfoDTO {

    /**
     * 当前回合数
     */
    private Integer currentRound;
    /**
     * 当前行动的军队color
     */
    private String currColor;
    /**
     * 当前军队的index
     */
    private Integer currArmyIndex;
    /**
     * 当前阵容
     */
    private Integer currCamp;
    /**
     * 当前游戏的玩家ID
     */
    private String currPlayer;

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public String getCurrColor() {
        return currColor;
    }

    public void setCurrColor(String currColor) {
        this.currColor = currColor;
    }

    public Integer getCurrArmyIndex() {
        return currArmyIndex;
    }

    public void setCurrArmyIndex(Integer currArmyIndex) {
        this.currArmyIndex = currArmyIndex;
    }

    public Integer getCurrCamp() {
        return currCamp;
    }

    public void setCurrCamp(Integer currCamp) {
        this.currCamp = currCamp;
    }

    public String getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(String currPlayer) {
        this.currPlayer = currPlayer;
    }

    @Override
    public String toString() {
        return "GameInfoDTO{" +
            "currentRound=" + currentRound +
            ", currColor='" + currColor + '\'' +
            ", currArmyIndex=" + currArmyIndex +
            ", currCamp=" + currCamp +
            ", currPlayer='" + currPlayer + '\'' +
            '}';
    }
}
