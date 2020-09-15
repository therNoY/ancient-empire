package pers.mihao.ancient_empire.auth.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.dao.PermissionDao;
import pers.mihao.ancient_empire.auth.dao.UserDao;
import pers.mihao.ancient_empire.auth.dao.UserRoleRelationDao;
import pers.mihao.ancient_empire.auth.dto.ReqUserDto;
import pers.mihao.ancient_empire.auth.dto.RespAuthDao;
import pers.mihao.ancient_empire.auth.entity.Permission;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserRoleRelation;
import pers.mihao.ancient_empire.auth.enums.RoleEnum;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.constant.RedisKey;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDto;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RedisHelper;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    UserDao userDao;
    @Autowired
    PermissionDao permissionDao;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRoleRelationDao userRoleRelationDao;
    @Autowired
    RedisHelper redisHelper;

    @Override
    @Cacheable(RedisKey.USER_INFO)
    public User getUserByNameOrEmail(String username) {
        log.info("query {} from DB", username);
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("name", username)
                .or()
                .eq("email", username);
        return userDao.selectOne(wrapper);
    }

    @Override
    public List<Permission> getPermissionList(Integer id) {
        return permissionDao.getPermissionByUserId(id);
    }

    /**
     * 验证登录信息返回验证的token
     *
     * @param loginDto
     * @return
     */
    @Override
    public RespAuthDao login(LoginDto loginDto) {
        String token;
        User loginUser = getUserByNameOrEmail(loginDto.getUserName());
        if (loginUser != null && passwordEncoder.matches(loginDto.getPassword(), loginUser.getPassword())) {
            token = JwtTokenUtil.generateToken(loginUser.getId().toString());
            return new RespAuthDao(loginUser.getName(), loginDto.getPassword(), token);
        }
        return null;
    }

    /**
     * 管理员登录
     *
     * @param loginDto
     * @return
     */
    @Override
    public String adminLogin(LoginDto loginDto) {
        User loginUser = getUserByNameOrEmail(loginDto.getUserName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUserName());
        for (GrantedAuthority author : userDetails.getAuthorities()) {
            if (author.toString().equals(RoleEnum.ADMIN.type())) {
                return JwtTokenUtil.generateToken(userDetails.getUsername());
            }
        }
        return null;
    }

    @Override
    public User getUserByName(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("name", userName);
        return userDao.selectOne(wrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("email", email);
        return userDao.selectOne(wrapper);
    }

    @Override
    public void save(RegisterDto registerDto) {
        User user = new User();
        user.setName(registerDto.getUserName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setEmail(registerDto.getEmail());
        userDao.insert(user);
        if (user.getId() != null) {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setUserId(user.getId());
            userRoleRelationDao.insert(userRoleRelation);
            return;
        }
        log.error("插入用户没有返回主键{}", user);
    }

    /**
     * 更新用户信息 更新成功返回新的token
     *
     * @param user
     * @return
     */
    @Override
    public String updateUserInfo(ReqUserDto user) {
        // 1.防止用户伪造 通过Token 获取当前登录对象 当前用户只能更改当前用户
        if (user.getId() - AuthUtil.getAuthId() != 0) {
            return null;
        } else {
            // 清除缓存
            redisHelper.delKey(RedisKey.USER_INFO_ + userDao.selectById(user.getId()).getName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.updateByReqUserDto(user.getUserName(), user.getPassword(), user.getId());
            return JwtTokenUtil.generateToken(user.getUserName());
        }
    }

}
