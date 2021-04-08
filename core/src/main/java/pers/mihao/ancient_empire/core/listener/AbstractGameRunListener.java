package pers.mihao.ancient_empire.core.listener;

import java.util.ArrayList;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameContextBaseHandler;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.command.ShowDialogCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;

/**
 * @Author mh32736
 * @Date 2021/4/1 9:16
 */
public abstract class AbstractGameRunListener extends CommonHandler implements GameRunListener {

    private static GameSessionManger sessionManger;

    protected static final int LOAD = 13;

    protected static final int FRIEND_CAMP = 1;

    protected static final String FRIEND_BLUE = ColorEnum.BLUE.type();

    protected static final String FRIEND_GREEN = ColorEnum.GREEN.type();

    protected static final String ENEMY_RED = ColorEnum.RED.type();

    protected static final String ENEMY_BLACK = ColorEnum.BLACK.type();

    private final Object lockObj = new Object();

    static {
        sessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
    }

    @Override
    public void setGameContext(GameContext gameContext) {

    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onClickTip() {

    }

    @Override
    public void onUnitDead(UnitInfo unitInfo) {

    }

    @Override
    public void onUnitDone(UnitInfo unitInfo) {

    }

    @Override
    public void onRoundStart(Army army) {

    }

    @Override
    public boolean onGameCommandAdd(GameCommand command) {
        return false;
    }

    @Override
    public void onUnitStatusChange(GameCommand command, Stream stream) {

    }

    public void addDialog(DialogEnum type, String message) {
        ShowDialogCommand showDialogCommand = new ShowDialogCommand();
        showDialogCommand.setDialogType(type);
        showDialogCommand.setMessage(message);
        sessionManger.sendMessage2Game(showDialogCommand, gameContext.getGameId());
    }

    public void addDialogAndWait(DialogEnum type, String message) {
        gameContext.getHandler().sendCommandNow();
        ChapterUtil.getMessage(message);
        addDialog(type, message);
        await();
    }

    protected void await() {
        try {
            lockObj.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void notifySelf() {
        lockObj.notifyAll();
    }

    protected void gameOver() {
    }

    protected void gameWin() {
        onGameWin();
    }

    protected void onGameWin() {
    }



    protected List<Site> getEnemyCastle() {
        List<Site> list = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if (region.getColor().equals(ENEMY_RED) || region.getColor().equals(ENEMY_BLACK)) {
                list.add(getSiteByRegionIndex(i));
            }
        }
        return list;
    }

    protected List<Site> getFriendCastle() {
        List<Site> list = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if (region.getColor().equals(FRIEND_BLUE) || region.getColor().equals(FRIEND_GREEN)) {
                list.add(getSiteByRegionIndex(i));
            }
        }
        return list;
    }


}
