package pers.mihao.ancient_empire.auth.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.dao.PermissionDAO;
import pers.mihao.ancient_empire.auth.entity.Permission;
import pers.mihao.ancient_empire.auth.service.PermissionService;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDAO, Permission> implements PermissionService {

}
