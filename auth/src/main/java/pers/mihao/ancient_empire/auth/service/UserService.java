package pers.mihao.ancient_empire.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.auth.dto.ReqUserDto;
import pers.mihao.ancient_empire.auth.dto.RespAuthDao;
import pers.mihao.ancient_empire.auth.entity.Permission;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.common.dto.LoginDto;
import pers.mihao.ancient_empire.common.dto.RegisterDto;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
public interface UserService extends IService<User> {

    User getUserByNameOrEmail(String username);

    List<Permission> getPermissionList(Integer id);

    RespAuthDao login(LoginDto loginDto);

    String adminLogin(LoginDto loginDto);

    User getUserByName(String userName);

    User getUserById(Integer id);

    User getUserByEmail(String email);

    void save(RegisterDto registerDto);

    String updateUserInfo(ReqUserDto user);
}
