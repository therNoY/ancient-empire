package pers.mihao.ancient_empire.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.config.AppConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */
public class JwtTokenUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);
    /* 用户信息Key */
    private static final String CLAIM_KEY_USER_ID = "sub";
    /* 创建时间KEY */
    private static final String CLAIM_KEY_CREATED = "created";
    /* 秘钥 */
    private static String secret = AppConfig.get("jwt.secret");
    /* 过期时间 */
    private static Long expiration = Long.valueOf(AppConfig.get("jwt.expiration"));

    /**
     * 根据用户信息生成token
     *
     * @param userId
     * @return
     */
    public static String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    public static TokenInfo getTokenInfoFromToken(String token) {
        TokenInfo tokenInfo;
        try {
            Claims claims = getClaimsFromToken(token);
            String info = claims.getSubject();
            Date date = claims.getExpiration();
            tokenInfo = new TokenInfo(info, date);
        } catch (Exception e) {
            log.error("获取token信息异常", e);
            tokenInfo = null;
        }
        return tokenInfo;
    }


    /**
     * 从token中获取有效的登录名
     *
     * @param token
     * @return
     */
    public static String getEffectiveUserId(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            if (!isEffectiveToken(claims.getExpiration())) {
                return null;
            }
            userId = claims.getSubject();
        } catch (Exception e) {
            log.error("", e);
            userId = null;
        }
        return userId;
    }


    /**
     * 从token中获取有效的登录名
     *
     * @param token
     * @return
     */
    public static String getUserId(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            log.error("", e);
            userId = null;
        }
        return userId;
    }

    /**
     * 从token中获取过期时间
     * @param token
     * @return
     */
    public static Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }


    /**
     * 判断token是否已经失效
     * true 还生效
     */
    public static boolean isEffectiveToken(Date date) {
        // TODO 这里需要对过期时间进行判断, 如果即将过期，要重新签发token
        return date.after(new Date());
    }


    /**
     * 判断token是否可以被刷新
     */
//    public static boolean canRefresh(String token) {
//        return !isEffectiveToken(token);
//    }

    /**
     * 刷新token
     */
//    public static String refreshToken(String token) {
//        Claims claims = getClaimsFromToken(token);
//        claims.put(CLAIM_KEY_CREATED, new date());
//        return generateToken(claims);
//    }

    /**
     * 根据负载生成token
     * @param claims
     * @return
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成token的过期时间
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public static class TokenInfo{
        String info;
        Date date;

        public TokenInfo() {
        }

        public TokenInfo(String info, Date date) {
            this.info = info;
            this.date = date;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

}
