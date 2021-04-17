package pers.mihao.ancient_empire.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pers.mihao.ancient_empire.auth.dto.MyUserDetails;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

/**
 * 和用户身份相关的工具类
 */
public class AuthUtil {

    private static Logger log = LoggerFactory.getLogger(AuthUtil.class);

    private static ThreadLocal<Integer> authId = new ThreadLocal<>();
    private static ThreadLocal<MyUserDetails> userDetailsThreadLocal = new ThreadLocal<>();

    public static MyUserDetails getLoginUser() {
        return userDetailsThreadLocal.get();
    }

    public static void setLoginUser(UserDetails user) {
        userDetailsThreadLocal.set((MyUserDetails) user);
    }

    public static Integer getUserId() {
        return authId.get();
    }

    public static void setUserId(Integer userId){
        authId.set(userId);
    }

    @KnowledgePoint("对于threadLocal的使用如果是线程池使用（有回收的情况）就需要" +
            "每次使用完都清除掉，不然线程回收了，但是保存的对象没有回收，会造成内存泄漏")
    public static void clear(){
        authId.remove();
        userDetailsThreadLocal.remove();
    }
}
