package com.mihao.ancient_empire.service;

import com.mihao.ancient_empire.common.vo.MyUserDetails;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.dto.auth_dto.LoginDto;
import com.mihao.ancient_empire.dto.auth_dto.RegisterDto;
import com.mihao.ancient_empire.dto.auth_dto.ReqUserDto;
import com.mihao.ancient_empire.dto.auth_dto.RespAuthDao;
import com.mihao.ancient_empire.entity.Permission;
import com.mihao.ancient_empire.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    User getUserByEmail(String email);

    void save(RegisterDto registerDto);

    String updateUserInfo(ReqUserDto user);
}
