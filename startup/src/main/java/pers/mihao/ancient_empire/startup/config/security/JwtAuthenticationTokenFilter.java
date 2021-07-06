package pers.mihao.ancient_empire.startup.config.security;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.mihao.ancient_empire.auth.service.UserSettingService;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;

/**
 * JWT登录授权过滤器
 *
 * @author mihao
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserSettingService userSettingService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {
        // 1.判断如果是OPTIONS 请求直接掉过
        if (request.getMethod().equals(RequestMethod.OPTIONS.toString())) {
            chain.doFilter(request, response);
            return;
        }
        // 1.1 判断如果是不需要验证身份的就跳过
        if (!request.getRequestURI().startsWith("/api") && !request.getRequestURI().startsWith("/root")) {
            chain.doFilter(request, response);
            return;
        }
        // 2. 首先看请求头中是否有Token
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            // The part after "Bearer "
            String authToken = authHeader.substring(this.tokenHead.length());
            // 3. 判断token是否过期 如果token 没有过期就设置 信息到Spring Security 上下文中
            JwtTokenUtil.TokenInfo tokenInfo = JwtTokenUtil.getTokenInfoFromToken(authToken);
            String userId = null;
            if (tokenInfo != null && JwtTokenUtil.isEffectiveToken(tokenInfo.getDate())
                && (userId = tokenInfo.getInfo()) != null) {
                LoginUserHolder.setUserId(Integer.parseInt(userId));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
                LoginUserHolder.setLoginUser(userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authentication);
                // 设置语言
                LanguageEnum languageEnum = EnumUtil
                    .valueOf(LanguageEnum.class, userSettingService.getUserSettingById(LoginUserHolder.getUserId()).getLanguage());
                LoginUserHolder.setLanguage(languageEnum);
            }
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().println(JSON.toJSONString(RespUtil.error(40003)));
            response.getWriter().flush();
            return;
        }
        try {
            chain.doFilter(request, response);
        } finally {
            // 保证清除
            LoginUserHolder.clear();
        }
    }
}
