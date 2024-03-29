package pers.mihao.ancient_empire.core.robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.robot.BuyUnitDTO;
import pers.mihao.ancient_empire.core.dto.robot.CastleRegion;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;
import pers.mihao.ancient_empire.core.robot.handler.AbstractRobotHandler;

/**
 * 机器人处理任务
 *
 * @Author mihao
 * @Date 2020/9/9 20:34
 */
public abstract class AbstractRobot extends RobotCommonHandler implements Runnable {

    /**
     * 物理攻击
     */
    protected static final String PHYSICAL = "1";
    /**
     * 魔法攻击类型
     */
    protected static final String MAGIC = "2";

    protected static Logger log = LoggerFactory.getLogger(AbstractRobot.class);

    /**
     * 机器人解析行动的类
     */
    private static Map<RobotActiveEnum, Class> robotHandlerMap = new HashMap<>(16);

    public AbstractRobot(GameContext gameContext) {
        setGameContext(gameContext);
    }

    /**
     * 注册机器人事件处理
     */
    public static void registerRobotActiveHandler() {
        String packName = AbstractRobotHandler.class.getPackage().getName();
        String className = "RobotHandler";
        String handlerName, classPathName;
        for (RobotActiveEnum gameEventEnum : RobotActiveEnum.values()) {
            handlerName = StringUtil.underscoreToHump(gameEventEnum.toString(), true);
            classPathName = packName + CommonConstant.POINT + handlerName + className;
            try {
                Class clazz = AbstractRobotHandler.class.getClassLoader().loadClass(classPathName);
                robotHandlerMap.put(gameEventEnum, clazz);
                log.info("机器人事件：{}处理器 注册成功：{}", gameEventEnum);
            } catch (Exception e) {
                log.error("机器人事件：{}处理器 注册失败：{}");
            }
        }
    }

    @Override
    public void run() {
        log.info("================机器人开始行动===============");
        try {
            // 1.找所有的可移动单位
            CanMoveUnit canMoveUnit = findAllCanMoveUnit();
            log.info("找到可以行动的所有单位:{}", canMoveUnit);

            // 2.先移动半血以上的单位
            activeUnits(canMoveUnit.moreThanHalf);

            BuyUnitDTO buyUnit;
            // 3.购买单位移动
            while ((buyUnit = buyNewUnit(record())) != null) {
                log.info("选择购买的单位是{}", buyUnit.getUnitInfo());
                doActive(buyUnit.getUnitInfo());
            }

            activeUnits(canMoveUnit.lessThanHalf);
            log.info("================机器人行动结束===============");
        } catch (Exception e) {
            log.error("机器人行动出错", e);
        } finally {
            try {
                handleRobotEvent(GameEventEnum.ROUND_END);
            } catch (Exception e) {
                log.error("机器人结束回合出错", e);
            }
        }
    }

    private void activeUnits(List<UnitInfo> units) {
        while (units.size() > 0) {
            UnitInfo unit = chooseUnit(units);
            log.info("选择较为满意的行动单位：{}", unit);
            // 移除单位
            for (int i = 0; i < units.size(); i++) {
                if (unit.getId().equals(units.get(i).getId())) {
                    units.remove(i);
                    break;
                }
            }
            doActive(unit);
        }
    }

    /**
     * 选择是否购买单位
     *
     * @param record
     * @return
     */
    protected BuyUnitDTO buyNewUnit(UserRecord record) {
        BuyUnitDTO buyUnitDTO = new BuyUnitDTO();
        UnitInfo buyUnit;
        log.info("准备选择购买的单位");
        Army army = currArmy();
        boolean hasLoad = AppUtil.hasLoad(army);
        // 1.获取目前能买的单位
        List<UnitInfo> canBuyUnitMes;
        java.util.stream.Stream<UnitInfo> canBuyUnitMesStream = unitMesService.getCanBuyUnit(record.getTemplateId())
            .stream().map(unitMes -> unitMesService.getUnitInfo(unitMes.getId(), 1))
            .filter(unitInfo -> (unitInfo.getUnitMes().getPrice() <= army.getMoney() &&
                unitInfo.getUnitMes().getPopulation() <= record.getMaxPop() - army.getPop()));

        if (hasLoad) {
            canBuyUnitMes = canBuyUnitMesStream
                .filter(unitInfo -> !unitInfo.getAbilities().contains(AbilityEnum.CASTLE_GET.ability()))
                .collect(Collectors.toList());
        } else {
            canBuyUnitMes = canBuyUnitMesStream.collect(Collectors.toList());
        }

        if (canBuyUnitMes.size() == 0) {
            log.info("最便宜的都买不起 直接结束");
            return null;
        }

        // 2.判断是否还有自己的castle
        List<CastleRegion> castleList = getCastleRegions(record);
        if (castleList.size() == 0) {
            log.info("没有自己的城堡直接结束");
            return null;
        }

        // 3.选择召唤城堡
        CastleRegion selectCastle = null;
        long maxScore = Integer.MIN_VALUE, score;
        for (CastleRegion castleRegion : castleList) {
            score = getCastleScore(castleRegion);
            if (score > maxScore) {
                selectCastle = castleRegion;
            }
        }
        log.info("从{}个城堡中 选出 {}", castleList.size(), selectCastle);
        buyUnitDTO.setCastleRegion(selectCastle);
        changeCurrPoint(selectCastle.getSite());
        changeCurrRegion(selectCastle.getSite());

        // 4.选择召唤的单位
        NeedUnitType needUnitType = getNeedAbility();
        buyUnit = getMastNeedUnit(canBuyUnitMes, needUnitType);
        buyUnitDTO.setUnitInfo(buyUnit);
        if (buyUnit == null) {
            return null;
        }
        buyUnit.setRow(selectCastle.getSite().getRow());
        buyUnit.setColumn(selectCastle.getSite().getColumn());
        handleRobotEvent(GameEventEnum.CLICK_BUY_ACTION, buyUnit.getUnitMes().getId());
        return buyUnitDTO;
    }

    /**
     * 单位行动
     *
     * @param unit
     */
    private void doActive(UnitInfo unit) {
        log.info("机器人发送点击命令事件:{}", unit);
        handleRobotEvent(GameEventEnum.CLICK_ACTIVE_UNIT, unit);
        if (unit.getAbilities().contains(AbilityEnum.CASTLE_GET.ability())) {
            handleRobotEvent(GameEventEnum.CLICK_MOVE_ACTION);
        }
        List<Site> sites = gameContext.getWillMoveArea();
        // 可以结束的点
        List<Site> canStandBySite = sites.stream()
            .filter(site -> !isHaveFriend(record(), site.getRow(), site.getColumn()))
            .collect(Collectors.toList());
        List<Site> canEffectSite = getUnitAttachArea(canStandBySite);

        // 2.没有可以进行的行动 在选择其他行动
        ActionIntention intention = getUnitActionIntention(unit, canEffectSite);
        log.info("{} 的行动意向是：{}", unit, intention);
        actionUnit(intention);
    }

    /**
     * 获取单位的所有的可以行动的可能
     *
     * @param unit          单位
     * @param canEffectSite 单位当前回合可以作用的点
     * @return
     */
    private ActionIntention getUnitActionIntention(UnitInfo unit, List<Site> canEffectSite) {
        UnitAble unitAble = getUnitAble();
        // 1.获取单位所有可以进行的行动
        List<ActionIntention> actionList = new ArrayList<>();
        // 修复城堡
        if (unitAble.repairer) {
            List<RegionInfo> repairer = getAllCanRepairRegion();
            for (RegionInfo regionInfo : repairer) {
                actionList.add(new ActionIntention(RobotActiveEnum.REPAIR, regionInfo));
            }
        }
        // 占领城镇
        if (unitAble.villageGet) {
            List<RegionInfo> repairer = getAllCanOccupyVillage();
            for (RegionInfo regionInfo : repairer) {
                actionList.add(new ActionIntention(RobotActiveEnum.OCCUPIED, regionInfo));
            }
        }
        // 占领城堡
        if (unitAble.castleGet) {
            List<RegionInfo> repairer = getAllCanOccupyCastle();
            for (RegionInfo regionInfo : repairer) {
                actionList.add(new ActionIntention(RobotActiveEnum.OCCUPIED, regionInfo));
            }
        }
        // 召唤坟墓
        if (unitAble.hasSummoner && record().getTombList() != null) {
            for (Site tomb : record().getTombList()) {
                actionList.add(new ActionIntention(RobotActiveEnum.SUMMON, tomb));
            }
        }
        // 治疗受伤的友军
        List<UnitInfo> friendUnit;
        if (unitAble.hasHealer && (friendUnit = getAllFriendUnits()) != null) {
            // 治疗友军
            for (UnitInfo unitInfo : friendUnit) {
                int maxLife = unitLevelMesService.getUnitLevelMes(unitInfo.getTypeId(), unitInfo.getLevel())
                    .getMaxLife();
                if (unitInfo.getLife() < maxLife) {
                    log.info("{} 可以进行 治疗 操作目标单位:{} 血量：{}", unit.getType(), unitInfo.getType(), unitInfo.getLife());
                    actionList.add(new ActionIntention(RobotActiveEnum.HEAL, unitInfo, unitInfo));
                }
            }
        }
        // 攻击可以攻击的单位
        List<UnitInfo> allEnemyUnits = getAllEnemyUnits();
        for (UnitInfo unitInfo : allEnemyUnits) {
            actionList.add(new ActionIntention(RobotActiveEnum.ATTACH, unitInfo, unitInfo));
        }
        // 防守有危险的地区
        List<RegionInfo> threatenedRegion;
        if ((threatenedRegion = getThreatenedRegion()) != null) {
            for (RegionInfo regionInfo : threatenedRegion) {
                actionList.add(new ActionIntention(RobotActiveEnum.DEFENSIVE, regionInfo));
            }
        }

        // 2 先从监听器中选择
        ActionIntention listerChoose = gameContext.chooseAction(unit, actionList);
        if (listerChoose != null) {
            return listerChoose;
        }

        // 2.3 给每个操作进行打分 选出一个最好的操作
        if (actionList.size() > 0) {
            log.info("有许多可选的操作size = {} 首先从当前回合就能行动的点 选出一个最好的操作", actionList.size());
            List<ActionIntention> canEffectSiteNow = new ArrayList<>();
            for (ActionIntention actionIntention : actionList) {
                for (Site site : canEffectSite) {
                    if (siteIsCanEffectAction(actionIntention, site)) {
                        canEffectSiteNow.add(actionIntention);
                    }
                }
            }
            // 获取当前回合就能进行的行动
            if (canEffectSiteNow.size() > 0) {
                return getPreferredAction(canEffectSiteNow);
            }
            log.error("没有选出可以直接行动的点, 开始选择可以预计行动的点");
            return getPreferredAction(actionList);
        }
        // 如果没有任何可选操做 就原地待命
        return new ActionIntention(RobotActiveEnum.END, currSite());
    }

    /**
     * 判断这一点是否是当前可以作用到行动的点
     *
     * @param intention
     * @param site
     */
    private boolean siteIsCanEffectAction(ActionIntention intention, Site site) {
        boolean isCanEffect = false;
        switch (intention.getActive()) {
            case ATTACH:
            case HEAL:
            case SUMMON:
                List<Site> effectArea = AttachStrategy.getInstance()
                    .getAttachArea(currUnit().getUnitMes(), site, gameMap());
                isCanEffect = effectArea != null && effectArea.contains(intention.getSite());
                break;
            case OCCUPIED:
            case REPAIR:
            case RECOVER:
            case DEFENSIVE:
            case END:
                isCanEffect = AppUtil.siteEquals(intention.getSite(), site);
                break;
        }
        return isCanEffect;
    }

    /**
     * 从众多可选操做中选择一个最佳操做
     *
     * @param actionList
     */
    protected ActionIntention getPreferredAction(List<ActionIntention> actionList) {
        ActionIntention niceAction = actionList.get(0);
        int score, maxActionScore = Integer.MIN_VALUE;
        for (ActionIntention actionIntention : actionList) {
            score = getActionScore(actionIntention);
            if (score > maxActionScore) {
                niceAction = actionIntention;
                maxActionScore = score;
            }
        }
        return niceAction;
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
     * @param intention
     * @param intention
     */
    private void actionUnit(ActionIntention intention) {
        Class clazz = robotHandlerMap.get(intention.getActive());
        if (clazz != null) {
            AbstractRobotHandler robotHandler = null;
            try {
                robotHandler = (AbstractRobotHandler) clazz.newInstance();
                robotHandler.setGameContext(gameContext);
                robotHandler.setRobot(this);
                robotHandler.setThreatenedRegion(getThreatenedRegion());
            } catch (Exception e) {
                log.error("", e);
            }
            log.info("机器人准备处理：{}逻辑 当前上下文：{}", intention, robotHandler.printlnContext());
            robotHandler.handler(intention);
        }
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
            if (unit.getLife() > 50) {
                moreThanHalf.add(getUnitInfoByUnit(unit));
            } else {
                lessThanHalf.add(getUnitInfoByUnit(unit));
            }
        }
        canMoveUnit.moreThanHalf = moreThanHalf;
        canMoveUnit.lessThanHalf = lessThanHalf;
        return canMoveUnit;
    }

    @Override
    public void setGameContext(GameContext gameContext) {
        super.gameContext = gameContext;
    }

    /**
     * 获取每个action的得分
     *
     * @param actionIntention
     * @return
     */
    protected abstract int getActionScore(ActionIntention actionIntention);


    /**
     * 从待选的单位中选择一个单位
     *
     * @param moreThanHalf
     * @return
     */
    protected abstract UnitInfo chooseUnit(List<UnitInfo> moreThanHalf);

    /**
     * 从可以购买的单位中选择最需要单位
     *
     * @param canBuyUnitMes
     * @return
     */
    protected abstract UnitInfo getMastNeedUnit(List<UnitInfo> canBuyUnitMes, NeedUnitType needUnitType);

    /**
     * 根据城堡位置获取城堡召唤的分数 分数越大越会选择这个召唤
     *
     * @param castleRegion 城堡位置
     * @return 分数
     */
    protected abstract Long getCastleScore(CastleRegion castleRegion);

    /**
     * 选出当前需要的能力
     *
     * @param userRecord
     * @return
     */
    protected abstract NeedUnitType getNeedAbility();

    /**
     * 从不在攻击目标的单位中选择单位 默认血量最少 可以实现其他策略
     *
     * @param otherCanAttachUnit
     * @return
     */
    public Unit getOtherCanAttachUnit(List<Unit> otherCanAttachUnit) {
        return otherCanAttachUnit.stream().sorted(Comparator.comparingInt(Unit::getLife)).findFirst().get();
    }

    public Tomb getOtherCanSummonTomb(List<Tomb> otherCanSummonTomb) {
        return otherCanSummonTomb.get(0);
    }
}
