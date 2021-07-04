package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.entity.Buff;

/**
 * <p>
 * buff信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public interface BuffService extends IService<Buff> {

    /**
     * 获取buff详情
     * @param type
     * @return
     */
    Buff getByType(String type);
}
