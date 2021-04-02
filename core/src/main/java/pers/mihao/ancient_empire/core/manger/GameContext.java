package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import pers.mihao.ancient_empire.core.listener.GameRunListener;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;

/**
 * 一局游戏的上下文，一局游戏一个context
 * @Author mh32736
 * @Date 2020/9/9 20:53
 */
public class GameContext extends UserTemplateHelper implements GameRunListener {

    /**
     * 当前回合的玩家
     */
    private static ThreadLocal<User> user = new ThreadLocal<>();

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
     * 状态机
     */
    private StatusMachineEnum statusMachine = StatusMachineEnum.INIT;

    /**
     * 子状态
     */
    private SubStatusMachineEnum subStatusMachine = SubStatusMachineEnum.INIT;

    /**
     * 玩家数量
     */
    private Integer playerCount;

    /**
     * 展示的背景颜色
     */
    private String bgColor;

    /**
     * 控制开始游戏
     */
    private CyclicBarrier startGame;

    /**
     * 将要移动的区域
     */
    private List<Site> willMoveArea;

    /**
     * 将要移动的区域
     */
    private List<Site> willAttachArea;

    /**
     * 准备移动的点
     */
    private List<PathPosition> readyMoveLine;

    /**
     * action
     */
    private Set<String> actions;

    /**
     * 移动前的点
     */
    private Site startMoveSite;

    /**
     * 将被攻击的单位
     */
    private Unit beAttachUnit;

    /**
     * 将被攻击的单位
     */
    private Site beSummonTomb;

    /**
     * 准备移动的点
     */
    private Site readyMoveSite;

    /**
     * 开始游戏时间
     */
    private Date startTime;

    /**
     * 游戏运行监听
     */
    private List<GameRunListener> gameRunListeners;

    public GameContext() {
    }

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

    public SubStatusMachineEnum getSubStatusMachine() {
        return subStatusMachine;
    }

    public void setSubStatusMachine(SubStatusMachineEnum subStatusMachine) {
        this.subStatusMachine = subStatusMachine;
    }

    public List<Site> getWillAttachArea() {
        return willAttachArea;
    }

    public void setWillAttachArea(List<Site> willAttachArea) {
        this.willAttachArea = willAttachArea;
    }

    public static User getUser(){
        return user.get();
    }

    public static void setUser(User id){
        user.set(id);
    }

    public static void clear(){
        user.remove();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public GameTypeEnum getGameTypeEnum() {
        return gameTypeEnum;
    }

    public void setGameTypeEnum(GameTypeEnum gameTypeEnum) {
        this.gameTypeEnum = gameTypeEnum;
    }

    public Site getBeSummonTomb() {
        return beSummonTomb;
    }

    public void setBeSummonTomb(Site beSummonTomb) {
        this.beSummonTomb = beSummonTomb;
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

    public StatusMachineEnum getStatusMachine() {
        return statusMachine;
    }

    public List<Site> getWillMoveArea() {
        return willMoveArea;
    }

    public void setWillMoveArea(List<Site> willMoveArea) {
        this.willMoveArea = willMoveArea;
    }

    public void setStatusMachine(StatusMachineEnum statusMachine) {
        this.statusMachine = statusMachine;
        if (statusMachine.equals(StatusMachineEnum.INIT)) {
            setSubStatusMachine(SubStatusMachineEnum.INIT);
        }
    }

    public Site getReadyMoveSite() {
        return readyMoveSite;
    }

    public void setReadyMoveSite(Site readyMoveSite) {
        this.readyMoveSite = readyMoveSite;
    }

    public Site getStartMoveSite() {
        return startMoveSite;
    }

    public void setStartMoveSite(Site startMoveSite) {
        this.startMoveSite = startMoveSite;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public List<PathPosition> getReadyMoveLine() {
        return readyMoveLine;
    }

    public void setReadyMoveLine(List<PathPosition> readyMoveLine) {
        this.readyMoveLine = readyMoveLine;
    }

    public Unit getBeAttachUnit() {
        return beAttachUnit;
    }

    public void setBeAttachUnit(Unit beAttachUnit) {
        this.beAttachUnit = beAttachUnit;
    }

    public List<GameRunListener> getGameRunListeners() {
        return gameRunListeners;
    }

    public void setGameRunListeners(List<GameRunListener> gameRunListeners) {
        this.gameRunListeners = gameRunListeners;
    }

    @Override
    public void onGameStart() {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onGameStart();
            }
        }

    }

    @Override
    public void onUnitDead() {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitDead();
            }
        }
    }

    @Override
    public void onUnitDone() {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitDone();
            }
        }
    }

    @Override
    public boolean onGameCommandAdd(GameCommand command) {
        boolean res = true;
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                res = res && listener.onGameCommandAdd(command);
            }
        }
        return res;
    }

    @Override
    public void onUnitLevelUp(GameCommand command, Stream stream) {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitLevelUp(command, stream);
            }
        }
    }
}
