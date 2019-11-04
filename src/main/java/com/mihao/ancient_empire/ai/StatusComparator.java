package com.mihao.ancient_empire.ai;

import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.Unit;

import java.util.Comparator;

/**
 * 单位状态排序的比较器
 */
public class StatusComparator implements Comparator<Unit> {

    @Override
    public int compare(Unit u1, Unit u2) {
        if (u1.getStatus().equals(StateEnum.EXCITED.type())) {
            return 1;
        }else if (u2.getStatus().equals(StateEnum.EXCITED.type())) {
            return -1;
        }
        return 0;
    }
}
