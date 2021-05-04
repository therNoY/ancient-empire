package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mh32736
 * @Date 2021/5/2 17:50
 */
public class CreateUnitImgDTO extends ApiRequestDTO {

    private List<String> imgList;

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
