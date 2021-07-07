package pers.mihao.ancient_empire.core.manger.command;

import com.alibaba.fastjson.JSONObject;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.util.GameCoreUtil;
import pers.mihao.ancient_empire.core.util.GameCoreUtil.Globalization;

/**
 * 发送给前端处理的任务
 *
 * @Author mihao
 * @Date 2020/9/17 16:26
 */
public class GameCommand extends AbstractCommand {

    /**
     * 游戏命令类型枚举
     */
    private GameCommendEnum gameCommend;

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
    private Region aimRegion;

    /**
     * 单位的Index
     */
    private Integer unitIndex;

    /**
     * 延迟
     */
    private Integer delay;

    /**
     * 非通用信息
     */
    private JSONObject extMes;

    public GameCommendEnum getGameCommend() {
        return gameCommend;
    }

    public void setGameCommend(GameCommendEnum gameCommend) {
        this.gameCommend = gameCommend;
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

    public Region getAimRegion() {
        return aimRegion;
    }

    public void setAimRegion(Region aimRegion) {
        this.aimRegion = aimRegion;
    }

    public Integer getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(Integer unitIndex) {
        this.unitIndex = unitIndex;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public JSONObject getExtMes() {
        return extMes;
    }

    public void setExtMes(JSONObject extMes) {
        this.extMes = extMes;
    }

    @Override
    public Command beforeSend(User user) {
        this.setMessagePrefix(this, user);
        return this;
    }

    private void setMessagePrefix(Command command, User user) {
        GameCommand gameCommand = (GameCommand) command;
        LanguageEnum lang = LoginUserHolder.getLanguage();
        if (gameCommand.getGameCommend().equals(GameCommendEnum.SHOW_GAME_NEWS)) {
            String oldMes = getGlobalization(gameCommand.getExtMes().get(ExtMes.MESSAGE), lang);
            if (LoginUserHolder.getLoginUser() != null) {
                gameCommand.getExtMes()
                    .put(ExtMes.SEND_MESSAGE,
                        AppUtil.getMessagePrefix(LoginUserHolder.getLoginUser().getUsername()) + oldMes);
            } else {
                gameCommand.getExtMes().put(ExtMes.SEND_MESSAGE, AppUtil.getSystemMessagePrefix() + oldMes);
            }
        } else if (gameCommand.getGameCommend().equals(GameCommendEnum.SHOW_SYSTEM_NEWS)) {
            String message = getGlobalization(gameCommand.getExtMes().get(ExtMes.MESSAGE), lang);
            gameCommand.getExtMes().put(ExtMes.SEND_MESSAGE, message);
        }
    }

    private String getGlobalization(Object mes, LanguageEnum languageEnum){
        if (mes instanceof GameCoreUtil.Globalization) {
            GameCoreUtil.Globalization globalizationMes = (Globalization) mes;
            String message = GameCoreUtil.getMessageByLang(globalizationMes, languageEnum);
            return message;
        }
        return mes.toString();
    }

    @Override
    public String toString() {
        return "GameCommand{" +
            ", gameCommendEnum=" + gameCommend +
            ", aimSite=" + aimSite +
            ", aimUnit=" + aimUnit +
            ", aimRegion=" + aimRegion +
            ", unitIndex=" + unitIndex +
            ", extMes=" + extMes +
            '}';
    }
}
