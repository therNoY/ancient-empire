package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * @Author mh32736
 * @Date 2020/9/17 16:26
 */
public class GameCommand extends AbstractCommand{

    /**
     * 发送消息类型枚举
     */
    private SendTypeEnum sendTypeEnum;

    /**
     * 游戏命令类型枚举
     */
    private GameCommendEnum gameCommendEnum;

    /**
     * 目标点的位置
     */
    private Site aimSite;

    /**
     * 目标点的位置
     */
    private Unit aimUnit;

    /**
     * 目标地形
     */
    private BaseSquare aimRegion;



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

    public Site getAimSite() {
        return aimSite;
    }

    public void setAimSite(Site aimSite) {
        this.aimSite = aimSite;
    }

    public Unit getAimUnit() {
        return aimUnit;
    }

    public void setAimUnit(Unit aimUnit) {
        this.aimUnit = aimUnit;
    }

    public BaseSquare getAimRegion() {
        return aimRegion;
    }

    public void setAimRegion(BaseSquare aimRegion) {
        this.aimRegion = aimRegion;
    }

    @Override
    public String toString() {
        return "GameCommand{" +
                "sendTypeEnum=" + sendTypeEnum +
                ", gameCommendEnum=" + gameCommendEnum +
                ", pointSite=" + aimSite +
                '}';
    }
}
