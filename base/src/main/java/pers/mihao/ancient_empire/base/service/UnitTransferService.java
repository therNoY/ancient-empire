package pers.mihao.ancient_empire.base.service;

import java.util.List;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2020-10-24
 */
public interface UnitTransferService extends ComplexKeyService<UnitTransfer> {


    /**
     * 获取单位可以晋升的单位
     * @param unitId
     * @return
     */
    List<UnitTransfer> getTransferByUnitId(Integer unitId);
}
