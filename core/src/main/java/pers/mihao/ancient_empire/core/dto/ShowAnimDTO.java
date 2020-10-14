package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Site;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\11 0011 10:44
 */
public class ShowAnimDTO extends Site {

    List<String> animList;

    public ShowAnimDTO(Site site, List<String> animList) {
        super(site);
        this.animList = animList;
    }

    public ShowAnimDTO(Integer row, Integer column, List<String> animList) {
        super(row, column);
        this.animList = animList;
    }

    public List<String> getAnimList() {
        return animList;
    }

    public void setAnimList(List<String> animList) {
        this.animList = animList;
    }
}
