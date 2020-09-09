package pers.mihao.ancient_empire.core;

import java.util.Comparator;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.enums.StateEnum;

/**
 * 单位状态排序的比较器
 */
public class StatusComparator implements Comparator<Unit> {

    @Override
    public int compare(Unit u1, Unit u2) {
        if (u1.getStatus() != null && u1.getStatus().equals(StateEnum.EXCITED.type())) {
            return 1;
        }else if (u2.getStatus() != null && u2.getStatus().equals(StateEnum.EXCITED.type())) {
            return -1;
        }
        return 0;
    }
}
