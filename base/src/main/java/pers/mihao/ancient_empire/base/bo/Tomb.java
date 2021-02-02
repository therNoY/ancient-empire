package pers.mihao.ancient_empire.base.bo;

/**
 * 坟墓
 * @version 1.0
 * @auther mihao
 * @date 2021\2\1 0001 21:39
 */
public class Tomb extends Site{

    /**
     * 单位的死前类型Id
     */
    private Integer unitTypeId;

    /**
     * 存在的回合数
     */
    private Integer presenceNum;



    public Tomb(Integer row, Integer column, Integer unitTypeId) {
        super(row, column);
        this.unitTypeId = unitTypeId;
        this.presenceNum = 1;
    }

    public Tomb(Site site, Integer unitTypeId) {
        super(site);
        this.unitTypeId = unitTypeId;
        this.presenceNum = 1;
    }

    public Integer getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Integer unitTypeId) {
        this.unitTypeId = unitTypeId;
    }

    public Integer getPresenceNum() {
        return presenceNum;
    }

    public void setPresenceNum(Integer presenceNum) {
        this.presenceNum = presenceNum;
    }

    @Override
    public String toString() {
        return "Tomb{" +
                "unitTypeId=" + unitTypeId +
                ", presenceNum=" + presenceNum +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
