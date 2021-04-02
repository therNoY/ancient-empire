package pers.mihao.ancient_empire.core.listener;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyStatusInfoDTO;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.UserTemplateHelper;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.handler.AbstractGameEventHandler.Stream;

/**
 * 上下文处理监听 每一个上下文都会有一个这个类
 * 监听命令变化可以保证gameContext中的 userRecord 数据一致性 handle中不需要处理userRecord
 * @Author mh32736
 * @Date 2021/4/2 11:30
 */
public class GameContextHelperListener extends AbstractGameRunListener {

    Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onGameStart() {

    }

    @Override
    public void onUnitDead() {

    }

    @Override
    public void onUnitDone() {

    }

    @Override
    public boolean onGameCommandAdd(GameCommand gameCommand) {
        JSONObject extMes = gameCommand.getExtMes();
        switch (gameCommand.getGameCommendEnum()) {
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
            case CHANGE_CURR_BG_COLOR:
                gameContext.setBgColor(extMes.getString(ExtMes.BG_COLOR));
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
        if (isInvalidCommand(unitStatus)) {
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
     * 判断是否无效的命令
     * @param unitStatusInfoDTO
     * @return
     */
    private boolean isInvalidCommand(UnitStatusInfoDTO unitStatusInfoDTO) {
        return unitStatusInfoDTO != null &&
            (StringUtil.isNotBlack(unitStatusInfoDTO.getStatus())
                || (unitStatusInfoDTO.getExperience()) != null
                || (unitStatusInfoDTO.getDone()) != null
                || (unitStatusInfoDTO.getLevel()) != null
                || (unitStatusInfoDTO.getLife()) != null
            );
    }

    @Override
    public void onUnitLevelUp(GameCommand gameCommand, Stream stream) {
        if (gameCommand.getExtMes().get(ExtMes.UNIT_STATUS) instanceof List) {
            List unitStatusList = (List) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
            UnitStatusInfoDTO unitStatus;
            for (Object obj : unitStatusList) {
                unitStatus = (UnitStatusInfoDTO) obj;
                handlerLevelUp(unitStatus, stream);
            }
        } else {
            UnitStatusInfoDTO unitStatus = (UnitStatusInfoDTO) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
            handlerLevelUp(unitStatus, stream);
        }
    }

    private void handlerLevelUp(UnitStatusInfoDTO unitStatus, Stream stream) {
        if (!isInvalidCommand(unitStatus)) {
            return;
        }
        // 更新单位的状态
        Unit unit = getUnitByIndex(unitStatus);
        // 判断是否升级
        Integer levelExp = gameContext.getLevelExp(unit.getLevel());
        if (unitStatus.getExperience() != null && unitStatus.getExperience() >= levelExp) {
            // 可以升级
            int maxLevel = gameContext.getUserTemplate().getUnitMaxLevel();
            if (maxLevel == unit.getLevel()) {
                // 设置最大经验值 不升级
                unitStatus.setExperience(levelExp);
            } else {
                // 升级
                boolean isPromotion = false;
                if ((UserTemplateHelper.COMMON.equals(gameContext.getUserTemplate().getPromotionMode())
                    || UserTemplateHelper.RANDOM.equals(gameContext.getUserTemplate().getPromotionMode()))
                    && unit.getLevel() + 1 > gameContext.getUserTemplate().getPromotionLevel()) {
                    // 开启晋升模式 同一个兵种的最大晋级数量1
                    Army army = record().getArmyList().get(unitStatus.getArmyIndex());
                    int count = 0;
                    int typeCount = 0;
                    for (Unit u : army.getUnits()) {
                        if (Boolean.TRUE.equals(u.getPromotion())) {
                            if (u.getTypeId().equals(unit.getTypeId())) {
                                typeCount++;
                            } else {
                                count++;
                            }
                        }
                    }
                    // 最大晋升数量 和类型晋升数量都小于模板最大数量才能晋级
                    if (count < gameContext.getUserTemplate().getPromotionMaxNum() &&
                        typeCount < gameContext.getTypePromotionCount()) {
                        if (gameContext.getRandomPromotionChance()) {
                            log.info("准备晋升");
                            QueryWrapper<UnitTransfer> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("unitId", unit.getTypeId())
                                .eq("order", 1);
                            UnitTransfer unitTransfer = unitTransferService.getOne(queryWrapper);
                            if (unitTransfer != null) {
                                isPromotion = true;
                                unit.setLevel(unit.getLevel() + 1);
                                unit.setExperience(unit.getExperience() - levelExp);
                                Unit newUnit = new Unit();
                                BeanUtil.copyValueByGetSet(unit, newUnit);
                                newUnit.setTypeId(unitTransfer.getTransferUnitId());
                                stream.toGameCommand().removeUnit(unitStatus)
                                    .toGameCommand().addUnit(newUnit, unitStatus.getArmyIndex());
                            }

                        }

                    }
                }
                if (!isPromotion) {
                    unitStatus.setLevel(unit.getLevel() + 1);
                    unitStatus.setExperience(unitStatus.getExperience() - levelExp);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ExtMes.LEVEL_UP_INFO, gameContext.getLevelUpImg());
                    jsonObject.put(ExtMes.SITE, unit);
                    stream.toGameCommand().addOrderCommand(GameCommendEnum.SHOW_LEVEL_UP, jsonObject);
                }
            }
        }
    }
}
