package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mh32736
 * @Date 2021/4/17 19:25
 */
public class ReqRecordContinueDTO extends ApiRequestDTO {

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "ReqRecordContinueDTO{" +
            "uuid='" + uuid + '\'' +
            '}';
    }
}
