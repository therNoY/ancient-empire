package pers.mihao.ancient_empire.base;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\17 0017 21:37
 */
public class JwtUtilTest {


    /* 用户信息Key */
    private final String CLAIM_KEY_USER_ID = "sub";
    /* 创建时间KEY */
    private final String CLAIM_KEY_CREATED = "created";

    String mes = "123";

    @Test
    public void testMakeToken() {

        String token = generateToken(mes);

        System.out.println(token);

        Date date = getExpiredDateFromToken(token);

        System.out.println(date.getTime() - new Date().getTime() + " " + 14000 * 1000);

    }

    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    public  Date getExpiredDateFromToken(String token) {
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
                    .setSigningKey("mySecret")
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
        }
        return claims;
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, "mySecret")
                .compact();
    }


    /**
     * 生成token的过期时间
     *
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + 14000 * 1000);
    }

}
