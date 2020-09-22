package pers.mihao.ancient_empire.base.vo;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserMap;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\20 0020 21:01
 */
public class UserMapVo extends UserMap {

    /**
     * 城堡的头部的位置
     */
    List<Site> castleTitles;


    public List<Site> getCastleTitles() {
        return castleTitles;
    }

    public void setCastleTitles(List<Site> castleTitles) {
        this.castleTitles = castleTitles;
    }
}
