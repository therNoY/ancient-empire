package pers.mihao.ancient_empire.core.listener;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.command.ShowDialogCommand;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.robot.ActionIntention;

/**
 * @Author mh32736
 * @Date 2021/4/1 9:16
 */
public abstract class AbstractGameRunListener extends CommonHandler implements GameRunListener {

    private static GameSessionManger sessionManger;

    /**
     * 标记当前上线问是否还有效
     */
    private boolean isEffective = true;

    /**
     * 对话框点击提示之后的线程锁
     */
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

    @Override
    public void onOccupied(UnitInfo currUnit, Region region) {
    }

    @Override
    public void beforeRoundStart(Army currArmy) {
    }

    @Override
    public ActionIntention chooseAction(UnitInfo unitInfo, List<ActionIntention> actionList) {
        return null;
    }

    /**
     * 添加一个类型的提示框
     * @param type 类型
     * @param message 消息
     */
    protected void addDialogAndWait(DialogEnum type, String message) {
        addDialog(type, ChapterDialogHelper.getMessage(message));
        // 等待用户点击
        await();
        ShowDialogCommand showDialogCommand = new ShowDialogCommand();
        showDialogCommand.setDialogType(DialogEnum.DIS_SHOW_DIALOG.type());
        sessionManger.sendMessage2Game(showDialogCommand, gameContext.getGameId());
    }

    private void addDialog(DialogEnum type, String message) {
        ShowDialogCommand showDialogCommand = new ShowDialogCommand();
        showDialogCommand.setDialogType(type.type());
        showDialogCommand.setMessage(message);
        sessionManger.sendMessage2Game(showDialogCommand, gameContext.getGameId());
    }


    /**
     * 等待前端的交互, 并设置最大等待时间
     * @param maxTime
     */
    protected void waitExecutionOk(int maxTime) {
        log.info("等待前端交互完毕, 之后延迟");
        gameContext.getInteractiveLock().untilExecutionOk(maxTime, gameContext.getWaitTime());
    }

    /**
     * 超时退出
     * @param time
     */
    protected void awaitTime(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("" , e);
        }
    }

    /**
     * 等待用户点击交互
     */
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
            throw new AeException("无效的上下文");
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
        addDialog(DialogEnum.GAME_OVER, ChapterDialogHelper.getMessage("GAME_OVER"));

        onGameOver();
    }

    protected void onGameOver() {
    }

    protected void gameWin() {
        onGameWin();
        addDialog(DialogEnum.GAME_WIN, ChapterDialogHelper.getMessage("GAME_WIN"));
    }

    protected void onGameWin() {
    }

    @Override
    public void onRoundEnd(Army army) {
    }


    /**
     * 添加单位并移动
     *
     * @param armyIndex
     * @param typeId
     * @param sites
     * @return
     */
    protected Unit addUnitAndMove(Integer armyIndex, Integer typeId, Site... sites) {
        Unit unit = addNewUnit(typeId, sites[0], armyIndex);
        moveUnit(unit.getId(), sites);
        return unit;
    }

    /**
     * 根据初始点移动单位
     *
     * @param sites
     * @return
     */
    protected Unit moveUnit(Site... sites) {
        Pair<Integer, Unit> unit = getUnitFromMapBySite(sites[0]);
        assert unit != null;
        moveUnit(unit.getValue().getId(), sites);
        return unit.getValue();
    }

    /**
     * 移动单位
     *
     * @param uuid  单位id
     * @param sites 移动路径
     */
    protected void moveUnit(String uuid, Site... sites) {
        ArmyUnitIndexDTO armyUnitIndexDTO = getArmyUnitIndexByUnitId(uuid);
        List<PathPosition> pathPositions = new ArrayList<>();
        for (Site site : sites) {
            pathPositions.add(new PathPosition(site));
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
