package pers.mihao.ancient_empire.core.listener.chapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

/**
 * 章节监听抽象处理类
 * @Author mh32736
 * @Date 2021/4/7 9:55
 */
public abstract class AbstractChapterListener extends AbstractGameRunListener {

    static final Logger log = LoggerFactory.getLogger(AbstractChapterListener.class);

    protected static UserService userService;
    static {
        userService = ApplicationContextHolder.getBean(UserService.class);
    }

    private int stage = 0;

    private int maxStage;

    TriggerCondition[] triggerConditions;

    private static final String TRIGGER_STAGE = "triggerStage";

    public AbstractChapterListener() {
        initConditions();
        if (triggerConditions != null) {
            maxStage = triggerConditions.length;
        }
    }

    /**
     * 初始化剧情出发条件
     */
    protected abstract void initConditions();

    @Override
    public final void onGameStart() {
        StatusMachineEnum preStatus = gameContext.getStatusMachine();
        gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
        onChapterGameStart();
        gameContext.setStatusMachine(preStatus);
    }

    @Override
    public final void onUnitDead(Integer armyIndex, UnitInfo unitInfo) {
        Integer camp = record().getArmyList().get(armyIndex).getCamp();

        if (unitInfo.getUnitMes().getId() == LOAD && camp == FRIEND_CAMP) {
            gameOver();
            return;
        }

        if (stage < maxStage && camp == ENEMY_CAMP) {
            if (triggerConditions[stage].getTriggerType().equals(TriggerTypeEnum.ANY_DEAD)) {
                // 任意单位死亡触发
                triggerPlot();
            } else if (triggerConditions[stage].getTriggerType().equals(TriggerTypeEnum.ALL_DEAD)
                && !hasEnemyUnit()) {
                // 全部单位死亡触发
                triggerPlot();
            }
        } else  if (stage == maxStage && camp == ENEMY_CAMP) {
            // 剧情结束 并且没有敌方军队 并且没有敌方城堡
            if (!hasEnemyUnit() && getEnemyCastle().size() == 0) {
                gameWin();
            }
        }
    }

    /**
     * 是否有敌军
     * @return
     */
    private boolean hasEnemyUnit() {
        for (Army army : record().getArmyList()) {
            if (army.getCamp().equals(ENEMY_CAMP)) {
                if (army.getUnits().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected final void onGameWin() {
        StatusMachineEnum preStatus = gameContext.getStatusMachine();
        gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
        onChapterGameWin();
        gameContext.setStatusMachine(preStatus);
    }

    @Override
    protected final void onGameOver() {
        onChapterGameOver();
    }

    /**
     * 游戏失败
     */
    protected abstract void onChapterGameOver();


    @Override
    public final void onUnitDone(UnitInfo unitInfo) {
        if ((getUnitArmy(unitInfo).getCamp()) == FRIEND_CAMP && stage < maxStage) {
            TriggerCondition triggerSite = triggerConditions[stage];
            if (triggerSite.getTriggerType().equals(TriggerTypeEnum.ANY_DONE)) {
                // 任意单位接触触发
                triggerPlot();
            } else if (triggerSite.getTriggerType().equals(TriggerTypeEnum.IN_AREA)
                && unitInfo.getRow() <= triggerSite.getMaxSite().getRow() && unitInfo.getColumn() <= triggerSite.getMaxSite().getColumn()
                && unitInfo.getRow() >= triggerSite.getMinSite().getRow() && unitInfo.getColumn() >= triggerSite.getMinSite().getColumn()) {
                // 单位停留区域触发
                triggerPlot();
            }
        }
    }

    /**
     * 触发剧情
     */
    private void triggerPlot() {
        stage++;
        try {
            StatusMachineEnum preStatus = gameContext.getStatusMachine();
            gameContext.setStatusMachine(StatusMachineEnum.DIALOG);
            Method method = this.getClass().getDeclaredMethod(TRIGGER_STAGE + stage);
            method.invoke(this);
            gameContext.setStatusMachine(preStatus);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
        }
    }

    void changeUnitPoint(ArmyUnitIndexDTO attachIndex) {
        Unit unit = getUnitByIndex(attachIndex);
        changeCurrPoint(unit);
        changeCurrUnit(unit);
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


    public Integer getMaxPop() {
        return 40;
    }

    public Integer getInitMoney() {
        return 0;
    }


}
