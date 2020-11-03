package pers.mihao.ancient_empire.core.robot.factory;


import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.robot.Robot;

/**
 * 机器人工厂接口
 * @Author mh32736
 * @Date 2020/9/9 20:31
 */
public interface RobotFactory {


    /**
     * 创建机器人
     * @param gameContext
     * @return
     */
    Robot create(GameContext gameContext);

}
