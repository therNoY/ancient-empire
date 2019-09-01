package com.mihao.ancient_empire.service.imp;

import com.mihao.ancient_empire.entity.Buff;
import com.mihao.ancient_empire.dao.BuffDao;
import com.mihao.ancient_empire.service.BuffService;
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
