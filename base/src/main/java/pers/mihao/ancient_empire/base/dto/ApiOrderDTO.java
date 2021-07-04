package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * @Author mihao
 * @Date 2021/4/29 20:07
 */
public class ApiOrderDTO extends ApiConditionDTO {

    /**
     * 根据星星总数排序
     */
    private String byStartSum;

    public String getByStartSum() {
        return byStartSum;
    }

    public void setByStartSum(String byStartSum) {
        this.byStartSum = byStartSum;
    }
}
