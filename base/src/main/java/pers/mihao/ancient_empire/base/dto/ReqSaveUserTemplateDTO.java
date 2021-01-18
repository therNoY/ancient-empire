package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.entity.UserTemplate;

import java.util.List;

/**
 * 保存模板信息请求DTO
 * @version 1.0
 * @auther mihao
 * @date 2021\1\6 0006 22:44
 */
public class ReqSaveUserTemplateDTO extends UserTemplate {

    /**
     * 模板关联的单位
     */
    private List<Integer> relationUnitList;

    /**
     * 是否是取消保存
     */
    private Boolean cancelSave;

    public Boolean getCancelSave() {
        return cancelSave;
    }

    public void setCancelSave(Boolean cancelSave) {
        this.cancelSave = cancelSave;
    }

    public List<Integer> getRelationUnitList() {
        return relationUnitList;
    }

    public void setRelationUnitList(List<Integer> relationUnitList) {
        this.relationUnitList = relationUnitList;
    }
}
