package pers.mihao.ancient_empire.robot.factory;

import pers.mihao.ancient_empire.robot.DefaultRoot;
import pers.mihao.ancient_empire.robot.Robot;

/**
 * 机器人Factory实现类
 * @Author mh32736
 * @Date 2020/9/9 20:32
 */
public class DefaultRobotFactory extends AbstractRobotFactory{

    @Override
    public Robot createRobot() {
        return new DefaultRoot();
    }
}
