package pers.mihao.ancient_empire.base.bo;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 军队
 */
public class Army implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    // 颜色
    @NotEmpty(message = "颜色不能为空")
    private String color;
    private Integer money;
    // 所有的单位地图信息
    private List<Unit> units;
    // 军队的阵营
    @NotNull(message = "阵营不能为空")
    @DecimalMin("0")
    private Integer camp;
    // 军队的先行顺序
    @NotNull(message = "顺序不能为空")
    @DecimalMin("0")
    private Integer order;
    // 当前的人口
    private Integer pop;
    // 单位类型 user ai no
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public Integer getCamp() {
        return camp;
    }

    public void setCamp(Integer camp) {
        this.camp = camp;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getPop() {
        return pop;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
