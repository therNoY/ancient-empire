package pers.mihao.ancient_empire.core.manger;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

/**
 * 一局游戏的上下文，一局游戏一个context
 * @Author mh32736
 * @Date 2020/9/9 20:53
 */
public class GameContext extends UserTemplateHelper {

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
     * 状态机
     */
    private StatusMachineEnum statusMachine = StatusMachineEnum.NO_CHOOSE;

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


    public List<Site> getWillAttachArea() {
        return willAttachArea;
    }

    public void setWillAttachArea(List<Site> willAttachArea) {
        this.willAttachArea = willAttachArea;
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
}
