package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import pers.mihao.ancient_empire.core.listener.GameRunListener;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.interactive.InteractiveLock;

/**
 * 一局游戏的上下文，一局游戏一个context
 * @Author mh32736
 * @Date 2020/9/9 20:53
 */
public class GameContext extends UserTemplateHelper {

    /**
     * 当前处理命令玩家
     */
    private static ThreadLocal<User> currHandleUser = new ThreadLocal<>();

    /**
     * 唯一的GameId与创建的用户存档Id一样
     */
    private String gameId;

    /**
     * 类型枚举
     */
    private GameTypeEnum gameType;
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
     * 开始游戏控制器
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

    /**
     * 帮助实现交互的
     */
    private InteractiveLock interactiveLock = new InteractiveLock();

    public GameContext() {
    }

    public InteractiveLock getInteractiveLock() {
        return interactiveLock;
    }

    public void setInteractiveLock(InteractiveLock interactiveLock) {
        this.interactiveLock = interactiveLock;
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
        return currHandleUser.get();
    }

    public static void setUser(User u){
        currHandleUser.set(u);
    }

    public static void clear(){
        currHandleUser.remove();
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

    public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
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


    public void onGameStart() {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onGameStart();
            }
        }

    }

    public void onClickTip() {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onClickTip();
            }
        }
    }

    public void onUnitDead(Integer armyIndex, UnitInfo unitInfo, CommonHandler handler) {
        handler.sendCommandNow();
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitDead(armyIndex, unitInfo);
            }
        }
    }

    public void onUnitDone(UnitInfo unitInfo, CommonHandler handler) {
        handler.sendCommandNow();
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitDone(unitInfo);
            }
        }
    }

    public boolean onGameCommandAdd(GameCommand command, CommonHandler handler) {
        boolean res = true;
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                res = res && listener.onGameCommandAdd(command);
            }
        }
        return res;
    }

    public void onRoundEnd(Army army, CommonHandler handler) {
        handler.sendCommandNow();
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onRoundEnd(army);
            }
        }
    }

    public void onUnitStatusChange(GameCommand command, Stream stream) {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onUnitStatusChange(command, stream);
            }
        }
    }

    public List<UnitInfo> filterUnit(List<UnitInfo> respUnitMes) {
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                respUnitMes = listener.filterUnit(respUnitMes);
            }
        }
        return respUnitMes;
    }


    public void onOccupiced(UnitInfo currUnit, Region region, CommonHandler handler) {
        handler.sendCommandNow();
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.onOccupied(currUnit, region);
            }
        }
    }

    public void beforeRoundStart(Army currArmy, CommonHandler handler) {
        handler.sendCommandNow();
        if (gameRunListeners != null) {
            for (GameRunListener listener : gameRunListeners) {
                listener.beforeRoundStart(currArmy);
            }
        }
    }
}
