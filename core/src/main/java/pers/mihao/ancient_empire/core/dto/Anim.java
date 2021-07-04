package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Site;

/**
 * @Author mihao
 * @Date 2021/4/25 21:57
 */
public class Anim extends Site {

    private String animImg;

    public Anim(String animImg){
        this.animImg = animImg;
    }

    public String getAnimImg() {
        return animImg;
    }

    public void setAnimImg(String animImg) {
        this.animImg = animImg;
    }
}
