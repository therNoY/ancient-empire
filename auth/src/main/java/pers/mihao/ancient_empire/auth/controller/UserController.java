package pers.mihao.ancient_empire.auth.controller;


import java.util.UUID;
import javax.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import pers.mihao.ancient_empire.auth.dto.ChangePwdDTO;
import pers.mihao.ancient_empire.auth.dto.ReqUserDTO;
import pers.mihao.ancient_empire.auth.dto.RespAuthDAO;
import pers.mihao.ancient_empire.auth.dto.WeChatInfoDTO;
import pers.mihao.ancient_empire.auth.dto.WeChatSourceInfoDTO;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.WeChatUtil;
import pers.mihao.ancient_empire.common.constant.CacheKey;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDTO;
import pers.mihao.ancient_empire.common.email.EmailService;
import pers.mihao.ancient_empire.common.base_catch.CatchUtil;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@RestController
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${wx.appid}")
    String wxAppId;

    @Value("${wx.secret}")
    String wxSecret;

    /**
     * 用户登录
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/user/login")
    public RespAuthDAO login(@RequestBody LoginDto loginDto) {
        RespAuthDAO respAuthDao = userService.login(loginDto);
        if (respAuthDao == null) {
            throw new AeException(40011);
        }
        return respAuthDao;
    }

    /**
     * 用户登录
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/admin/login")
    public String adminLogin(@RequestBody LoginDto loginDto) {
        String token = userService.adminLogin(loginDto);
        if (token == null) {
            throw new AeException(40011);
        }
        return token;
    }

    /**
     * 用户请求注册
     *
     * @param registerDto
     * @param result
     * @return
     */
    @PostMapping("/user/register")
    public RespAuthDAO userRegister(@RequestBody RegisterDTO registerDto) {
        User user = userService.getUserByName(registerDto.getUserName());
        if (user != null) {
            log.error("用户注册错误 用户名 {} 重复", user.getName());
            throw new AeException(40012);
        }

        if (StringUtil.isNotBlack(registerDto.getPhone())) {
            RespAuthDAO respAuthDAO = userService.registerWeChatUser(registerDto);
            return respAuthDAO;
        } else {
            // 邮箱注册 先验证邮箱是否存在 再验证用户是否存在
            User userByEmail = userService.getUserByEmail(registerDto.getEmail());
            if (userByEmail != null) {
                log.error("用户注册错误 邮箱 {} 已注册", userByEmail.getName());
                throw new AeException(40013);
            }
            // 准备发送给邮件服务器
            // 1.获取token
            String uuid = UUID.randomUUID().toString();
            // 2.发送邮件
            emailService.sendRegisterEmail(registerDto, uuid);
            // 3.放到缓存中 key email+_REGISTER 时间60s
            CatchUtil.set(uuid, registerDto, 600L);
            return null;
        }
    }


    /**
     * 用户通过邮箱确认注册的api
     */
    @GetMapping("/register")
    public ModelAndView registerCallback(@RequestParam String token) {
        RegisterDTO registerDto = null;
        if ((registerDto = CatchUtil.getObject(token, RegisterDTO.class)) != null) {
            userService.save(registerDto);
            return new ModelAndView("registerSuccess");
        } else {
            return new ModelAndView("registerErr");
        }
    }


    /**
     * 修改用户密码
     *
     * @param pwdDto
     * @param result
     * @return
     */
    @PostMapping("/api/user/changePwd")
    public String changePwd(@RequestBody ChangePwdDTO pwdDto) {
        // 根据Token 获取用户信息
        User user = userService.getById(pwdDto.getUserId());
        // 验证密码是否正确
        if (passwordEncoder.matches(pwdDto.getOldPassword(), user.getPassword())) {
            CatchUtil.delKey(CacheKey.getKey(CacheKey.USER_INFO) + user.getName());
            user.setPassword(passwordEncoder.encode(pwdDto.getNewPassword()));
            userService.updateById(user);
            return JwtTokenUtil.generateToken(user.getId().toString());
        }
        throw new AeException(40014);
    }

    /**
     * 更新用户信息 返回Token
     *
     * @param user
     * @param result
     * @return
     */
    @PutMapping("/api/user")
    public String changeUserInfo(@RequestBody ReqUserDTO user) {
        String token = userService.updateUserInfo(user);
        if (token == null) {
            throw new AeException(40003);
        }
        return token;
    }

    /**
     * 根据Id获取用户名字
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/user/name")
    public String getUserNameById(@PathParam("id") Integer userId) {
        return userService.getUserById(userId).getName();
    }


    /**
     * 获取微信用户的手机
     *
     * @param weChatSourceInfoDTO
     * @return
     */
    @PostMapping("/user/getWeiXinPhone")
    public WeChatInfoDTO getWeiXinUserInfo(@RequestBody WeChatSourceInfoDTO weChatSourceInfoDTO) {

        // 1.获取用户手机号
        String codeSessionKey = WeChatUtil.codeChangeSessionKey(weChatSourceInfoDTO.getCode(), wxAppId, wxSecret);
        if (StringUtil.isBlack(codeSessionKey)) {
            throw new AeException(40016);
        }
        WeChatInfoDTO phoneInfo = WeChatUtil
            .decrypt(codeSessionKey, weChatSourceInfoDTO.getEncryptedData(), weChatSourceInfoDTO.getIv());

        User user = userService.getUserByPhone(phoneInfo.getPhoneNumber());
        if (user != null) {
            // 用户属于老用户
            phoneInfo.setUserName(user.getName());
            String token = JwtTokenUtil.generateToken(user.getId().toString());
            phoneInfo.setToken(token);
            phoneInfo.setUserId(user.getId().toString());
        } else {
            // 新用户
            WeChatInfoDTO userInfo = WeChatUtil.decrypt(codeSessionKey, weChatSourceInfoDTO.getUserInfoEncrypted(),
                weChatSourceInfoDTO.getUserInfoIv());
            BeanUtil.copyValueByGetSet(userInfo, phoneInfo);
        }
        return phoneInfo;
    }

}
