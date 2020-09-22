package pers.mihao.ancient_empire.base.vo;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserRecord;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\21 0021 21:52
 */
public class GameVO extends UserRecord {

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
