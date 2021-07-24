package pers.mihao.ancient_empire.core.listener.chapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserSetting;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.service.UserSettingService;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.LifeChangeDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * 章节监听抽象处理类
 *
 * @Author mihao
 * @Date 2021/4/7 9:55
 */
public abstract class AbstractChapterListener extends AbstractGameRunListener {

    static final Logger log = LoggerFactory.getLogger(AbstractChapterListener.class);

    static final int LOAD = 10;

    static final int FRIEND_CAMP = 1;

    static final int ENEMY_CAMP = 2;

    private static final String FRIEND_BLUE = ColorEnum.BLUE.type();

    private static final String FRIEND_GREEN = ColorEnum.GREEN.type();

    private static final String ENEMY_RED = ColorEnum.RED.type();

    private static final String ENEMY_BLACK = ColorEnum.BLACK.type();

    protected static UserService userService;
    protected static UserSettingService userSettingService;

    static {
        userService = ApplicationContextHolder.getBean(UserService.class);
        userSettingService = ApplicationContextHolder.getBean(UserSettingService.class);
    }

    protected int stage = 0;

    protected int maxStage;

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
        awaitTime(500);
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
        } else if (stage == maxStage && camp == ENEMY_CAMP) {
            // 剧情结束 并且没有敌方军队 并且没有敌方城堡
            if (!hasEnemyUnit() && getEnemyCastle().size() == 0) {
                gameWin();
            }
        }
    }

    @Override
    public void onRoundEnd(Army army) {
        if (stage < maxStage && triggerConditions[stage].getTriggerType().equals(TriggerTypeEnum.END_ROUND)) {
            if (triggerConditions[stage].getRound() == null) {
                triggerPlot();
            } else if (record().getCurrentRound().equals(triggerConditions[stage].getRound())) {
                triggerPlot();
            }
        }
    }

    /**
     * 启动天堂之怒
     *
     * @param unit
     * @param attachNum
     */
    protected void showHeavenFury(Unit unit, int attachNum) {
        // 展示动画
        showHeavenFuryAnim(unit);

        // 展示血量变化
        List<LifeChangeDTO> leftChangeList = new ArrayList<>();
        leftChangeList.add(new LifeChangeDTO(AppUtil.getArrayByInt(-1, attachNum), unit));
        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, leftChangeList);

        // 修改单位血量
        int lastLife = Math.max(unit.getLife() - attachNum, 0);
        UnitStatusInfoDTO unitStatusInfo = new UnitStatusInfoDTO(getArmyUnitIndexByUnitId(unit.getId()));
        unitStatusInfo.setLife(lastLife);
        unitStatusInfo.setUpdateCurr(false);
        changeUnitStatus(unitStatusInfo);

        // 判断是否死亡
        if (lastLife == 0) {
            addUnitDeadCommend(getUnitInfoByUnit(unit), getArmyUnitIndexByUnitId(unit.getId()));
        }
    }

    /**
     * 是否有敌军
     *
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
        updateUserSetting();
        onChapterGameWin();
        gameContext.setStatusMachine(preStatus);
    }

    private void updateUserSetting() {
        User user = GameContext.getUser();
        if (user != null) {
            UserSetting setting = userSettingService.getUserSettingById(user.getId());
            if (getCurrentChapter() == setting.getMaxChapter()) {
                setting.setMaxChapter(setting.getMaxChapter() + 1);
                userSettingService.updateByUserId(setting);
            }
        }
    }

    /**
     * 当前章节
     *
     * @return
     */
    protected int getCurrentChapter() {
        return Integer.valueOf(this.getClass().getName().charAt(7) + "");
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
            } else if (triggerSite.getTriggerType().equals(TriggerTypeEnum.IN_AREA) && isInArea(unitInfo,
                triggerSite)) {
                // 单位停留区域触发
                triggerPlot();
            } else if (triggerSite.getTriggerType().equals(TriggerTypeEnum.OUR_AREA) && !isInArea(unitInfo,
                triggerSite)) {
                triggerPlot();
            }
        }
    }

    private boolean isInArea(UnitInfo unitInfo, TriggerCondition triggerSite) {
        int minRow = Math.min(triggerSite.getMaxSite().getRow(), triggerSite.getMinSite().getRow());
        int maxRow = Math.max(triggerSite.getMaxSite().getRow(), triggerSite.getMinSite().getRow());
        int minColumn = Math.min(triggerSite.getMaxSite().getColumn(), triggerSite.getMinSite().getColumn());
        int maxColumn = Math.min(triggerSite.getMaxSite().getColumn(), triggerSite.getMinSite().getColumn());
        return unitInfo.getRow() <= maxRow && unitInfo.getRow() >= minRow && unitInfo.getColumn() <= maxColumn
            && unitInfo.getColumn() >= minColumn;
    }

    /**
     * 触发剧情
     */
    private void triggerPlot() {
        awaitTime(300);
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

    void addUnitSendNow(Integer unitId, Site site, Integer armyIndex) {
        Unit wolf1 = addNewUnit(unitId, site, armyIndex);
        changeUnitPoint(getArmyUnitIndexByUnitId(wolf1.getId()));
        sendCommandNow();
        waitExecutionOk(1500);
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

    public Integer getInitMoney(Army army) {
        return 0;
    }


    protected List<Site> getEnemyCastle() {
        List<Site> list = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if ((region.getColor().equals(ENEMY_RED) || region.getColor().equals(ENEMY_BLACK))
                && region.getType().equals(RegionEnum.CASTLE.type())) {
                list.add(getSiteByRegionIndex(i));
            }
        }
        return list;
    }

    protected List<Site> getFriendCastle() {
        List<Site> list = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if (region.getColor().equals(FRIEND_BLUE) || region.getColor().equals(FRIEND_GREEN)) {
                list.add(getSiteByRegionIndex(i));
            }
        }
        return list;
    }

}
