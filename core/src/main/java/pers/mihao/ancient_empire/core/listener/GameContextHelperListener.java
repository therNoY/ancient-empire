package pers.mihao.ancient_empire.core.listener;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyStatusInfoDTO;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.UserTemplateHelper;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

/**
 * 上下文处理监听 每一个上下文都会有一个这个类 监听命令变化可以保证gameContext中的 userRecord 数据一致性 handle中不需要处理userRecord
 *
 * @Author mihao
 * @Date 2021/4/2 11:30
 */
public class GameContextHelperListener extends AbstractGameRunListener {

    Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onGameStart() {
        log.info("{}开始", gameContext.getGameId());

        if (record().getCurrArmyIndex() == null) {
            record().setCurrArmyIndex(1);
        }

        if (StringUtil.isBlack(gameContext.getUserRecord().getCurrPlayer())) {
            log.info("开局是robot");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            robotManger.startRobot(gameContext);
        }
    }

    @Override
    public void onUnitDead(Integer armyIndex, UnitInfo unitInfo) {
        log.info("{}单位死亡", unitInfo);
    }

    @Override
    public void onUnitDone(UnitInfo unitInfo) {
        log.info("{}单位结束", unitInfo);
    }


    @Override
    public boolean onGameCommandAdd(GameCommand gameCommand) {
        JSONObject extMes = gameCommand.getExtMes();
        switch (gameCommand.getGameCommend()) {
            case CHANGE_CURR_POINT:
                record().setCurrPoint(gameCommand.getAimSite());
                break;
            case ADD_TOMB:
                Unit unitInfo = (Unit) gameCommand.getAimSite();
                Tomb newTomb = new Tomb(unitInfo, unitInfo.getTypeId());
                record().getTombList().add(newTomb);
                break;
            case ADD_UNIT:
                List<Unit> units = record().getArmyList().get(extMes.getInteger(ExtMes.ARMY_INDEX)).getUnits();
                units.add((Unit) extMes.get(ExtMes.UNIT));
                break;

            case REMOVE_UNIT:
                // 移除死亡的单位
                ArmyUnitIndexDTO indexDTO = (ArmyUnitIndexDTO) extMes.get(ExtMes.ARMY_UNIT_INDEX);
                Army army = record().getArmyList().get(indexDTO.getArmyIndex());
                army.getUnits().remove(indexDTO.getUnitIndex().intValue());
                break;
            case REMOVE_TOMB:
                // 移除坟墓
                record().getTombList().remove(new Site(gameCommand.getAimSite()));
                break;

            case CHANGE_UNIT_STATUS:
                // 更新单位状态变化
                if (gameCommand.getExtMes().get(ExtMes.UNIT_STATUS) instanceof List) {
                    // 这种是回合结束 回合开始 不更新单位的位置
                    List unitStatusList = (List) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
                    for (Object object : unitStatusList) {
                        updateUnitInfo((UnitStatusInfoDTO) object);
                    }
                } else {
                    // 单个需要更新当前单位的位置
                    UnitStatusInfoDTO unitStatus = (UnitStatusInfoDTO) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
                    updateUnitInfo(unitStatus);
                }
                break;
            case CHANGE_CURR_REGION:
                record().setCurrRegion((RegionInfo) extMes.get(ExtMes.REGION_INFO));
                break;
            case CHANGE_CURR_UNIT:
                record().setCurrUnit((UnitInfo) extMes.get(ExtMes.UNIT_INFO));
                break;
            case CHANGE_ARMY_INFO:
                ArmyStatusInfoDTO armyStatusInfoDTO = (ArmyStatusInfoDTO) extMes.get(ExtMes.ARMY_INFO);
                if (armyStatusInfoDTO.getMoney() != null) {
                    currArmy().setMoney(armyStatusInfoDTO.getMoney());
                }
                if (armyStatusInfoDTO.getPop() != null) {
                    currArmy().setPop(armyStatusInfoDTO.getPop());
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void updateUnitInfo(UnitStatusInfoDTO unitStatus) {
        if (isEffectiveCommand(unitStatus)) {
            Unit unit = getUnitByIndex(unitStatus);
            updateUnitInfo(unit, unitStatus);
            if (Boolean.TRUE.equals(unitStatus.getUpdateCurr())) {
                unit.setRow(currSite().getRow());
                unit.setColumn(currSite().getColumn());
            }
        }
    }

    private void updateUnitInfo(Unit unit, UnitStatusInfoDTO unitStatusInfoDTO) {
        BeanUtil.copyValueByGetSet(unitStatusInfoDTO, unit);
        if (StringUtil.isNotBlack(unitStatusInfoDTO.getStatus())
            && !StateEnum.NORMAL.type().equals(unitStatusInfoDTO.getStatus())) {
            // 有非正常状态的要设置回合数
            unit.setStatusPresenceNum(gameContext.getStatusRoundNum());
        }

    }

    /**
     * 判断是否有效的命令
     *
     * @param unitStatusInfoDTO
     * @return
     */
    private boolean isEffectiveCommand(UnitStatusInfoDTO unitStatusInfoDTO) {
        return unitStatusInfoDTO != null &&
            (StringUtil.isNotBlack(unitStatusInfoDTO.getStatus())
                || (unitStatusInfoDTO.getExperience()) != null
                || (unitStatusInfoDTO.getDone()) != null
                || (unitStatusInfoDTO.getLevel()) != null
                || (unitStatusInfoDTO.getLife()) != null
            );
    }

    @Override
    public void onUnitStatusChange(List<UnitStatusInfoDTO> unitStatusInfoDTOS, Stream stream) {
        for (UnitStatusInfoDTO unitStatus : unitStatusInfoDTOS) {
            handlerLevelUp(unitStatus, stream);
        }
    }

    private boolean handlerLevelUp(UnitStatusInfoDTO unitStatusChangeInfo, Stream stream) {
        if (!isEffectiveCommand(unitStatusChangeInfo)) {
            return false;
        }
        Unit unit = getUnitByIndex(unitStatusChangeInfo);
        // 判断是否升级
        Integer levelExp = gameContext.getLevelExp(unit.getLevel());
        if (unitStatusChangeInfo.getExperience() != null && unitStatusChangeInfo.getExperience() >= levelExp) {
            // 可以升级
            int maxLevel = gameContext.getUserTemplate().getUnitMaxLevel();
            if (maxLevel == unit.getLevel()) {
                // 设置最大经验值 不升级
                log.info("单位：{}达到最大等级不升级", unit);
                unitStatusChangeInfo.setExperience(levelExp);
            } else {
                log.info("单位：{}准备升级", unit);
                // 是否晋升
                boolean isPromotion = false;
                if (isCanPromotion(unit, gameContext.getUserTemplate())) {
                    // 开启晋升模式 同一个兵种的最大晋级数量1
                    Army army = record().getArmyList().get(unitStatusChangeInfo.getArmyIndex());
                    // 所有晋升过的总数量和 该类型兵种晋升的总数量
                    int count = 0, typeCount = 0;
                    for (Unit u : army.getUnits()) {
                        if (Boolean.TRUE.equals(u.getPromotion())) {
                            if (u.getTypeId().equals(unit.getTypeId())) {
                                typeCount++;
                            }
                            count++;
                        }
                    }
                    // 最大晋升数量 和类型晋升数量都小于模板最大数量才能晋级
                    if (count < gameContext.getUserTemplate().getPromotionMaxNum() &&
                        typeCount < gameContext.getTypePromotionCount()) {
                        if (gameContext.getRandomPromotionChance()) {
                            log.info("准备晋升");
                            List<UnitTransfer> unitTransfers = unitTransferService
                                .getTransferByUnitId(unit.getTypeId());
                            if (CollectionUtil.isNotEmpty(unitTransfers)) {
                                UnitTransfer unitTransfer = unitTransfers.get(0);
                                // 这里才真正的晋升
                                isPromotion = true;
                                unit.setLevel(unit.getLevel() + 1);
                                unit.setExperience(unit.getExperience() - levelExp);
                                Unit newUnit = UnitFactory.copyUnit(unit);
                                newUnit.setTypeId(unitTransfer.getTransferUnitId());
                                removeUnit(unitStatusChangeInfo);
                                addNewUnit(newUnit, unitStatusChangeInfo.getArmyIndex());
                            }
                        }
                    }
                }
                if (!isPromotion) {
                    unitStatusChangeInfo.setLevel(unit.getLevel() + 1);
                    unitStatusChangeInfo.setExperience(unitStatusChangeInfo.getExperience() - levelExp);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ExtMes.LEVEL_UP_INFO, gameContext.getLevelUpImg());
                    jsonObject.put(ExtMes.SITE, unit);
                    stream.toGameCommand().addOrderCommand(GameCommendEnum.SHOW_LEVEL_UP, jsonObject, 99);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否达到晋升的条件
     *
     * @param unit
     * @param template
     * @return
     */
    private boolean isCanPromotion(Unit unit, UserTemplate template) {
        return (UserTemplateHelper.COMMON.equals(template.getPromotionMode())
            || UserTemplateHelper.RANDOM.equals(template.getPromotionMode()))
            && unit.getLevel() + 1 > template.getPromotionLevel();
    }
}
