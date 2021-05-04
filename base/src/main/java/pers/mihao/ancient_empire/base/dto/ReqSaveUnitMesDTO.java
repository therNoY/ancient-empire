package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.common.annotation.ValidatedBean;

import java.io.Serializable;
import java.util.List;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * 保存单位信息
 * @version 1.0
 * @author mihao
 * @date 2021\1\17 0017 18:55
 */
public class ReqSaveUnitMesDTO extends ApiRequestDTO {
    /**
     * 基础信息
     */
    private UnitMes baseInfo;

    /**
     * 单位等级信息
     */
    private List<UnitLevelMes> levelInfoData;

    /**
     * 能力信息
     */
    private List<Ability> abilityInfo;

    /**
     * 操做类型 1是保存草稿 2是发布版本
     */
    private Integer optType;

    /**
     * 新上传的单位
     */
    private NewUnitImgDTO newUploadImg;

    public UnitMes getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(UnitMes baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<UnitLevelMes> getLevelInfoData() {
        return levelInfoData;
    }

    public void setLevelInfoData(List<UnitLevelMes> levelInfoData) {
        this.levelInfoData = levelInfoData;
    }

    public List<Ability> getAbilityInfo() {
        return abilityInfo;
    }

    public void setAbilityInfo(List<Ability> abilityInfo) {
        this.abilityInfo = abilityInfo;
    }

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }

    public NewUnitImgDTO getNewUploadImg() {
        return newUploadImg;
    }

    public void setNewUploadImg(NewUnitImgDTO newUploadImg) {
        this.newUploadImg = newUploadImg;
    }

    @Override
    public String toString() {
        return "ReqSaveUnitMesDTO{" +
                "baseInfo=" + baseInfo +
                ", levelInfoData=" + levelInfoData +
                ", abilityInfo=" + abilityInfo +
                '}';
    }
}
