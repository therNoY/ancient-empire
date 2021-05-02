package pers.mihao.ancient_empire.base.service.impl;

import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UnitTransferDao;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.service.UnitTransferService;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-10-24
 */
@Service
public class UnitTransferServiceImpl extends ComplexKeyServiceImpl<UnitTransferDao, UnitTransfer> implements UnitTransferService {

}
