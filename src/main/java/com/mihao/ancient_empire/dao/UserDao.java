package com.mihao.ancient_empire.dao;

import com.mihao.ancient_empire.dto.auth_dto.LoginDto;
import com.mihao.ancient_empire.dto.auth_dto.ReqUserDto;
import com.mihao.ancient_empire.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
public interface UserDao extends BaseMapper<User> {

    @Update("update user u set u.name= #{name}, u.password=#{pwd} where u.id = #{id}")
    void updateByReqUserDto(@Param("name") String userName, @Param("pwd") String password, @Param("id") Integer id);
}
