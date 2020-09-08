package pers.mihao.ancient_empire.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import pers.mihao.ancient_empire.auth.entity.User;

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
