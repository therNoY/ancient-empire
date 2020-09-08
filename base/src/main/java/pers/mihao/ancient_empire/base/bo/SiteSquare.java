package pers.mihao.ancient_empire.base.bo;

public class SiteSquare {

    BaseSquare square;
    Site site;

    public SiteSquare(BaseSquare square, Site site) {
        this.square = square;
        this.site = site;
    }

    public BaseSquare getSquare() {
        return square;
    }

    public void setSquare(BaseSquare square) {
        this.square = square;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
