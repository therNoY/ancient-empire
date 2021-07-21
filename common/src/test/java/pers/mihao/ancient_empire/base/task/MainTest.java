package pers.mihao.ancient_empire.base.task;

import java.util.Random;
import pers.mihao.ancient_empire.common.task.GroupTaskHandler;
import pers.mihao.ancient_empire.common.util.IntegerUtil;

/**
 * @Author mihao
 * @Date 2021/7/19 19:35
 */
public class MainTest {

    public static void main(String[] args) {
        GroupTaskHandler taskHandler = new TestGroupTaskHandler();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                taskHandler
                    .submitTask(IntegerUtil.getRandomIn(3, 10) + "",
                        new TestGroupTaskHandler.TestInteger(IntegerUtil.getRandomIn(1, 10)));
            }
        }



    }

}
