package pers.mihao.ancient_empire.auth.config;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.util.RespUtil;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 * @author hspcadmin
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("用户未登录");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSON.toJSONString(RespUtil.error(40001)));
        response.getWriter().flush();
    }
}
