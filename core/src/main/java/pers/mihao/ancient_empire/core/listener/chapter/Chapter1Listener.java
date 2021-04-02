package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;

/**
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter1Listener extends AbstractGameRunListener {

    @Override
    public void onGameStart() {

    }

    @Override
    public void onUnitDead() {

    }

    @Override
    public void onUnitDone() {

    }

    @Override
    public boolean onGameCommandAdd(GameCommand command) {
        return false;
    }

    @Override
    public void onUnitLevelUp(GameCommand command, Stream stream) {

    }
}
