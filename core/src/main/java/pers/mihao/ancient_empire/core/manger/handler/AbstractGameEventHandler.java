package pers.mihao.ancient_empire.core.manger.handler;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.common.annotation.ExecuteTime;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameContextBaseHandler;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 游戏事件处理类 对于每个处理都是一个线程池中的线程执行 执行命令过程中产生command集合 返回给前端
 *
 * @Author mihao
 * @Date 2020/9/16 18:57
 */
public abstract class AbstractGameEventHandler extends GameContextBaseHandler implements GameHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 一次时间产生的命令合集 可以有多个无序命令 有一个或零个有序命令
     */
    protected List<GameCommand> commandList = null;
    /**
     * 帮助构建命令流
     */
    private Stream stream;

    int orderIndex = 0;

    @Override
    @ExecuteTime
    public final List<GameCommand> handler(Event event) {
        log.info("开始处理事件：{}", event);
        handlerGameEvent((GameEvent) event);
        log.info("处理事件结束 返回的命令集合：{}", commandList);
        return commandList;
    }

    /**
     * 获得一个同步发送消息流
     *
     * @return
     */
    protected Stream commandStream() {
        if (stream == null) {
            stream = new Stream();
        }
        return stream;
    }

    /**
     * 获得一个异步发送流
     *
     * @return
     */
    protected Stream commandAsyncStream() {
        if (stream == null) {
            stream = new Stream();
            stream.isAsync = true;
        }
        return stream;
    }

    /**
     * 获得一个异步发送流
     *
     * @return
     */
    protected Stream commandOrderStream() {
        if (stream == null) {
            stream = new Stream();
            stream.isAsync = true;
        }
        return stream;
    }

    /**
     * 获得一个异步发送流
     *
     * @return
     */
    protected Stream commandAsyncOrderStream() {
        if (stream == null) {
            stream = new Stream();
            stream.isAsync = true;
            stream.order = false;
        }
        return stream;
    }

    /**
     * 返回的命令
     *
     * @param command
     */
    protected AbstractGameEventHandler addGameCommand(GameCommand command) {
        if (commandList == null) {
            commandList = new ArrayList<>();
        }
        gameContext.onGameCommandAdd(command, (CommonHandler) this);
        commandList.add(command);
        return this;
    }

    /**
     * 子类据需要实现的处理事件
     *
     * @param gameEvent
     */
    public abstract void handlerGameEvent(GameEvent gameEvent);

    @Override
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public GameContext getGameContext() {
        return gameContext;
    }


    /**
     * 流式命令
     */
    public class Stream {

        /**
         * 是否同步
         *
         * @return
         */
        boolean isAsync = false;

        /**
         * 是有顺序的
         */
        boolean order = false;

        public FlowGameCommand toUserCommand() {
            FlowGameCommand command = new FlowGameCommand();
            command.setSendType(SendTypeEnum.SEND_TO_USER);
            if (order) {
                command.setOrder(orderIndex++);
            }
            command.setAsync(isAsync);
            return command;
        }

        public FlowGameCommand toGameCommand() {
            FlowGameCommand command = new FlowGameCommand();
            command.setSendType(SendTypeEnum.SEND_TO_GROUP);
            if (order) {
                command.setOrder(orderIndex++);
            }
            command.setAsync(isAsync);
            return command;
        }
    }

    /**
     * 帮助构建流式
     */
    public class FlowGameCommand extends GameCommand {

        public Stream addCommand(GameCommendEnum gameCommendEnum) {
            setGameCommend(gameCommendEnum);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite) {
            setGameCommend(gameCommendEnum);
            setAimSite(aimSite);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum) {
            setGameCommend(gameCommendEnum);
            addGameCommand(this);
            setOrder(orderIndex++);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, Site aimSite) {
            setGameCommend(gameCommendEnum);
            setAimSite(aimSite);
            addGameCommand(this);
            setOrder(orderIndex++);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite, JSONObject extData) {
            setGameCommend(gameCommendEnum);
            setAimSite(aimSite);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite, Integer unitIndex) {
            setGameCommend(gameCommendEnum);
            setAimSite(aimSite);
            setUnitIndex(unitIndex);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, String key, Object value) {
            setGameCommend(gameCommendEnum);
            JSONObject extData = new JSONObject(2);
            extData.put(key, value);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, String key, Object value) {
            setGameCommend(gameCommendEnum);
            JSONObject extData = new JSONObject(2);
            extData.put(key, value);
            setExtMes(extData);
            setOrder(orderIndex++);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, JSONObject extData) {
            setGameCommend(gameCommendEnum);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, JSONObject extData) {
            setGameCommend(gameCommendEnum);
            setExtMes(extData);
            setOrder(orderIndex++);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, JSONObject extData, int order) {
            setGameCommend(gameCommendEnum);
            setExtMes(extData);
            setOrder(order);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, JSONObject extData, Integer unitIndex) {
            setGameCommend(gameCommendEnum);
            setExtMes(extData);
            setUnitIndex(unitIndex);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, JSONObject extData, Integer unitIndex) {
            setGameCommend(gameCommendEnum);
            setExtMes(extData);
            setUnitIndex(unitIndex);
            setOrder(orderIndex++);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Unit aimUnit) {
            setGameCommend(gameCommendEnum);
            setAimUnit(aimUnit);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Region square) {
            setGameCommend(gameCommendEnum);
            setAimRegion(square);
            addGameCommand(this);
            return stream;
        }


    }

    public void sendCommandNow() {
        gameCoreManger.handleCommand(commandList, gameContext);
        commandList = new ArrayList<>();
        orderIndex = 0;
    }




}
