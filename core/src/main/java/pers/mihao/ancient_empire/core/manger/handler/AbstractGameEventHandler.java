package pers.mihao.ancient_empire.core.manger.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 游戏事件处理类 对于每个处理都是一个线程池中的线程执行
 * 执行命令过程中产生command集合 返回给前端
 * @Author mh32736
 * @Date 2020/9/16 18:57
 */
public abstract class AbstractGameEventHandler implements Handler{
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String CURR_ARMY_IS_NULL = "currArmyIsNull";
    // 游戏上下文
    protected GameContext gameContext;
    private Army currArmy;
    // 命令集合
    private List<Command> commandList = null;
    // 帮助构建流
    private Stream stream;

    @Override
    public List<Command> handler(Event event) {
        log.info("开始处理事件：{}", event);
        handlerGameEvent((GameEvent) event);
        log.info("处理事件结束 返回的命令集合：{}", commandList);
        return commandList;
    }

    /**
     * 获得一个流
     * @return
     */
    protected Stream commandStream(){
        if (stream == null) {
            stream = new Stream();
        }
        return stream;
    }

    /**
     * 返回的命令
     * @param command
     */
    protected AbstractGameEventHandler addGameCommand(Command command) {
        if (commandList == null) {
            commandList = new ArrayList<>();
        }
        commandList.add(command);
        return this;
    }

    /**
     * 子类据需要实现的处理事件
     * @param gameEvent
     */
    public abstract void handlerGameEvent(GameEvent gameEvent);

    /**
     * 基础方法
     * @param army
     * @param site
     * @return
     */
    protected Unit getUnitBySite(Site site) {
        for (Unit unit : currArmy().getUnits()) {
            if (AppUtil.siteEquals(unit, site)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 基础方法 根据位置获取地形
     * @param army
     * @param site
     * @return
     */
    protected BaseSquare getRegionBySite(Site site) {
        return gameMap().getRegions().get((site.getRow() - 1) * gameMap().getColumn() - 1 + site.getColumn());
    }

    @Override
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 当前记录
     * @return 记录视图
     */
    protected UserRecord userRecord(){
        return gameContext.getUserRecord();
    }

    /**
     * 当前记录
     * @return 记录视图
     */
    protected GameMap gameMap(){
        return userRecord().getGameMap();
    }

    /**
     * game视图
     * @return 当前单位
     */
    protected Army currArmy(){
        if (currArmy == null) {
            for (Army army : userRecord().getArmyList()) {
                if (army.getCamp().equals(userRecord().getCurrCamp())) {
                    this.currArmy = army;
                    break;
                }
            }
        }
        Assert.notNull(currArmy, CURR_ARMY_IS_NULL);
        return currArmy;
    }

    /**
     * 流式
     */
    class Stream{
        public FlowGameCommand toAllCommand(){
            FlowGameCommand command = new FlowGameCommand();
            command.setSendTypeEnum(SendTypeEnum.SEND_TO_SYSTEM);
            return command;
        }

        public FlowGameCommand toUserCommand(){
            FlowGameCommand command = new FlowGameCommand();
            command.setSendTypeEnum(SendTypeEnum.SEND_TO_USER);
            return command;
        }

        public FlowGameCommand toGameCommand(){
            FlowGameCommand command = new FlowGameCommand();
            command.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
            return command;
        }

    }

    /**
     * 帮助构建流式
     */
    class FlowGameCommand extends GameCommand{

        public Stream addCommand(GameCommendEnum gameCommendEnum){
            setGameCommendEnum(gameCommendEnum);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site pointSite){
            setGameCommendEnum(gameCommendEnum);
            setAimSite(pointSite);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Unit aimUnit){
            setGameCommendEnum(gameCommendEnum);
            setAimUnit(aimUnit);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, BaseSquare square){
            setGameCommendEnum(gameCommendEnum);
            setAimRegion(square);
            addGameCommand(this);
            return stream;
        }


    }
}
