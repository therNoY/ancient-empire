package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @Author mh32736
 * @Date 2020/9/9 20:34
 */
public abstract class Robot implements Runnable{

    protected final GameContext gameContext;

    public Robot(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void run() {

    }

}
