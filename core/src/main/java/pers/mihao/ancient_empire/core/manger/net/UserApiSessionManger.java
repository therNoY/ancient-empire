package pers.mihao.ancient_empire.core.manger.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.common.annotation.Manger;

/**
 * @Author mh32736
 * @Date 2021/3/3 22:00
 */
@Manger
public class UserApiSessionManger {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 维护用户和其session 一个用户只能在一个地方登录
     */
    private Map<Integer, UserApiSessionEndpoint> userSessionMap = new ConcurrentHashMap<>();

    /**
     * 添加用户消息
     * @param userApiSessionEndpoint
     */
    public void addNewUserSession(UserApiSessionEndpoint userApiSessionEndpoint) {
        User loginUser = userApiSessionEndpoint.getLoginUser();
        UserApiSessionEndpoint old;
        if ((old = userSessionMap.get(loginUser.getId())) != null) {
            // 一个用户只能登录一个,另一个会掉线
            log.info("同一个用户：{}在其他环境登录,需要关闭这个session", loginUser);
            old.closeSession();
        }

        userSessionMap.put(loginUser.getId(), userApiSessionEndpoint);
    }

    public void removeUserSession(User loginUser) {
        userSessionMap.remove(loginUser.getId());
    }
}
