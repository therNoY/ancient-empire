package com.mihao.ancient_empire.service.imp;

import com.mihao.ancient_empire.entity.Permission;
import com.mihao.ancient_empire.dao.PermissionDao;
import com.mihao.ancient_empire.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

}
