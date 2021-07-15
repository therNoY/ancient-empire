package pers.mihao.ancient_empire.base.util;

import java.util.concurrent.ConcurrentSkipListSet;
import pers.mihao.ancient_empire.common.util.IntegerUtil;

/**
 * 管理生成房间Id的类 房间号由四位号码生成
 *
 * @Author mihao
 * @Date 2021/3/2 20:51
 */
public class GameRoomIdUtil {

    /**
     * id的长度
     */
    private static int idLength = 4;

    /**
     * 归还id池的数量 当达到100的时候 必定取池中的 否则概率新建
     */
    private static int maxNumPool = 100;

    private static ConcurrentSkipListSet<String> useRoomNumSet = new ConcurrentSkipListSet<>();

    private static ConcurrentSkipListSet<String> noUseRoomNumSet = new ConcurrentSkipListSet<>();

    /**
     * 生成一个房间号
     *
     * @return
     */
    public static String borrowRoomId() {
        if (noUseRoomNumSet.size() > maxNumPool) {
            return noUseRoomNumSet.pollFirst();
        } else {
            int num = IntegerUtil.getRandomIn(0, 100);
            if (num < maxNumPool - noUseRoomNumSet.size()) {
                String newId = createRoomId();
                useRoomNumSet.add(newId);
                return newId;
            }
            return noUseRoomNumSet.pollFirst();
        }

    }

    private static String createRoomId() {
        String roomId;
        StringBuilder roomIdBuilder = new StringBuilder();
        boolean isNum;
        for (int i = 0; i < idLength; i++) {
            isNum = IntegerUtil.getRandomIn(0, 9) > 3;
            if (isNum) {
                roomIdBuilder.append(IntegerUtil.getRandomIn(0, 9));
            } else {
                roomIdBuilder.append((char) IntegerUtil.getRandomIn(97, 122));
            }
        }
        roomId = roomIdBuilder.toString();
        while (useRoomNumSet.contains(roomId)) {
            roomId = createRoomId();
        }
        return roomId;
    }


    public static void returnRoomId(String roomId) {
        noUseRoomNumSet.add(roomId);
    }
}
