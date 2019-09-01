package com.mihao.ancient_empire.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dao.UnitLevelMesDao;
import com.mihao.ancient_empire.dao.UnitMesDao;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.service.UnitMesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mihao.ancient_empire.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 单位信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class UnitMesServiceImpl extends ServiceImpl<UnitMesDao, UnitMes> implements UnitMesService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UnitMesDao unitMesDao;
    @Autowired
    UnitLevelMesDao unitLevelMesDao;

    /**
     * 获取所有单位信息
     * @param page
     * @return
     */
    @Override
    public IPage<UnitMes> getList(Page<UnitMes> page) {
        return unitMesDao.selectPage(page, null);
    }

    /**
     * 更新单位信息
     * @param unitMes
     */
    @Override
    @Transactional
    public void saveUnitMes(UnitMes unitMes) {
        if (unitMes.getId() != null) {
            unitMes.setCreateUserId(AuthUtil.getAuthId());
            unitMesDao.updateById(unitMes);
        }else {
            unitMesDao.insert(unitMes);

            if (unitMes.getId() != null) {
                for (int i = 0; i < 4; i++) {
                    UnitLevelMes unitLevelMes = new UnitLevelMes();
                    unitLevelMes.setUnitId(unitMes.getId());
                    unitLevelMes.setLevel(i);
                    unitLevelMesDao.insert(unitLevelMes);
                }
            }else {
                log.error("没有获取到插入的主键");
                throw new MyException(500);
            }

        }
    }

    /**
     * 根据创建者的用户获取 可用的Unit
     * 获取所有可用单位信息
     * @return
     */
    @Override
    @Cacheable(value = RedisKey.ENABLE_UNIT)
    public List<UnitMes> getEnableUnitByUserId(Integer id) {
        log.info(">>>>用户从数据库中获取可用单位<<<<");
        QueryWrapper wrapper = new QueryWrapper<UnitMes>();
        wrapper.eq("enable", 1);
//        wrapper.eq("create_user_id", id);
        List<UnitMes> unitMes = unitMesDao.selectList(wrapper);
        return unitMes;
    }

    /**
     * 获取单位信息byTyoe
     * @return
     * @param type
     */
    @Cacheable(RedisKey.UNIT_MES)
    @Override
    public UnitMes getByType(String type) {
        UnitMes unitMes = unitMesDao.selectOne(new QueryWrapper<UnitMes>().eq("type", type));
        return unitMes;
    }
}
