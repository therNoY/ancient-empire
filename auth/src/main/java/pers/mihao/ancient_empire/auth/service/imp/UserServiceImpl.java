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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.dao.PermissionDao;
import pers.mihao.ancient_empire.auth.dao.UserDao;
import pers.mihao.ancient_empire.auth.dao.UserRoleRelationDao;
import pers.mihao.ancient_empire.auth.dto.ReqUserDTO;
import pers.mihao.ancient_empire.auth.dto.RespAuthDAO;
import pers.mihao.ancient_empire.auth.entity.Permission;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserRoleRelation;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDTO;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;

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

    @Override
    @Cacheable(CatchKey.USER_INFO)
    public User getUserByNameOrEmail(String username) {
        log.info("查询用户 {} 登录信息", username);
        return userDao.getUserByNameOrEmail(username);
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
    public RespAuthDAO login(LoginDto loginDto) {
        String token;
        User loginUser = getUserByNameOrEmail(loginDto.getUserName());
        if (loginUser != null && passwordEncoder.matches(loginDto.getPassword(), loginUser.getPassword())) {
            token = JwtTokenUtil.generateToken(loginUser.getId().toString());
            log.info("给用户：{}生成token", loginDto);
            RespAuthDAO respAuthDao = new RespAuthDAO(loginUser.getName(), loginDto.getPassword(), token);
            respAuthDao.setUserId(loginUser.getId());
            return respAuthDao;
        }
        return null;
    }

    /**
     * 管理员登录
     * FIXME 改成不需要管理员的
     * @param loginDto
     * @return
     */
    @Override
    public String adminLogin(LoginDto loginDto) {
        User loginUser = getUserByNameOrEmail(loginDto.getUserName());
//        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUserName());
//        for (GrantedAuthority author : userDetails.getAuthorities()) {
//            if (author.toString().equals(RoleEnum.ADMIN.type())) {
//                return JwtTokenUtil.generateToken(userDetails.getUsername());
//            }
//        }
        return JwtTokenUtil.generateToken(loginUser.getId().toString());
    }

    @Override
    public User getUserByName(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("name", userName);
        return userDao.selectOne(wrapper);
    }

    @Cacheable(CatchKey.USER)
    @Override
    public User getUserById(Integer id) {
        return super.getById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("email", email);
        return userDao.selectOne(wrapper);
    }

    @Override
    public void save(RegisterDTO registerDto) {
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
    public String updateUserInfo(ReqUserDTO user) {
        // 清除缓存
        RedisUtil.delKey(CatchKey.getKey(CatchKey.USER_INFO) + userDao.selectById(user.getUserId()).getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.updateByReqUserDto(user.getUserName(), user.getPassword(), user.getUserId());
        return JwtTokenUtil.generateToken(user.getUserName());
    }

}
