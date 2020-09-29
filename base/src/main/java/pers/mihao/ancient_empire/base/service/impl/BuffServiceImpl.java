package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.BuffDAO;
import pers.mihao.ancient_empire.base.entity.Buff;
import pers.mihao.ancient_empire.base.service.BuffService;

/**
 * <p>
 * buff信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Service
public class BuffServiceImpl extends ServiceImpl<BuffDAO, Buff> implements BuffService {

}
