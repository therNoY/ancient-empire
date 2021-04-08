package pers.mihao.ancient_empire.core.listener.chapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;

/**
 * 章节监听抽象处理类
 * @Author mh32736
 * @Date 2021/4/7 9:55
 */
public abstract class AbstractChapterListener extends AbstractGameRunListener {

    static final Logger log = LoggerFactory.getLogger(AbstractChapterListener.class);

    int stage = 1;

    int maxStage;

    Site[] triggerSites;

    private static final String TRIGGER_STAGE = "triggerStage";

    @Override
    public final void onGameStart() {
        StatusMachineEnum preStatus = gameContext.getStatusMachine();
        gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
        onChapterGameStart();
        gameContext.setStatusMachine(preStatus);
    }

    @Override
    public final void onUnitDead(UnitInfo unitInfo) {
        Integer camp = getUnitArmy(unitInfo).getCamp();
        if (unitInfo.getUnitMes().getId() == LOAD && camp == FRIEND_CAMP) {
            gameOver();
            return;
        }

        if (stage == maxStage && camp == FRIEND_CAMP && getEnemyCastle().size() == 0) {
            gameWin();
        }
    }


    @Override
    protected final void onGameWin() {
        StatusMachineEnum preStatus = gameContext.getStatusMachine();
        gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
        onChapterGameWin();
        gameContext.setStatusMachine(preStatus);
    }


    @Override
    public final void onUnitDone(UnitInfo unitInfo) {
        if ((getUnitArmy(unitInfo).getCamp()) == FRIEND_CAMP && triggerSites != null && triggerSites.length > 0) {
            Site triggerSite = triggerSites[stage];
            if (unitInfo.getRow() < triggerSite.getRow() && unitInfo.getColumn() < triggerSite.getColumn()) {
                try {
                    StatusMachineEnum preStatus = gameContext.getStatusMachine();
                    gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
                    Method method = this.getClass().getMethod(TRIGGER_STAGE + stage);
                    method.invoke(this);
                    gameContext.setStatusMachine(preStatus);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.error("", e);
                }
                stage++;
            }
        }
    }


    @Override
    public final void onClickTip() {
        notifySelf();
    }

    /**
     * 章节游戏开始
     */
    public abstract void onChapterGameStart();

    /**
     * 章节游戏胜利
     */
    public abstract void onChapterGameWin();


}
