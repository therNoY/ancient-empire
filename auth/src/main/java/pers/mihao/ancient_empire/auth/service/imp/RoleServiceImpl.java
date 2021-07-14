package pers.mihao.ancient_empire.auth.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.dao.RoleDAO;
import pers.mihao.ancient_empire.auth.entity.Role;
import pers.mihao.ancient_empire.auth.service.RoleService;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-10
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDAO, Role> implements RoleService {

}
