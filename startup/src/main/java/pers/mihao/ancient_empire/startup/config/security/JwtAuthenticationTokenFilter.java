package pers.mihao.ancient_empire.startup.config.security;

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
import pers.mihao.ancient_empire.common.util.JwtTokenHelper;

/**
 * JWT登录授权过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
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
            String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
            // 3. 判断token是否过期 如果token 没有过期就设置 信息到Spring Security 上下文中
            if (!jwtTokenHelper.validateToken(authToken)) {
                String username = jwtTokenHelper.getUserNameFromToken(authToken);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                if (username != null && securityContext.getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (!jwtTokenHelper.validateToken(authToken)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        securityContext.setAuthentication(authentication);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
