package pers.mihao.ancient_empire.auth.util;

import com.alibaba.fastjson.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.dto.WeChatInfoDTO;
import pers.mihao.ancient_empire.common.util.HttpUtil;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import pers.mihao.ancient_empire.common.vo.AeException;


/**
 * 微信认证服务
 *
 * @Author mh32736
 * @Date 2021/5/22 22:45
 */
public class WeChatUtil {

    public static final String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取access_token.
     */
    public static String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 根据openId获取用户信息
     */
    public static String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?openid=";
    /**
     * 用oauth2获取用户信息.
     */
    public static String OAUTH2_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=%s";
    /**
     * 验证oauth2的access token是否有效.
     */
    public static String OAUTH2_VALIDATE_TOKEN_URL = "https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s";

    /**
     * 微信小程序code换取openId接口
     */
    public static String CODE_CHANGE_OPENID = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    /**
     * 微信小程序消息回复发送接口
     */
    public static String SEND_MSG = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    static Logger log = LoggerFactory.getLogger(WeChatUtil.class);

    public static String codeChangeSessionKey(String code, String appid, String secret) {
        String url = String.format(CODE_CHANGE_OPENID, appid, secret, code);
        Map<String, String> resp = HttpUtil.get(url, null);
        String body = resp.get("body");
        String sessionKey = null;
        if (body != null) {
            JSONObject object = JSONObject.parseObject(body);
            sessionKey = object.getString("session_key");
        }
        return sessionKey;
    }


    public static WeChatInfoDTO decrypt(String sessionKey, String encryptedData, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                log.info("解析出用户数据为:[{}]", result);
                return JSONObject.parseObject(result, WeChatInfoDTO.class);
            }
        } catch (Exception e) {
            log.error("微信小程序数据解析异常:[{}]", e.getMessage(), e);
        }
        throw new AeException(40015);
    }

}
