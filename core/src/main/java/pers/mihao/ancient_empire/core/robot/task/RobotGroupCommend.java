package pers.mihao.ancient_empire.core.robot.task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import pers.mihao.ancient_empire.common.task.GroupCommend;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

/**
 * @Author mihao
 * @Date 2021/7/17 16:03
 */
public class RobotGroupCommend implements GroupCommend {

    private List<GameCommand> commandList;

    private GameContext gameContext;

    private volatile long delay;

    private int wait;

    private int maxWait;

    public RobotGroupCommend(List<GameCommand> commandList, GameContext gameContext, int wait, int maxWait) {
        this.commandList = commandList;
        this.gameContext = gameContext;
        this.wait = wait;
        this.maxWait = maxWait;
    }

    public long getDelay() {
        return delay;
    }


    public List<GameCommand> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<GameCommand> commandList) {
        this.commandList = commandList;
    }

    public GameContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    @Override
    public void notifyFirst() {
        System.out.println("\n准备立即执行该命令" + this.toString() + "\n");
        this.delay = System.currentTimeMillis() + wait;
    }

    @Override
    public String toString() {
        return "RobotGroupCommend{" +
            "commandList=" + commandList.stream().map(GameCommand::getGameCommend).collect(Collectors.toList()) +
            ", gameContext=" + gameContext.getGameId() +
            ", delay=" + delay +
            ", wait=" + wait +
            ", maxWait=" + maxWait +
            '}';
    }
}
