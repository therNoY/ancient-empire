package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.entity.UserTemplate;

import java.util.List;

/**
 * 保存模板信息请求DTO
 * @version 1.0
 * @author mihao
 * @date 2021\1\6 0006 22:44
 */
public class ReqSaveUserTemplateDTO extends UserTemplate {

    /**
     * 模板关联的单位
     */
    private List<Integer> relationUnitList;

    /**
     * 操做类型 1是保存草稿 2是发布版本
     */
    private Integer optType;

    public List<Integer> getRelationUnitList() {
        return relationUnitList;
    }

    public void setRelationUnitList(List<Integer> relationUnitList) {
        this.relationUnitList = relationUnitList;
    }

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }
}
