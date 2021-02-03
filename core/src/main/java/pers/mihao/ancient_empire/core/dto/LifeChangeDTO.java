package pers.mihao.ancient_empire.core.dto;

import java.util.Arrays;
import pers.mihao.ancient_empire.base.bo.Site;

/**
 * 生命修改
 * @version 1.0
 * @author mihao
 * @date 2020\10\11 0011 10:16
 */
public class LifeChangeDTO extends Site{

    /**
     * 血量变化的数字
     */
    private Integer[] attach;


    public LifeChangeDTO() {
    }

    public LifeChangeDTO(Integer[] attach, Site site) {
        this.attach = attach;
        this.row = site.getRow();
        this.column = site.getColumn();
    }

    public LifeChangeDTO(Integer[] attach, Integer row, Integer column) {
        this.attach = attach;
        this.row = row;
        this.column = column;
    }

    public Integer[] getAttach() {
        return attach;
    }

    public void setAttach(Integer[] attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "LifeChangeDTO{" +
            "attach=" + Arrays.toString(attach) +
            '}';
    }
}
