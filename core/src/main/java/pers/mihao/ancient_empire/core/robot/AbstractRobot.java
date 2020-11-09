package pers.mihao.ancient_empire.core.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.ai.CastleRegion;
import pers.mihao.ancient_empire.core.dto.robot.BuyUnitDTO;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 机器人处理任务
 *
 * @Author mh32736
 * @Date 2020/9/9 20:34
 */
public abstract class AbstractRobot extends GameAnalysis implements Runnable {

    /**
     * 物理攻击
     */
    protected static final String PHYSICAL = "1";
    /**
     * 魔法攻击类型
     */
    protected static final String MAGIC = "2";


    public AbstractRobot(GameContext gameContext) {
        setGameContext(gameContext);
    }

    @Override
    public void setGameContext(GameContext gameContext) {
        super.gameContext = gameContext;
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

        BuyUnitDTO buyUnit;
        while ((buyUnit = buyNewUnit(record())) != null) {
            log.info("选择购买的单位是{}", buyUnit.getUnitInfo());
            // 购买单位命令
            sendGameEvent();
            doActive(buyUnit.getUnitInfo());
        }

        activeUnits(canMoveUnit.lessThanHalf);

        log.info("================机器人行动结束===============");
    }


    private void activeUnits(List<UnitInfo> units) {
        while (units.size() > 0) {
            UnitInfo unit = chooseUnit(units);
            log.info("选择较为满意的行动单位：{}", unit);
            removeChooseUnit(units, unit);
            doActive(unit);
        }
    }

    /**
     * 选择是否购买单位
     *
     * @param userRecord
     * @return
     */
    protected BuyUnitDTO buyNewUnit(UserRecord record) {
        BuyUnitDTO buyUnitDTO = new BuyUnitDTO();
        UnitInfo buyUnit;
        log.info("准备选择购买的单位");
        Army army = currArmy();
        // 获取目前能买的单位
        List<UnitInfo> canBuyUnitMes = unitMesService.getCanBuyUnit(record.getTemplateId())
                .stream().map(unitMes -> unitMesService.getUnitInfo(unitMes.getId().toString(), 1))
                .filter(unitInfo -> (unitInfo.getUnitMes().getPrice() <= army.getMoney() &&
                        unitInfo.getUnitMes().getPopulation() <= record.getMaxPop() - army.getPop()))
                .collect(Collectors.toList());

        if (canBuyUnitMes.size() > 0) {
            log.info("最便宜的都买不起 直接结束");
            return buyUnitDTO;
        }

        // 判断是否还有自己的castle
        List<CastleRegion> castleList = getCastleRegions(record);

        if (castleList.size() == 0) {
            log.info("没有自己的城堡直接结束");
            return buyUnitDTO;
        }

        // 选择最佳城堡 看哪个城堡的周围的敌军最多
        Map<CastleRegion, Integer> map = getCastleScore(castleList);
        // 选择的城堡
        CastleRegion selectCastle = null;
        int minIndex = Integer.MAX_VALUE;
        for (Map.Entry<CastleRegion, Integer> entry : map.entrySet()) {
            if (entry.getValue() < minIndex) {
                selectCastle = entry.getKey();
            }
        }
        log.info("从{}个城堡中 选出 {}", castleList.size(), selectCastle);
        buyUnitDTO.setCastleRegion(selectCastle);
        NeedUnitType needUnitType = getNeedAbility(gameContext.getUserRecord());
        buyUnit = getBestUnit(canBuyUnitMes, needUnitType);
        buyUnitDTO.setUnitInfo(buyUnit);
        return buyUnitDTO;
    }


    /**
     * 单位行动
     *
     * @param userRecord
     * @param unit
     */
    private void doActive(Unit unit) {
        ActionIntention intention = chooseUnitAction(gameContext, unit);
        log.info("{} 的行动意向是：{}", unit, intention);
        moveUnit(unit, intention);
    }

    /**
     * 处理机器人事件
     */
    private void sendGameEvent() {
        GameEvent gameEvent = new GameEvent();
        gameCoreManger.handelTask(gameEvent);
    }

    /**
     * 获取自己的城堡
     *
     * @param record
     * @return
     */
    private List<CastleRegion> getCastleRegions(UserRecord record) {
        List<Region> regions = gameMap().getRegions();
        List<CastleRegion> castleList = new ArrayList<>();
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            if (region.getType().equals(RegionEnum.CASTLE.type()) && region.getColor().equals(record.getCurrColor())) {
                Site site = getSiteByRegionIndex(i);
                castleList.add(new CastleRegion(region, site));
            }
        }
        return castleList;
    }

    /**
     * 根据单位的行动意愿进行移动
     *
     * @param unit
     * @param intention
     */
    private void moveUnit(Unit unit, ActionIntention intention) {
    }


    /**
     * 找到所有代移动的单位
     *
     * @return
     */
    private CanMoveUnit findAllCanMoveUnit() {
        CanMoveUnit canMoveUnit = new CanMoveUnit();
        List<UnitInfo> moreThanHalf = new ArrayList<>();
        List<UnitInfo> lessThanHalf = new ArrayList<>();
        for (Unit unit : currArmy().getUnits()) {
            if (AppUtil.getUnitLife(unit) > 50) {
                moreThanHalf.add(getUnitInfoByUnit(unit));
            } else {
                lessThanHalf.add(getUnitInfoByUnit(unit));
            }
        }
        return canMoveUnit;
    }

    /**
     * 移除单位
     *
     * @param units
     * @param unit
     */
    private void removeChooseUnit(List<UnitInfo> units, UnitInfo unit) {
        for (int i = 0; i < units.size(); i++) {
            if (unit.getId().equals(units.get(i).getId())) {
                units.remove(i);
                return;
            }
        }
    }

    /**
     * 选择当前单位的行动意向
     *
     * @param moveArea
     * @param unitInfo
     * @return
     */
    protected abstract ActionIntention chooseUnitAction(List<Site> moveArea, UnitInfo unitInfo);

    /**
     * 从待选的单位中选择一个单位
     *
     * @param moreThanHalf
     * @return
     */
    protected abstract UnitInfo chooseUnit(List<UnitInfo> moreThanHalf);


    /**
     * 从单位中选择最需要单位
     *
     * @param canBuyUnitMes
     * @return
     */
    protected abstract UnitInfo getBestUnit(List<UnitInfo> canBuyUnitMes, NeedUnitType needUnitType);


    /**
     * 根据所有的城堡获取城堡的分数
     *
     * @param castleList 拥有的城堡
     * @return
     */
    protected abstract Map<CastleRegion, Integer> getCastleScore(List<CastleRegion> castleList);


    /**
     * 获取当前需要的能力
     *
     * @param userRecord
     * @return
     */
    protected abstract NeedUnitType getNeedAbility(UserRecord userRecord);


}
