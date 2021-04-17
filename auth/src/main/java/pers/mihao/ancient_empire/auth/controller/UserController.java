package pers.mihao.ancient_empire.auth.controller;


import com.sun.deploy.net.HttpResponse;
import java.util.UUID;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
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
import pers.mihao.ancient_empire.auth.dto.ChangePwdDTO;
import pers.mihao.ancient_empire.auth.dto.ReqUserDto;
import pers.mihao.ancient_empire.auth.dto.RespAuthDao;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDTO;
import pers.mihao.ancient_empire.common.email.EmailService;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import javax.websocket.server.PathParam;

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

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @PostMapping("/user/login")
    public RespJson login(@RequestBody @Validated LoginDto loginDto, BindingResult result, HttpServletResponse response) {
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
    public RespJson userRegister(@RequestBody @Validated RegisterDTO registerDto, BindingResult result) {
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
        RedisUtil.set(uuid, registerDto, 600L);
        return RespUtil.successResJson();
    }

    /**
     * 用户通过邮箱确认注册的api
     */
    @GetMapping("/register")
    public ModelAndView registerCallback(@RequestParam String token) {
        RegisterDTO registerDto = null;
        if ((registerDto = RedisUtil.getObject(token, RegisterDTO.class)) != null) {
            userService.save(registerDto);
            return new ModelAndView("registerSuccess");
        }else {
            return new ModelAndView("registerErr");
        }
    }


    /**
     * 修改用户密码
     * @param pwdDto
     * @param result
     * @return
     */
    @PostMapping("/api/user/changePwd")
    public RespJson changePwd(@RequestBody @Validated ChangePwdDTO pwdDto, BindingResult result) {
        // 根据Token 获取用户信息
        User user = userService.getById(pwdDto.getUserId());
        // 验证密码是否正确
        if (passwordEncoder.matches(pwdDto.getOldPassword(), user.getPassword())) {
            RedisUtil.delKey(CatchKey.getKey(CatchKey.USER_INFO)  + user.getName());
            user.setPassword(passwordEncoder.encode(pwdDto.getNewPassword()));
            userService.updateById(user);
            return RespUtil.successResJson(JwtTokenUtil.generateToken(user.getId().toString()));
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

    @GetMapping("/api/user/name")
    public RespJson getUserNameById(@PathParam("id") Integer userId) {
        return RespUtil.successResJson(userService.getUserById(userId).getName());
    }
}
