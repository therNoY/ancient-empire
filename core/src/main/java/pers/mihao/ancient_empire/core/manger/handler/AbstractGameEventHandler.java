package pers.mihao.ancient_empire.core.manger.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
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
public abstract class AbstractGameEventHandler implements Handler {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    // 游戏上下文
    protected GameContext gameContext;
    // 命令集合
    private List<Command> commandList = null;
    // 帮助构建流
    private Stream stream;

    private int orderIndex = 0;

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


    @Override
    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
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

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite){
            setGameCommendEnum(gameCommendEnum);
            setAimSite(aimSite);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, Site aimSite){
            setGameCommendEnum(gameCommendEnum);
            setAimSite(aimSite);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite, JSONObject extData){
            setGameCommendEnum(gameCommendEnum);
            setAimSite(aimSite);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Site aimSite, Integer unitIndex){
            setGameCommendEnum(gameCommendEnum);
            setAimSite(aimSite);
            setUnitIndex(unitIndex);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, String key, Object value){
            setGameCommendEnum(gameCommendEnum);
            JSONObject extData = new JSONObject(2);
            extData.put(key, value);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream changeUnitStatus(UnitStatusInfoDTO... unitStatusInfoDTOS){
            setGameCommendEnum(GameCommendEnum.CHANGE_UNIT_STATUS);
            JSONObject extData = new JSONObject(2);
            extData.put(ExtMes.UNIT_STATUS, Arrays.asList(unitStatusInfoDTOS));
            setExtMes(extData);
            setOrder(orderIndex ++);
            addGameCommand(this);
            handlerLevelUp(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, String key, Object value){
            setGameCommendEnum(gameCommendEnum);
            JSONObject extData = new JSONObject(2);
            extData.put(key, value);
            setExtMes(extData);
            setOrder(orderIndex ++);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, JSONObject extData){
            setGameCommendEnum(gameCommendEnum);
            setExtMes(extData);
            addGameCommand(this);
            return stream;
        }

        public Stream removeUnit(ArmyUnitIndexDTO armyUnitIndexDTO){
            setGameCommendEnum(GameCommendEnum.REMOVE_UNIT);
            JSONObject addUnit = new JSONObject();
            addUnit.put(ExtMes.ARMY_UNIT_INDEX, armyUnitIndexDTO);
            setExtMes(addUnit);
            setOrder(orderIndex ++);
            addGameCommand(this);
            return stream;
        }

        public Stream addUnit(Unit unit, Integer armyIndex){
            setGameCommendEnum(GameCommendEnum.ADD_UNIT);
            JSONObject addUnit = new JSONObject();
            addUnit.put(ExtMes.UNIT, unit);
            addUnit.put(ExtMes.ARMY_INDEX, armyIndex);
            setExtMes(addUnit);
            setOrder(orderIndex ++);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, JSONObject extData){
            setGameCommendEnum(gameCommendEnum);
            setExtMes(extData);
            setOrder(orderIndex ++);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, JSONObject extData, Integer unitIndex){
            setGameCommendEnum(gameCommendEnum);
            setExtMes(extData);
            setUnitIndex(unitIndex);
            addGameCommand(this);
            return stream;
        }

        public Stream addOrderCommand(GameCommendEnum gameCommendEnum, JSONObject extData, Integer unitIndex){
            setGameCommendEnum(gameCommendEnum);
            setExtMes(extData);
            setUnitIndex(unitIndex);
            setOrder(orderIndex ++);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Unit aimUnit){
            setGameCommendEnum(gameCommendEnum);
            setAimUnit(aimUnit);
            addGameCommand(this);
            return stream;
        }

        public Stream addCommand(GameCommendEnum gameCommendEnum, Region square){
            setGameCommendEnum(gameCommendEnum);
            setAimRegion(square);
            addGameCommand(this);
            return stream;
        }


    }

    protected abstract void handlerLevelUp(GameCommand gameCommand);


}
