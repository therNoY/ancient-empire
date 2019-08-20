package com.mihao.ancient_empire.service.imp;

import com.mihao.ancient_empire.entity.Role;
import com.mihao.ancient_empire.dao.RoleDao;
import com.mihao.ancient_empire.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

}
