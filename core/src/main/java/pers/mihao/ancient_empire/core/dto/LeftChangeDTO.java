package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Site;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\11 0011 10:16
 */
public class LeftChangeDTO extends Site{

    /**
     * 血量变化的数字
     */
    private Integer[] attach;


    public LeftChangeDTO() {
    }

    public LeftChangeDTO(Integer[] attach, Site site) {
        this.attach = attach;
        this.row = site.getRow();
        this.column = site.getColumn();
    }

    public LeftChangeDTO(Integer[] attach, Integer row, Integer column) {
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

}
