package pers.mihao.ancient_empire.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.base.dao.AeSequenceDAO;
import pers.mihao.ancient_empire.base.service.AeSequenceService;

/**
 * @Author mihao
 * @Date 2021/5/9 10:15
 */
@Service
public class AeSequenceServiceImpl implements AeSequenceService {

    @Autowired
    AeSequenceDAO aeSequenceDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int getNewIdByType(String name) {
        int res = aeSequenceDAO.getNewId(name);
        aeSequenceDAO.increase(name);
        return res;
    }
}
