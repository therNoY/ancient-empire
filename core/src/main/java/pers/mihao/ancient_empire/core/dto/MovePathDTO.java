package pers.mihao.ancient_empire.core.dto;

import java.util.List;

/**
 * @Author mh32736
 * @Date 2021/2/24 8:49
 */
public class MovePathDTO {

    /**
     * 移动路径记录
     */
    private List<PathPosition> positionList;

    /**
     * 消耗
     */
    private Integer deplete;

    public List<PathPosition> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PathPosition> positionList) {
        this.positionList = positionList;
    }

    public Integer getDeplete() {
        return deplete;
    }

    public void setDeplete(Integer deplete) {
        this.deplete = deplete;
    }
}
