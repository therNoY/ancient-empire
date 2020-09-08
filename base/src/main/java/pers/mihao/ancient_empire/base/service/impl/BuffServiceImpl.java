package pers.mihao.ancient_empire.base.service.impl;

import com.mihao.ancient_empire.entity.Buff;
import com.mihao.ancient_empire.dao.BuffDao;
import pers.mihao.ancient_empire.auth.service.BuffService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * buff信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Service
public class BuffServiceImpl extends ServiceImpl<BuffDao, Buff> implements BuffService {

}
