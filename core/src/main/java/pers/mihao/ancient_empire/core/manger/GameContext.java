package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.base.entity.UserRecord;

/**
 * 一局游戏的上下文，一局游戏一个context
 * @Author mh32736
 * @Date 2020/9/9 20:53
 */
public class GameContext {

    private UserRecord userRecord;

    public UserRecord getUserRecord() {
        return userRecord;
    }

    public void setUserRecord(UserRecord userRecord) {
        this.userRecord = userRecord;
    }
}
