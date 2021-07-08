package pers.mihao.ancient_empire.core.manger.net;

import javax.websocket.Session;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserSetting;
import pers.mihao.ancient_empire.core.eums.NetConnectTypeEnum;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.manger.net.session.AbstractSession;

/**
 * 管理Session 处理 事件
 * @Author mihao
 * @Date 2021/6/16 9:39
 */
public interface SessionManger<T extends AbstractSession, E extends Event> {

    /**
     * 添加一个新的session
     *
     * @param netSession
     * @param typeId
     * @param user
     * @return
     */
    T addNewSession(Session netSession, String typeId, User user, NetConnectTypeEnum connectType);

    /**
     * 移除一个session
     *
     * @param typeId
     * @param netSession
     */
    void onSessionClose(String typeId, Session netSession);

    /**
     * 给系统发送消息
     *
     * @param command
     * @param typeId
     */
    void sendMessage2AllGroup(Command command, String typeId);

    /**
     * 给群组发送消息
     *
     * @param command
     * @param typeId
     */
    void sendMessageToGroup(Command command, String typeId);


    /**
     * 给用户发送消息
     *
     * @param command
     * @param gameId
     */
    void sendMessageToUser(Command command, String gameId);


    /**
     * 处理事件
     * @param message
     * @param typeId
     * @param user
     * @param userSetting
     */
    void handleMessage(String message, String typeId, User user, UserSetting userSetting);

    /**
     * 获取加入成功的命令
     * @param typeId
     * @return
     */
    Command getJoinSuccessCommon(String typeId);
}
