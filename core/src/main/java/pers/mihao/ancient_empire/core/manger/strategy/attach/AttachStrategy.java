package pers.mihao.ancient_empire.core.manger.strategy.attach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.*;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.UserRecordUtil;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.handel.attach.AttachHandle;
import pers.mihao.ancient_empire.core.handel.defense.DefenseHandle;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 攻击的策略
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\6 0006 19:36
 */
public class AttachStrategy extends AbstractStrategy<AttachStrategy> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static AttachStrategy instance = null;

    public static AttachStrategy getInstance() {
        if (instance == null) {
            instance = new AttachStrategy();
        }
        return instance;
    }

    /**
     * 获取单位的 攻击范围 保证不出地图
     *
     * @param unitMes  当前单位的信息
     * @param currSite 当前点
     * @param gameMap  游戏地图
     * @return
     */
    public List<Site> getAttachArea(UnitMes unitMes, Site currSite, GameMap gameMap) {
        Integer maxRange = unitMes.getMaxAttachRange();
        List<Site> maxAttach = new ArrayList<>();
        int minI = Math.max(currSite.getRow() - maxRange, 1);
        int maxI = Math.min(currSite.getRow() + maxRange + 1, gameMap.getRow() + 1);
        int minJ = Math.max(currSite.getColumn() - maxRange, 1);
        int maxJ = Math.min(currSite.getColumn() + maxRange + 1, gameMap.getRow() + 1);
        for (int i = minI; i < maxI; i++) {
            for (int j = minJ; j < maxJ; j++) {
                if (getSiteLength(i, j, currSite.getRow(), currSite.getColumn()) <= maxRange && getSiteLength(i, j, currSite.getRow(), currSite.getColumn()) > 0) {
                    maxAttach.add(new Site(i, j));
                }
            }
        }
        Integer minRange = unitMes.getMinAttachRange();
        List<Site> notAttach = null;
        if (minRange != 1) {
            // 获取无法攻击到的点
            minRange = minRange - 1;
            notAttach = new ArrayList<>();
            minI = Math.max(currSite.getRow() - minRange, 0);
            maxI = Math.min(currSite.getRow() + minRange + 1, gameMap.getRow());
            minJ = Math.max(currSite.getColumn() - minRange, 0);
            maxJ = Math.min(currSite.getColumn() + minRange + 1, gameMap.getRow());
            for (int i = minI; i < maxI; i++) {
                for (int j = minJ; j < maxJ; j++) {
                    if (getSiteLength(i, j, currSite.getRow(), currSite.getColumn()) <= minRange) {
                        notAttach.add(new Site(i, j));
                    }
                }
            }

        }

        int row = gameMap.getRow();
        int column = gameMap.getColumn();
        // 过滤符合条件的点
        List<Site> finalNotAttach = notAttach;
        return maxAttach.stream().filter(position -> {
            // 在地图范围内
            if (position.getRow() <= row && position.getColumn() <= column) {
                // 不在不可攻击范围内
                if (finalNotAttach == null || !finalNotAttach.contains(position)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }


    /**
     * 获取单位的攻击力
     * @param gameContext
     * @param attachUnitInfo
     * @param beAttachUnitInfo
     * @return
     */
    public AttributesPower getUnitAttachInfo(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo){
        AttributesPower attributesPower = new AttributesPower();
        getAttachPower(gameContext, attachUnitInfo, beAttachUnitInfo, attributesPower);
        log.info("{} 攻击 {} >>> 基础攻击力是{}", attachUnitInfo.getType(), beAttachUnitInfo.getType(), attributesPower.getNum());
        getAbilityStrategy(attachUnitInfo.getAbilities()).forEach(attachStrategy -> {
            attachStrategy.getAttachPower(gameContext, attachUnitInfo, beAttachUnitInfo, attributesPower);
        });
        return attributesPower;
    }


    /**
     * 获取攻击力
     *
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    public AttributesPower getAttachPower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo, AttributesPower attributesPower) {
        if (attributesPower.getNum() == null) {
            int attach = gameContext.getAttachNum(attachUnitInfo.getLevelMes());
            attributesPower.setNum(attach);
        }
        return attributesPower;
    }


}
