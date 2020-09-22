package pers.mihao.ancient_empire.auth.controller;


import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import pers.mihao.ancient_empire.auth.dto.CheckPwdDto;
import pers.mihao.ancient_empire.auth.dto.MyUserDetails;
import pers.mihao.ancient_empire.auth.dto.ReqUserDto;
import pers.mihao.ancient_empire.auth.dto.RespAuthDao;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDto;
import pers.mihao.ancient_empire.common.email.EmailService;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

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
    RedisUtil redisUtil;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @PostMapping("/user/login")
    public RespJson login(@RequestBody @Validated LoginDto loginDto, BindingResult result) {
        RespAuthDao respAuthDao = userService.login(loginDto);
        if (respAuthDao == null) {
            return RespUtil.error(40011);
        }else {
            return RespUtil.successResJson(respAuthDao);
        }
    }

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @PostMapping("/admin/login")
    public RespJson adminLogin(@RequestBody @Validated LoginDto loginDto, BindingResult result) {
        String token = userService.adminLogin(loginDto);
        if (token == null) {
            return RespUtil.error(40011);
        }else {
            return RespUtil.successResJson(token);
        }
    }

    /**
     * 用户请求注册
     * @param registerDto
     * @param result
     * @return
     */
    @PostMapping("/user/register")
    public RespJson userRegister(@RequestBody @Validated RegisterDto registerDto, BindingResult result) {
       // 先验证邮箱是否存在 再验证用户是否存在
        User userByEmail = userService.getUserByEmail(registerDto.getEmail());
        if (userByEmail != null) {
            log.error("用户注册错误 邮箱 {} 已注册", userByEmail.getName());
            return RespUtil.error(40013);
        }
        User user = userService.getUserByName(registerDto.getUserName());
        if (user != null) {
            log.error("用户注册错误 用户名 {} 重复", user.getName());
            return RespUtil.error(40012);
        }
        // 准备发送给邮件服务器
        // 1.获取token
        String uuid = UUID.randomUUID().toString();
        // 2.发送邮件
        emailService.sendRegisterEmail(registerDto, uuid);
        // 3.放到缓存中 key email+_REGISTER 时间60s
        redisUtil.set(uuid, registerDto, 600l);
        return RespUtil.successResJson();
    }

    /**
     * 用户通过邮箱确认注册的api
     */
    @GetMapping("/register")
    public ModelAndView registerCallback(@RequestParam String token) {
        RegisterDto registerDto = null;
        if ((registerDto = redisUtil.getObject(token, RegisterDto.class)) != null) {
            userService.save(registerDto);
            return new ModelAndView("registerSuccess");
        }else {
            return new ModelAndView("registerErr");
        }
    }


    /**
     * 验证密码
     * @param pwdDto
     * @param result
     * @return
     */
    @PostMapping("/api/user/checkPwd")
    public RespJson checkPwd(@RequestBody @Validated CheckPwdDto pwdDto, BindingResult result) {
        // 根据Token 获取用户信息
        MyUserDetails userDetails = AuthUtil.getLoginUser();
        // 验证密码是否正确
        if (passwordEncoder.matches(pwdDto.getPassword(), userDetails.getPassword())) {
            // 返回用户Id
            return RespUtil.successResJson(userDetails.getUserId());
        }
        return RespUtil.error(40014);
    }

    /**
     * 更新用户信息 返回Token
     * @param user
     * @param result
     * @return
     */
    @PutMapping("/api/user")
    public RespJson changeUserInfo (@RequestBody @Validated ReqUserDto user, BindingResult result) {
        String token = userService.updateUserInfo(user);
        if (token == null) {
            return RespUtil.error(40003);
        }
        return RespUtil.successResJson(token);
    }
}
