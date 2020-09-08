package pers.mihao.ancient_empire.auth.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.dao.RolePermissionRelationDao;
import pers.mihao.ancient_empire.auth.entity.RolePermissionRelation;
import pers.mihao.ancient_empire.auth.service.RolePermissionRelationService;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class RolePermissionRelationServiceImpl extends ServiceImpl<RolePermissionRelationDao, RolePermissionRelation> implements RolePermissionRelationService {

}
