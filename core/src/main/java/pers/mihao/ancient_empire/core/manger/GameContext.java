package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;

import java.util.Date;
import java.util.concurrent.CyclicBarrier;

/**
 * 一局游戏的上下文，一局游戏一个context
 * @Author mh32736
 * @Date 2020/9/9 20:53
 */
public class GameContext {

    private static ThreadLocal<String> userId = new ThreadLocal<>();

    /**
     * 唯一的GameId与创建的用户存档Id一样
     */
    private String gameId;

    /**
     * 类型枚举
     */
    private GameTypeEnum gameTypeEnum;

    /**
     * 游戏的存档
     */
    private UserRecord userRecord;

    /**
     * 游戏模板
     */
    private UserTemplate userTemplate;

    /**
     * 玩家数量
     */
    private Integer playerCount;

    /**
     * 控制开始游戏
     */
    private CyclicBarrier startGame;

    /**
     * 开始游戏时间
     */
    private Date startTime;


    public CyclicBarrier getStartGame() {
        return startGame;
    }

    public void setStartGame(CyclicBarrier startGame) {
        this.startGame = startGame;
    }

    public UserRecord getUserRecord() {
        return userRecord;
    }

    public void setUserRecord(UserRecord userRecord) {
        this.userRecord = userRecord;
    }


    public static String getUserId(){
        return userId.get();
    }

    public static void setUserId(String id){
        userId.set(id);
    }

    public static void clear(){
        userId.remove();
    }

    public static void setUserId(ThreadLocal<String> userId) {
        GameContext.userId = userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public GameTypeEnum getGameTypeEnum() {
        return gameTypeEnum;
    }

    public void setGameTypeEnum(GameTypeEnum gameTypeEnum) {
        this.gameTypeEnum = gameTypeEnum;
    }

    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public void setUserTemplate(UserTemplate userTemplate) {
        this.userTemplate = userTemplate;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
