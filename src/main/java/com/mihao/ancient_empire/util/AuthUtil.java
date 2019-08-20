package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.common.vo.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    static Logger log = LoggerFactory.getLogger(AuthUtil.class);

    public static MyUserDetails getLoginUser() {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (token == null) {
            log.error("错误的身份过滤");
            throw new MyException(40003);
        }
        MyUserDetails userDetails = (MyUserDetails) token.getPrincipal();
        return userDetails;
    }

    public static Integer getAuthId() {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (token == null) {
            log.error("错误的身份过滤");
            throw new MyException(40003);
        }
        MyUserDetails userDetails = (MyUserDetails) token.getPrincipal();
        return userDetails.getUserId();
    }
}
