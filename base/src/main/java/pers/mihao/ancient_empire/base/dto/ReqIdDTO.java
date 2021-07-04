package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mihao
 * @Date 2021/6/2 20:15
 */
public class ReqIdDTO extends ApiRequestDTO {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
