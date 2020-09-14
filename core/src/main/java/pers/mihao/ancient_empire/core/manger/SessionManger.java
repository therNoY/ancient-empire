package pers.mihao.ancient_empire.core.manger;

import javafx.util.Pair;
import pers.mihao.ancient_empire.common.annotation.Manger;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\14 0014 23:24
 */
@Manger
public class SessionManger {

    // 保存用户的session
    ConcurrentHashMap<String, List<Pair<String, Session>>> sessionMap = new ConcurrentHashMap();


    /**
     * 发送消息
     * @param session
     * @param message
     * @throws IOException
     */
    public void sendMessage(Session session, String message) {
        if (session != null) {
            synchronized (session) {
                System.out.println("发送数据：" + message);
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(String userName, String message) {

    }

    /**
     * Session 加入队列
     * @param session
     * @param recordId
     * @param userId
     */
    public void addNewSession(Session session, String recordId, String userId) {
    }

    /**
     * 移除队列
     * @param recordId
     * @param session
     */
    public void removeSession(String recordId, Session session) {
    }
}
