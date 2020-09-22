package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * @Author mh32736
 * @Date 2020/9/17 16:26
 */
public class GameCommand extends AbstractCommand{

    private SendTypeEnum sendTypeEnum;

    private GameCommendEnum gameCommendEnum;

    private Site pointSite;

    public SendTypeEnum getSendTypeEnum() {
        return sendTypeEnum;
    }

    public void setSendTypeEnum(SendTypeEnum sendTypeEnum) {
        this.sendTypeEnum = sendTypeEnum;
    }

    public GameCommendEnum getGameCommendEnum() {
        return gameCommendEnum;
    }

    public void setGameCommendEnum(GameCommendEnum gameCommendEnum) {
        this.gameCommendEnum = gameCommendEnum;
    }

    public Site getPointSite() {
        return pointSite;
    }

    public void setPointSite(Site pointSite) {
        this.pointSite = pointSite;
    }

    @Override
    public String toString() {
        return "GameCommand{" +
                "sendTypeEnum=" + sendTypeEnum +
                ", gameCommendEnum=" + gameCommendEnum +
                ", pointSite=" + pointSite +
                '}';
    }
}
