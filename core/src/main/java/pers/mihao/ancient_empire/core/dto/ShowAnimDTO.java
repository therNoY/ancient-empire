package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Site;

import java.util.List;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\10\11 0011 10:44
 */
public class ShowAnimDTO extends Site {

    /**
     * 动画
     */
    private List<Anim> animList;

    /**
     * 动画过度的帧数
     * @param site
     * @param animList
     */
    private Integer frame;

    public ShowAnimDTO() {
    }

    public ShowAnimDTO(Site site, List<Anim> animList) {
        super(site);
        this.animList = animList;
    }


    public List<Anim> getAnimList() {
        return animList;
    }

    public void setAnimList(List<Anim> animList) {
        this.animList = animList;
    }

    public Integer getFrame() {
        return frame;
    }

    public void setFrame(Integer frame) {
        this.frame = frame;
    }
}
