package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.entity.UserMap;

/**
 * @Author mihao
 * @Date 2021/5/10 21:41
 */
public class ReqSaveMap extends UserMap {

    /**
     * 操做类型 VersionConst
     */
    private Integer optType;

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }
}
