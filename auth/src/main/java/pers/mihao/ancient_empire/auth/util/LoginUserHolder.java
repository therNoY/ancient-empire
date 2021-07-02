package pers.mihao.ancient_empire.auth.util;

import org.springframework.security.core.userdetails.UserDetails;
import pers.mihao.ancient_empire.auth.dto.MyUserDetails;
import pers.mihao.ancient_empire.common.util.CurrUserIdHolder;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;

/**
 * 和用户身份相关的工具类
 *
 * @author hspcadmin
 */
public class LoginUserHolder extends CurrUserIdHolder {

    private static ThreadLocal<MyUserDetails> userDetailsThreadLocal = new ThreadLocal<>();

    public static MyUserDetails getLoginUser() {
        return userDetailsThreadLocal.get();
    }

    public static void setLoginUser(UserDetails user) {
        userDetailsThreadLocal.set((MyUserDetails) user);
    }

    public static Integer getUserId() {
        return CurrUserIdHolder.getUserId();
    }

    public static void setUserId(Integer userId) {
        CurrUserIdHolder.setUserId(userId);
    }

    @KnowledgePoint("对于threadLocal的使用如果是线程池使用（有回收的情况）就需要" +
        "每次使用完都清除掉，不然线程回收了，但是保存的对象没有回收，会造成内存泄漏")
    public static void clear() {
        CurrUserIdHolder.clean();
        userDetailsThreadLocal.remove();
    }
}
