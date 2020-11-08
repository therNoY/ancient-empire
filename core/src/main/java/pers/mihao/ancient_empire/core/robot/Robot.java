package pers.mihao.ancient_empire.core.robot;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 机器人处理任务
 * @Author mh32736
 * @Date 2020/9/9 20:34
 */
public abstract class Robot implements Runnable{

    protected final GameContext gameContext;



    public Robot(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        log.info("================机器人开始行动===============");
        // 1.找所有的可移动单位
        CanMoveUnit canMoveUnit = findAllCanMoveUnit();
        log.info("找到可以行动的所有单位");

        // 2.先移动半血以上的单位
        activeUnits(canMoveUnit.moreThanHalf);

        Unit buyUnit;
        while ((buyUnit = buyNewUnit(gameContext.getUserRecord())) != null) {
            log.info("选择购买的单位是{}", buyUnit);
            // 购买单位命令
            sendGameEvent();
            doActive(buyUnit);
        }

        activeUnits(canMoveUnit.lessThanHalf);

        log.info("================机器人行动结束===============");
    }



    private void activeUnits(List<Unit> units) {
        while (units.size() > 0) {
            Unit unit = chooseUnit(units);
            log.info("选择较为满意的行动单位：{}", unit);
            removeChooseUnit(units, unit);
            doActive(unit);
        }
    }

    /**
     * 处理机器人事件
     */
    protected void sendGameEvent(){
        GameEvent gameEvent = new GameEvent();
        gameCoreManger.handelTask(gameEvent);
    }

    /**
     * 单位行动
     * @param userRecord
     * @param unit
     */
    protected void doActive(Unit unit) {
        ActionIntention intention = chooseUnitAction(gameContext, unit);
        log.info("{} 的行动意向是：{}", unit, intention);
        moveUnit(unit, intention);
    }

    /**
     * 根据单位的行动意愿进行移动
     * @param unit
     * @param intention
     */
    private void moveUnit(Unit unit, ActionIntention intention) {
    }

    /**
     * 选择当前单位的行动意向
     * @param context
     * @param unit
     * @return
     */
    protected abstract ActionIntention chooseUnitAction(GameContext context, Unit unit);

    /**
     * 从待选的单位中选择一个单位
     * @param moreThanHalf
     * @return
     */
    protected abstract Unit chooseUnit(List<Unit> moreThanHalf);

    /**
     * 选择是否购买单位
     * @param userRecord
     * @return
     */
    protected abstract Unit buyNewUnit(UserRecord userRecord);


    /**
     * 找到所有代移动的单位
     * @return
     */
    private CanMoveUnit findAllCanMoveUnit() {
        UserRecord record = gameContext.getUserRecord();
        CanMoveUnit canMoveUnit = new CanMoveUnit();
        List<UnitInfo> moreThanHalf = new ArrayList<>();
        List<UnitInfo> lessThanHalf = new ArrayList<>();
        for (Unit unit : record.getArmyList().get(record.getCurrArmyIndex()).getUnits()) {
            if (AppUtil.getUnitLeft(unit) > 50) {
                moreThanHalf.add(unitMesService.getUnitInfo());
            }else {
                lessThanHalf.add(unit);
            }
        }
        return canMoveUnit;
    }

    private void removeChooseUnit(List<Unit> units, Unit unit){
        for (int i = 0; i < units.size(); i++) {
            if (unit.getId().equals(units.get(i).getId())) {
                units.remove(i);
                return;
            }
        }
    }

    static class CanMoveUnit{
        List<UnitInfo> moreThanHalf;
        List<UnitInfo> lessThanHalf;

        @Override
        public String toString() {
            return "CanMoveUnit{" +
                "moreThanHalf=" + moreThanHalf +
                ", lessThanHalf=" + lessThanHalf +
                '}';
        }
    }

    protected static GameCoreManger gameCoreManger;
    protected static UnitMesService unitMesService;
    static {
        gameCoreManger = ApplicationContextHolder.getBean(GameCoreManger.class);
        unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);
    }

    /**
     * 通过unit 获取单位的info
     *
     * @param indexDTO
     * @return
     */
    protected UnitInfo getUnitInfoByUnit(Unit unit) {
        UnitInfo unitInfo = unitMesService.getUnitInfo(unit.getTypeId().toString(), unit.getLevel());
        BeanUtil.copyValueFromParent(unit, unitInfo);
        return unitInfo;
    }

}
