package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\12\1 0001 23:54
 */
public class TemplateCountDTO implements Serializable {

    private Integer count;

    private Integer sum;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
