package pers.mihao.ancient_empire.core.manger.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserSetting;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.StatusCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.manger.net.session.AbstractSession;

/**
 * 提供基础功能的session管理
 *
 * @Author mihao
 * @Date 2021/6/16 9:40
 */
public abstract class AbstractSessionManger<T extends AbstractSession, E extends Event> implements SessionManger<T, E> {

    protected final ConcurrentHashMap<String, List<T>> sessionMap = new ConcurrentHashMap<>();

    Logger log = LoggerFactory.getLogger(GameSessionManger.class);

    @Autowired
    public UserService userService;

    @Override
    public final T addNewSession(Session netSession, String typeId, User user) {
        T session = createSession(netSession, typeId, user);
        if (session == null) {
            return null;
        }
        session.setSessionId(netSession.getId());

        List<T> list;
        synchronized (sessionMap) {
            list = sessionMap.computeIfAbsent(typeId, k -> new ArrayList<>());
        }

        synchronized (list) {
            list.add(session);
            addNewSession(session, list);
        }
        return session;
    }


    @Override
    public final void onSessionClose(String typeId, Session session) {
        List<T> sessionList = sessionMap.get(typeId);
        if (sessionList == null) {
            return;
        }
        T tSession;
        synchronized (sessionList) {
            for (int i = 0; i < sessionList.size(); i++) {
                tSession = sessionList.get(i);
                if (session.getId().equals(tSession.getSessionId())) {
                    sessionList.remove(i);
                    tSession.setLevelDate(new Date());
                    removeSessionFromGroup(tSession, sessionList);
                    break;
                }
            }
        }
        if (sessionList.isEmpty()) {
            log.info("session组：{}, 没有玩家 销毁", typeId);
            sessionMap.remove(typeId);
            removeGroup(typeId);
        }
    }

    /**
     * 发送消息
     *
     * @param command
     * @param gameId
     * @throws IOException
     */
    @Override
    public void sendMessage2AllGroup(Command command, String gameId) {
        for (Map.Entry<String, List<T>> entry : sessionMap.entrySet()) {
            List<T> sessions = entry.getValue();
            T session = null;
            log.info("发送数据{} 给群组：{}", command, gameId);
            try {
                for (int i = 0; i < sessions.size(); i++) {
                    session = sessions.get(i);
                    session.sendCommand(command);
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", session.getUser(), e);
            }
        }
    }

    @Override
    public void sendMessageToGroup(Command command, String typeId) {
        List<T> sessionList = sessionMap.get(typeId);
        if (sessionList != null) {
            T session = null;
            log.info("发送数据{} 给群组：{}", command, typeId);
            try {
                for (int i = 0; i < sessionList.size(); i++) {
                    session = sessionList.get(i);
                    Command newCommand = command.beforeSend(session.getUser());
                    session.sendCommand(newCommand);
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", session.getUser(), e);
            }
        }
    }

    @Override
    public void sendMessageToUser(Command command, String typeId) {
        List<T> sessionList = sessionMap.get(typeId);
        if (sessionList != null) {
            T session = null;
            log.info("发送数据{} 给用户：{}", command, typeId);
            try {
                for (int i = 0; i < sessionList.size(); i++) {
                    session = sessionList.get(i);
                    Command newCommand = command.beforeSend(session.getUser());
                    if (session.getUser().equals(GameContext.getUser())) {
                        session.sendCommand(newCommand);
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", session.getUser(), e);
            }
        }
    }

    @Override
    public final Command getJoinSuccessCommon(String typeId) {
        StatusCommand command = new StatusCommand();
        command.setOpenId(typeId);
        Object message = getJoinSuccessMessage(typeId);
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        command.setMessage(JSON.toJSONString(message, config));
        return command;
    }

    @Override
    public final void handleMessage(String message, String typeId, User user, UserSetting userSetting) {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 返回表示此类型实际类型参数的 Type 对象的数组()，想要获取第二个泛型的Class，所以索引写1
        Class<E> eClass = (Class) type.getActualTypeArguments()[1];
        Event event = JSON.parseObject(message, eClass);
        event.setId(typeId);
        event.setUser(user);
        event.setLanguage(
            userSetting == null ? LanguageEnum.ZH : LanguageEnum.valueOf(userSetting.getLanguage().toUpperCase()));
        event.setCreateTime(new Date());
        E e = (E) event;
        handleEvent(e);
    }

    /**
     * 发送消息
     *
     * @param command
     * @param id
     * @throws IOException
     */
    public void sendMessage(Command command, String id) {
        switch (command.getSendType()) {
            case SEND_TO_USER:
                sendMessageToUser(command, id);
                break;
            case SEND_TO_GROUP:
                sendMessageToGroup(command, id);
                break;
            case SEND_TO_ALL_GROUP:
                sendMessage2AllGroup(command, id);
            default:
                break;
        }
    }

    /**
     * 发送有序消息集合
     *
     * @param command
     * @param id
     * @throws IOException
     */
    public void sendOrderMessage(List<? extends Command> commandList, String id) {
        List<? extends Command> _2User = commandList.stream()
            .filter(c -> c.getSendType().equals(SendTypeEnum.SEND_TO_USER))
            .collect(Collectors.toList());

        sendOrderMessage2User(_2User, id);

        List<? extends Command> _2Group = commandList.stream()
            .filter(c -> c.getSendType().equals(SendTypeEnum.SEND_TO_GROUP))
            .collect(Collectors.toList());

        sendOrderMessage2Group(_2Group, id);

        List<? extends Command> _2AllGroup = commandList.stream()
            .filter(c -> c.getSendType().equals(SendTypeEnum.SEND_TO_ALL_GROUP))
            .collect(Collectors.toList());
        sendOrderMessage2AllGroup(_2AllGroup, id);

    }

    private void sendOrderMessage2AllGroup(List<? extends Command> commandList, String typeId) {
        for (Map.Entry<String, List<T>> entry : sessionMap.entrySet()) {
            List<T> sessions = entry.getValue();
            T session = null;
            log.info("发送数据{} 给群组：{}", commandList, typeId);
            try {
                List<Command> sendCommandList;
                for (int i = 0; i < sessions.size(); i++) {
                    session = sessions.get(i);
                    sendCommandList = new ArrayList<>();
                    for (Command command : commandList) {
                        sendCommandList.add(command.beforeSend(session.getUser()));
                    }
                    session.sendOrderCommand(sendCommandList);
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", session.getUser(), e);
            }
        }
    }

    private void sendOrderMessage2Group(List<? extends Command> commandList, String typeId) {
        List<T> sessionList = sessionMap.get(typeId);
        if (sessionList != null) {
            T session = null;
            log.info("发送数据{} 给群组：{}", commandList, typeId);
            List<Command> sendCommandList;
            for (int i = 0; i < sessionList.size(); i++) {
                try {
                    session = sessionList.get(i);
                    sendCommandList = new ArrayList<>();
                    for (Command command : commandList) {
                        sendCommandList.add(command.beforeSend(session.getUser()));
                    }
                    session.sendOrderCommand(sendCommandList);
                } catch (IOException e) {
                    log.error("发送数据给用户：{}失败", session.getUser(), e);
                }
            }

        }
    }

    private void sendOrderMessage2User(List<? extends Command> commandList, String typeId) {
        List<T> sessionList = sessionMap.get(typeId);
        if (sessionList != null) {
            T session = null;
            log.info("发送数据{} 给用户：{}", commandList, typeId);
            try {
                List<Command> sendCommandList;
                for (int i = 0; i < sessionList.size(); i++) {
                    session = sessionList.get(i);
                    sendCommandList = new ArrayList<>();
                    for (Command command : commandList) {
                        sendCommandList.add(command.beforeSend(session.getUser()));
                    }
                    if (session.getUser().equals(GameContext.getUser())) {
                        session.sendOrderCommand(sendCommandList);
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", session.getUser(), e);
            }
        }
    }

    /**
     * 获取用户参与的组id
     * @param userId
     * @return
     */
    public String getUserGroupId(Integer userId) {
        for (Map.Entry<String, List<T>> entry : sessionMap.entrySet()) {
            for (T session : entry.getValue()) {
                if (session.getUser().getId().equals(userId)) {
                    return entry.getKey();
                }
            }
        }
        throw new AeException("用户不存在参与的组");
    }



    /**
     * 处理事件
     * @param e
     */
    protected abstract void handleEvent(E e);



    /**
     * 返回加入成功的返回信息
     * @param typeId
     * @return
     */
    protected Object getJoinSuccessMessage(String typeId) {
        return null;
    }


    /**
     * 执行session加入的回调
     *
     * @param session
     * @param sessionList
     */
    abstract void addNewSession(T session, List<T> sessionList);

    /**
     * 全部session 离开组
     *
     * @param typeId 组的id
     */
    abstract void removeGroup(String typeId);

    /**
     * 某一个session离开
     *
     * @param tSession
     * @param lastSession 剩余
     */
    abstract void removeSessionFromGroup(T tSession, List<T> lastSession);

    /**
     * 创建一个包装Session
     *
     * @param session
     * @param typeId
     * @param user
     * @return
     */
    abstract T createSession(Session session, String typeId, User user);

}
