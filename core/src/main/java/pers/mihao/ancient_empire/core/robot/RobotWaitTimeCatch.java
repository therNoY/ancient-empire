package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;

/**
 * 机器人行动之后 需要等待的事件缓存
 *
 * @Author mh32736
 * @Date 2021/1/25 10:57
 */
public class RobotWaitTimeCatch {

    private static RobotWaitTimeCatch instance;

    public static RobotWaitTimeCatch getInstance() {
        if (instance == null) {
            instance = new RobotWaitTimeCatch();
        }
        return instance;
    }


    public long getLockTimeByEvent(GameEventEnum eventEnum) {
        return 1000L;
    }

}
