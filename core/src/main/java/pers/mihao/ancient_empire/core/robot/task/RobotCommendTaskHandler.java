package pers.mihao.ancient_empire.core.robot.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.task.AbstractGroupTaskHandler;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

/**
 * @Author mihao
 * @Date 2021/7/17 16:07
 */
@Component
@ConditionalOnProperty(name = "robot.handle.task", havingValue = "eventLoop")
public class RobotCommendTaskHandler extends AbstractGroupTaskHandler<RobotGroupCommend> {

    @Autowired
    GameCoreManger gameCoreManger;


    @Override
    protected long getDelay(RobotGroupCommend commend) {
        return (commend.getDelay() - System.currentTimeMillis());
    }

    @Override
    protected void doTask(String groupId, RobotGroupCommend robotGroupCommend) {
        gameCoreManger.handleCommand(robotGroupCommend.getCommandList(), robotGroupCommend.getGameContext());
    }

    @Override
    protected boolean isAsyncTask(RobotGroupCommend robotGroupCommend) {
        return true;
    }

    @Override
    protected void onNextReady(RobotGroupCommend commend) {
        commend.setDelay(System.currentTimeMillis() + commend.getWait());
    }


}
