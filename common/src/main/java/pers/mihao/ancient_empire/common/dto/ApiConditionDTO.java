package pers.mihao.ancient_empire.common.dto;

/**
 * 有一个查询条件的分页
 * @version 1.0
 * @author mihao
 * @date 2021\3\1 0001 21:14
 */
public class ApiConditionDTO extends ApiPageDTO{

    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
