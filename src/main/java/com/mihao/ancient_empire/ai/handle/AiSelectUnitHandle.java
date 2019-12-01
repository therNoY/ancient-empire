package com.mihao.ancient_empire.ai.handle;

import com.mihao.ancient_empire.ai.StatusComparator;
import com.mihao.ancient_empire.ai.dto.*;
import com.mihao.ancient_empire.common.util.EnumUtil;
import com.mihao.ancient_empire.common.util.IntegerUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.constant.UnitEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 单例的 改类维持在robot manger 中
 */
public class AiSelectUnitHandle extends AiActiveHandle {

    protected StatusComparator statusComparator = new StatusComparator();
    Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Integer physical = 1;
    private static final Integer magic = 2;

    public AiSelectUnitHandle(String id) {
        log.error("创建一次AiSelectUnitHandle id={}", id);
    }

    @Override
    public ActiveResult getActiveResult(UserRecord record) {
        return selectUnit(record);
    }

    /**
     * 准备选择单位
     *
     * @return
     */
    private ActiveResult selectUnit(UserRecord record) {
        String recordId = record.getUuid();
        int currentArmyIndex = AppUtil.getCurrentArmyIndex(record);
        Army army = record.getArmyList().get(currentArmyIndex);
        log.info("{} 色方准备 选择单位", army.getColor());
        SelectUnitResult selectUnitResult = new SelectUnitResult(recordId, currentArmyIndex, army.getColor());
        // 首选有攻击光环的
        Unit attacker = getFirstAvailableUnitWithAbility(AppUtil.getCurrentArmy(record), AbilityEnum.ATTACKER);
        if (attacker != null) {
            selectUnitResult.setSite(AppUtil.getPosition(attacker));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(attacker, army));
            log.info("选择标准：攻击光环");
            return selectUnitResult;
        }

        // 然后选择净化光环的
        Unit purify = getFirstAvailableUnitWithAbility(army, AbilityEnum.PURIFY);
        if (purify != null) {
            selectUnitResult.setSite(AppUtil.getPosition(purify));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(purify, army));
            log.info("选择标准 ： 净化光环");
            return selectUnitResult;
        }

        // 然后选择治愈光环的
        Unit healer = getFirstAvailableUnitWithAbility(AppUtil.getCurrentArmy(record), AbilityEnum.HEALER);
        if (healer != null) {
            selectUnitResult.setSite(AppUtil.getPosition(healer));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(healer, army));
            log.info("选择标准 ：治愈能力");
            return selectUnitResult;
        }

        // 然后选择位置是城堡的
        Unit onCastleUnit = getFirstOnCastle(record, army);
        if (onCastleUnit != null) {
            selectUnitResult.setSite(AppUtil.getPosition(onCastleUnit));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(onCastleUnit, army));
            log.info("选择标准 ：城堡之上");
            return selectUnitResult;
        }

        // 返回剩余的血量多的
        Unit unit = getLifeHealth(army);
        if (unit != null) {
            selectUnitResult.setSite(AppUtil.getPosition(unit));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(unit, army));
            log.info("选择标准 ：血量较多");
            return selectUnitResult;
        }

        // 没有可选的的单位  查看是否可以进行购买
        BuyUnitResult result = getBuyUnitResult(record);
        if (result != null) {
            return result;
        }

        log.info("{} 色方准备结束回合", army.getColor());
        return new EndTurnResult(recordId);
    }


    /**
     * 获取第一个拥有什么能力的人
     *
     * @param army
     * @param ability
     * @return
     */
    private Unit getFirstAvailableUnitWithAbility(Army army, AbilityEnum ability) {
        for (Unit unit : army.getUnits()) {
            if (!unit.isDone() && !unit.isDead() && abilityService.getUnitAbilityListByType(unit.getType()).contains(ability.type())) {
                return unit;
            }
        }
        return null;
    }


    /**
     * 获取位置是城堡的
     *
     * @param army
     * @return
     */
    private Unit getFirstOnCastle(UserRecord record, Army army) {
        for (Unit unit : army.getUnits()) {
            if (!unit.isDone() && !unit.isDead()) {
                BaseSquare square = AppUtil.getRegionByPosition(record, unit);
                if (square.getType().equals(RegionEnum.CASTLE.type())) {
                    return unit;
                }
            }
        }
        return null;
    }

    /**
     * 获取血量最高的那个
     *
     * @param army
     * @return
     */
    private Unit getLifeHealth(Army army) {
        Optional<Unit> optionalUnit = army.getUnits().stream()
                .filter(unit -> !unit.isDone() && !unit.isDead())
                .sorted(Comparator.comparingInt(u -> -1 * AppUtil.getIntByIntegers(u.getLife())))
                .sorted(statusComparator)
                .findFirst();
        if (optionalUnit.isPresent()) {
            return optionalUnit.get();
        }
        return null;
    }

    /**
     * 获取购买单位的结果
     *
     * @return
     */
    private BuyUnitResult getBuyUnitResult(UserRecord record) {
        BuyUnitResult buyUnitResult = null;
        log.info("准备选择购买的单位");

        Army army = AppUtil.getCurrentArmy(record);
        UnitMes unitMes = unitMesService.getMaxCheapUnit();
        if (army.getMoney() < unitMes.getPrice()) {
            log.info("最便宜的都买不起 直接结束");
            return null;
        }

        // 判断是否还有自己的castle
        List<BaseSquare> baseSquares = record.getInitMap().getRegions();
        List<CastleRegion> castleList = new ArrayList<>();
        for (int i = 0; i < baseSquares.size(); i++) {
            BaseSquare square = baseSquares.get(i);
            if (square.getType().equals(RegionEnum.CASTLE.type()) && square.getColor().equals(record.getCurrColor())) {
                Site site = AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn());
                castleList.add(new CastleRegion(square, site));
            }
        }
        if (castleList.size() == 0) {
            log.info("没有自己的城堡直接结束");
            return null;
        }

        // 选择最佳城堡 看哪个城堡的周围的敌军最多
        Map<CastleRegion, Integer> map = getDistance(castleList, record);
        CastleRegion selectCastle = null; // 选择的城堡
        int minIndex = Integer.MAX_VALUE;
        for (Map.Entry<CastleRegion, Integer> entry : map.entrySet()) {
            if (entry.getValue() < minIndex) {
                selectCastle = entry.getKey();
            }
        }
        log.info("从众多城堡中 {} 选出 {} 召唤", castleList.size(), selectCastle);

        // 准备希望要召唤的单位
        UnitMes loadMes = unitMesService.getByType(UnitEnum.LORD.type());
        if (!AppUtil.hasLoad(army) && army.getMoney() >= loadMes.getPrice()
                && loadMes.getPopulation() + army.getPop() <= record.getMaxPop()) {
            log.info("主帅已死 资金足够 重新招募");
            buyUnitResult = new BuyUnitResult(record.getUuid(), selectCastle.getSite(), loadMes);
            return buyUnitResult;
        }

        // 获取现在需要的能力
        NeedUnitType needUnitType = getNeedAbility(record);
        // 获取可以买的单位列表 TODO 考虑过滤loader
        List<UnitMes> canBuyUnit = unitMesService.getEnableBuyUnit()
                .stream().filter(mes -> {
                    if ((mes.getPrice() > army.getMoney() || loadMes.getPopulation() + army.getPop() > record.getMaxPop())
                            || mes.getType().equals(UnitEnum.LORD.type())) {
                        // 过滤 超过金额和人口的不买， 领主不买，能卖的话早就买了
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());

        UnitMes buyUnit = null;

        if (needUnitType.isNeedAbility) {
            log.info("购买单位需要的能力 {}", needUnitType.abilityEnum);
            List<UnitMes> buyUnitList = canBuyUnit.stream().filter(mes -> {
                if (abilityService.getUnitAbilityListByType(mes.getType()).contains(new Ability(needUnitType.abilityEnum))) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());

            if (buyUnitList.size() > 0) {
                buyUnit = buyUnitList.get(IntegerUtil.getRandomIn(buyUnitList.size() - 1));
                buyUnitResult = new BuyUnitResult(record.getUuid(), selectCastle.getSite(), buyUnit);
                log.info("计算要购买的单位是" + buyUnit.getType());
                return buyUnitResult;
            }
        }
        log.info("购买单位需要的攻击类型 {}： 1 物理 2 魔法", needUnitType.attachType);
        List<UnitMes> buyUnitList = canBuyUnit.stream().filter(mes -> {
            if (needUnitType.attachType == physical) {
                if (mes.getAttackType().equals(physical)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (mes.getAttackType().equals(magic)) {
                    return false;
                } else {
                    return true;
                }
            }
        }).collect(Collectors.toList());

        if (buyUnitList.size() > 0) {
            buyUnit = buyUnitList.get(IntegerUtil.getRandomIn(buyUnitList.size() - 1));
        }
        if (buyUnit != null) {
            buyUnitResult = new BuyUnitResult(record.getUuid(), selectCastle.getSite(), buyUnit);
            log.info("计算要购买的单位是" + buyUnit.getType());
        }
        return buyUnitResult;
    }

    /**
     * 选出需要的能力
     *
     * @return
     */
    private NeedUnitType getNeedAbility(UserRecord record) {
        // 首先要有3个具有占领能力的单位

        List<Army> armies = record.getArmyList();
        int camp = record.getCurrCamp();

        int villageGetNum = 0;
        int purifyNum = 0;
        int summonerNum = 0;
        int shooterNum = 0;
        int unHealthNum = 0;
        int remoteDefenseNum = 0;

        int enemyNum = 0;
        int enemyPhyDefSum = 0;
        int enemyMagDefSum = 0;
        int averagePhyDef = 0; /*地方平均物理防御*/
        int averageMagDef = 0; /*平均魔法防御*/
        int airEnemyNum = 0;
        int shooterEnemyNum = 0;

        // 统计军队状况
        for (Army army : armies) {
            for (Unit unit : army.getUnits()) {
                if (army.getCamp() == camp) {
                    // 统计己方能力数量
                    List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
                    for (Ability ability : abilityList) {
                        AbilityEnum abilityEnum = EnumUtil.valueOf(AbilityEnum.class, ability.getType());
                        switch (abilityEnum) {
                            case VILLAGE_GET:
                                villageGetNum++;
                                break;
                            case PURIFY:
                                purifyNum++;
                                break;
                            case SUMMONER:
                                summonerNum++;
                                break;
                            case SHOOTER:
                                shooterNum++;
                                break;
                            case REMOTE_DEFENSE:

                        }

                    }

                    // 统计己方不健康数量
                    String statue = unit.getStatus();
                    if (statue != null && !statue.equals(StateEnum.EXCITED.type()) && !statue.equals(StateEnum.EXCITED.type())) {
                        unHealthNum++;
                    }
                } else {
                    // 统计敌方情概况
                    enemyNum++;
                    UnitLevelMes levelMes = unitMesService.getUnitInfo(unit.getType(), unit.getLevel()).getLevel();
                    enemyPhyDefSum = levelMes.getPhysicalDefense();
                    enemyMagDefSum = levelMes.getMagicDefense();
                    List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
                    if (abilityList.contains(AbilityEnum.FLY.ability())) {
                        airEnemyNum++;
                    } else if (abilityList.contains(AbilityEnum.SHOOTER.ability())) {
                        shooterEnemyNum++;
                    }
                }
            }
        }

        averagePhyDef = enemyPhyDefSum / enemyNum;
        averageMagDef = enemyMagDefSum / enemyNum;

        if (villageGetNum < 3) {
            return new NeedUnitType(true, AbilityEnum.VILLAGE_GET);
        } else if (airEnemyNum > shooterNum * 2) {
            return new NeedUnitType(true, AbilityEnum.SHOOTER);
        } else if (record.getTomb() != null && record.getTomb().size() > summonerNum * 3) {
            return new NeedUnitType(true, AbilityEnum.SUMMONER);
        } else if (unHealthNum > purifyNum * 3) {
            return new NeedUnitType(true, AbilityEnum.PURIFY);
        } else if (shooterEnemyNum > remoteDefenseNum * 4) {
            return new NeedUnitType(true, AbilityEnum.REMOTE_DEFENSE);
        }

        return new NeedUnitType(false, averagePhyDef > averageMagDef ? physical : magic);
    }


    /**
     * 获取城堡周围的地方实例
     *
     * @param castleList
     * @param record
     * @return
     */
    private Map<CastleRegion, Integer> getDistance(List<CastleRegion> castleList, UserRecord record) {
        Map<CastleRegion, Integer> map = new HashMap<>();
        int camp = record.getCurrCamp();
        record.getArmyList().stream().forEach(army -> {
            if (army.getCamp() != camp) {
                army.getUnits().stream().forEach(unit -> {
                    if (!unit.isDead()) {
                        for (CastleRegion castle : castleList) {
                            Site castleSite = castle.getSite();
                            Integer sum = map.get(castleSite);
                            if (sum == null) {
                                sum = 0;
                            }
                            map.put(castle, sum + AppUtil.getLength(castleSite, unit));
                        }
                    }
                });
            }
        });

        return map;
    }


    /**
     * 封装需要的单位能力
     */
    private class NeedUnitType {
        boolean isNeedAbility;
        AbilityEnum abilityEnum;
        int attachType;

        public NeedUnitType(boolean isNeedAbility, AbilityEnum abilityEnum) {
            this.isNeedAbility = isNeedAbility;
            this.abilityEnum = abilityEnum;
        }

        public NeedUnitType(boolean isNeedAbility, int attachType) {
            this.isNeedAbility = isNeedAbility;
            this.attachType = attachType;
        }

        public boolean isNeedAbility() {
            return isNeedAbility;
        }

        public void setNeedAbility(boolean needAbility) {
            isNeedAbility = needAbility;
        }

        public AbilityEnum getAbilityEnum() {
            return abilityEnum;
        }

        public void setAbilityEnum(AbilityEnum abilityEnum) {
            this.abilityEnum = abilityEnum;
        }

        public int getAttachType() {
            return attachType;
        }

        public void setAttachType(int attachType) {
            this.attachType = attachType;
        }
    }
}
