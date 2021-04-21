package pers.mihao.ancient_empire.core.listener;

import java.util.ArrayList;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.PathPosition;
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

    protected static final int LOAD = 10;

    protected static final int FRIEND_CAMP = 1;

    protected static final int ENEMY_CAMP = 2;

    protected static final String FRIEND_BLUE = ColorEnum.BLUE.type();

    protected static final String FRIEND_GREEN = ColorEnum.GREEN.type();

    protected static final String ENEMY_RED = ColorEnum.RED.type();

    protected static final String ENEMY_BLACK = ColorEnum.BLACK.type();

    private boolean isEffective = true;


    private final Object lockObj = new Object();

    static {
        sessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
    }

    @Override
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onClickTip() {

    }

    @Override
    public void onUnitDead(Integer armyIndex, UnitInfo unitInfo) {

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

    @Override
    public List<UnitInfo> filterUnit(List<UnitInfo> respUnitMes) {
        return respUnitMes;
    }

    private void addDialog(DialogEnum type, String message) {
        ShowDialogCommand showDialogCommand = new ShowDialogCommand();
        showDialogCommand.setDialogType(type.type());
        showDialogCommand.setMessage(message);
        sessionManger.sendMessage2Game(showDialogCommand, gameContext.getGameId());
    }

    protected void addDialogAndWait(DialogEnum type, String message) {
        addDialog(type, ChapterUtil.getMessage(message));
        // 等待用户点击
        await();
        ShowDialogCommand showDialogCommand = new ShowDialogCommand();
        showDialogCommand.setDialogType(DialogEnum.DIS_SHOW_DIALOG.type());
        sessionManger.sendMessage2Game(showDialogCommand, gameContext.getGameId());
    }

    private void await() {
        synchronized (lockObj) {
            try {
                lockObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!isEffective) {
            log.error("无效的上下文 抛出异常结束 释放线程");
            throw new AncientEmpireException("无效的上下文");
        }
    }

    protected void await(int time) {
        synchronized (lockObj) {
            try {
                lockObj.wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    protected void notifySelf() {
        notifySelf(true);
    }

    public void notifySelf(boolean isEffective) {
        synchronized (lockObj) {
            if (!isEffective) {
                this.isEffective = false;
            }
            lockObj.notifyAll();
        }
    }

    protected void gameOver() {
        addDialog(DialogEnum.GAME_OVER, ChapterUtil.getMessage("GAME_OVER"));

        onGameOver();
    }

    protected void onGameOver(){}

    protected void gameWin() {
        onGameWin();
        addDialog(DialogEnum.GAME_WIN, ChapterUtil.getMessage("GAME_WIN"));
    }

    protected void onGameWin() {}

    @Override
    public void onRoundEnd(Army army) {

    }

    protected List<Site> getEnemyCastle() {
        List<Site> list = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if ((region.getColor().equals(ENEMY_RED) || region.getColor().equals(ENEMY_BLACK))
                && region.getType().equals(RegionEnum.CASTLE.type())) {
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


    protected void addUnitAndMove(Integer armyIndex, Integer typeId, Site... sites) {
        Unit unit = addNewUnit(typeId, sites[0], armyIndex);
        moveUnit(unit.getId(), sites);
    }

    protected void moveUnit(String uuid, Site... sites) {
        ArmyUnitIndexDTO armyUnitIndexDTO = getArmyUnitIndexByUnitId(uuid);
        List<PathPosition> pathPositions = new ArrayList<>();
        for (int i = 0; i < sites.length; i++) {
            pathPositions.add(new PathPosition(sites[i]));
        }

        // 将路径长度放进去
        for (int i = 0; i < pathPositions.size() - 1; i++) {
            PathPosition p = pathPositions.get(i);
            p.setLength(getSiteLength(p, pathPositions.get(i + 1)));
        }
        moveUnit(armyUnitIndexDTO, pathPositions, new ArrayList<>());
        commandList.get(commandList.size() - 1).setDelay(300);

        // 手动更正位置
        Unit unit = getUnitByIndex(armyUnitIndexDTO);
        unit.setRow(sites[sites.length - 1].getRow());
        unit.setColumn(sites[sites.length - 1].getColumn());
    }

}
