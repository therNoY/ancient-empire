package com.mihao.ancient_empire.dao;

import com.mihao.ancient_empire.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
public interface PermissionDao extends BaseMapper<Permission> {

    @Select("select name,value from permission p,role_permission_relation rp WHERE p.id = rp.permission_id and rp.role_id in" +
            "(SELECT r.id from role r,user_role_relation ur where r.id = ur.role_id and ur.user_id = #{id})")
    List<Permission> getPermissionByUserId(Integer id);
}
