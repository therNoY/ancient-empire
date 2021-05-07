package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\12\1 0001 22:23
 */
public class ReqUserTemplateDTO extends ApiConditionDTO {

    /**
     * 根据什么排序
     */
    private String byStartSum;

    public String getByStartSum() {
        return byStartSum;
    }

    public void setByStartSum(String byStartSum) {
        this.byStartSum = byStartSum;
    }
}
