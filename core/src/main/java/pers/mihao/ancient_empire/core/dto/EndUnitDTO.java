package pers.mihao.ancient_empire.core.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 行动结束对地图的影响
 */
public class EndUnitDTO implements Serializable {

    /**
     * 血量变化动画
     */
    private List<LifeChangeDTO> lifeChangeList;

    /**
     * 单位状态改变
     */
    private List<UnitStatusInfoDTO> unitStatusInfoDTOS;

    /**
     * 单位死亡DTO
     */
    private List<UnitDeadDTO> unitDeadDTOList;

    public List<LifeChangeDTO> getLifeChangeList() {
        return lifeChangeList;
    }

    public void setLifeChangeList(List<LifeChangeDTO> lifeChangeList) {
        this.lifeChangeList = lifeChangeList;
    }

    public List<UnitStatusInfoDTO> getUnitStatusInfoDTOS() {
        return unitStatusInfoDTOS;
    }

    public void setUnitStatusInfoDTOS(List<UnitStatusInfoDTO> unitStatusInfoDTOS) {
        this.unitStatusInfoDTOS = unitStatusInfoDTOS;
    }

    public List<UnitDeadDTO> getUnitDeadDTOList() {
        return unitDeadDTOList;
    }

    public void setUnitDeadDTOList(List<UnitDeadDTO> unitDeadDTOList) {
        this.unitDeadDTOList = unitDeadDTOList;
    }

    @Override
    public String toString() {
        return "EndUnitDTO{" +
                "lifeChangeList=" + lifeChangeList +
                ", unitStatusInfoDTOS=" + unitStatusInfoDTOS +
                ", unitDeadDTOList=" + unitDeadDTOList +
                '}';
    }
}
