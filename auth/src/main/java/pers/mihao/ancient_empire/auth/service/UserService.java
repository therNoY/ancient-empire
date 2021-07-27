package pers.mihao.ancient_empire.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.auth.dto.ReqUserDTO;
import pers.mihao.ancient_empire.auth.dto.RespAuthDAO;
import pers.mihao.ancient_empire.auth.entity.Permission;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.common.dto.LoginDTO;
import pers.mihao.ancient_empire.common.dto.RegisterDTO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名邮箱 手机号登录
     * @param username
     * @return
     */
    User getUserByNameOrEmail(String username);

    /**
     * 获取用户权限
     * @param id
     * @return
     */
    List<Permission> getPermissionList(Integer id);

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    RespAuthDAO login(LoginDTO loginDto);


    User getUserByName(String userName);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    User getUserById(Integer id);

    /**
     * 邮箱不能重复
     * @param email
     * @return
     */
    User getUserByEmail(String email);

    void save(RegisterDTO registerDto);

    /**
     * 修改信息
     * @param user
     * @return
     */
    String updateUserInfo(ReqUserDTO user);

    /**
     * 根据手机号获取用户
     * @param phone
     * @return
     */
    User getUserByPhone(String phone);

    RespAuthDAO registerWeChatUser(RegisterDTO registerDto);
}
