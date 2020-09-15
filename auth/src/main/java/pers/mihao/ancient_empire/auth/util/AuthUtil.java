package pers.mihao.ancient_empire.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import pers.mihao.ancient_empire.auth.dto.MyUserDetails;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.vo.MyException;

/**
 * 和用户身份相关的工具类
 */
public class AuthUtil {

    static Logger log = LoggerFactory.getLogger(AuthUtil.class);

    static ThreadLocal<Integer> authId = new ThreadLocal<>();
    static ThreadLocal<MyUserDetails> userDetailsThreadLocal = new ThreadLocal<>();

    public static MyUserDetails getLoginUser() {

        if (userDetailsThreadLocal.get() == null) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (token == null) {
                log.error("错误的身份过滤");
                throw new MyException(40003);
            }
            MyUserDetails userDetails = (MyUserDetails) token.getPrincipal();
            userDetailsThreadLocal.set(userDetails);
        }
        return userDetailsThreadLocal.get();
    }

    public static Integer getAuthId() {

        if (authId.get() == null) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (token == null) {
                log.error("错误的身份过滤");
                throw new MyException(40003);
            }
            MyUserDetails userDetails = (MyUserDetails) token.getPrincipal();
            authId.set(userDetails.getUserId());
        }

        return authId.get();
    }


    @KnowledgePoint("对于threadLocal的使用如果是线程池使用（有回收的情况）就需要" +
            "每次使用完都清除掉，不然线程回收了，但是保存的对象没有回收，会造成内存泄漏")
    public static void clear(){
        authId.remove();
        userDetailsThreadLocal.remove();
    }
}
