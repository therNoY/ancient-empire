package pers.mihao.ancient_empire.auth.dao;

import pers.mihao.ancient_empire.auth.entity.Permission;
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
public interface PermissionDAO extends BaseMapper<Permission> {

    /**
     * 获取用户权限
     * @param id
     * @return
     */
    List<Permission> getPermissionByUserId(Integer id);
}
