package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;

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

    private GameTypeEnum gameTypeEnum;

    /**
     * 游戏的存档
     */
    private UserRecord userRecord;

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
}
