package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Site;

/**
 * 单位修复占领传过来的
 */
public class ReqRepairOcpDto extends ReqSecondMoveDto{
    private Integer index; // 单位在军队中的index
    private Site region; // 地形信息

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Site getRegion() {
        return region;
    }

    public void setRegion(Site region) {
        this.region = region;
    }


}
