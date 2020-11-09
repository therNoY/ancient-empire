package pers.mihao.ancient_empire.core.robot.factory;


import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;
import pers.mihao.ancient_empire.core.robot.DefaultRobot;

/**
 * 机器人Factory实现类
 * @Author mh32736
 * @Date 2020/9/9 20:32
 */
public class DefaultRobotFactory extends AbstractRobotFactory{

    private static DefaultRobotFactory factory = new DefaultRobotFactory();

    private DefaultRobotFactory() {
    }

    @Override
    public AbstractRobot create(GameContext gameContext) {
        AbstractRobot abstractRobot = new DefaultRobot(gameContext);
        return abstractRobot;
    }

    public static AbstractRobot createRobot(GameContext gameContext) {
        return factory.create(gameContext);
    }
}
