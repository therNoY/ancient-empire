package pers.mihao.ancient_empire.core.manger.strategy.start;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.LifeChangeDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

/**
 * 单位开始后的各个能力的表现
 *
 * @author mihao
 */
public class StartStrategy extends AbstractStrategy<StartStrategy> {


    public static StartStrategy instance = null;

    public static StartStrategy getInstance() {
        if (instance == null) {
            instance = new StartStrategy();
        }
        return instance;
    }

    Logger log = LoggerFactory.getLogger(StartStrategy.class);


    /**
     * 获取回合开始时 不同能力的处理结果
     * @param regionInfo
     * @param unit
     * @param needRestoreLife
     * @param unitStatusInfoDTO
     * @param record
     * @return
     */
    public LifeChangeDTO getStartNewRoundLifeChange(RegionInfo regionInfo, Unit unit, UserRecord record) {
        int regionRestore, needRestoreLife, lastLife;
        lastLife = unit.getLife();
        // 单位等级信息
        UnitLevelMes levelMes  = unitLevelMesService.getUnitLevelMes(unit.getTypeId(), unit.getLevel());
        needRestoreLife = levelMes.getMaxLife() - lastLife;
        LifeChangeDTO lifeChangeDTO = null;
        if (needRestoreLife > 0) {
            if ((regionRestore = regionInfo.getRestore()) > 0) {
                if (colorIsCamp(record, regionInfo.getColor())
                    || (!RegionEnum.TOWN.type().equals(regionInfo.getType()) && !RegionEnum.CASTLE.type().equals(regionInfo.getType()))){
                    log.info("根据建筑物回血 需要回血：{} 地形可以回血：{}", needRestoreLife, regionRestore);
                    lifeChangeDTO = new LifeChangeDTO();
                    lifeChangeDTO.setRow(unit.getRow());
                    lifeChangeDTO.setColumn(unit.getColumn());
                    if (needRestoreLife < regionRestore) {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, needRestoreLife));
                    } else {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, regionRestore));
                    }
                }
            } else {
                List<Ability> abilities = abilityService.getUnitAbilityList(unit.getTypeId());
                int count = 0;
                for (StartStrategy startStrategy : getAbilityStrategy(abilities)) {
                    int recoverLife = startStrategy.recoverLife(regionInfo);
                    count += recoverLife;
                }
                if (count > 0) {
                    lifeChangeDTO = new LifeChangeDTO();
                    lifeChangeDTO.setRow(unit.getRow());
                    lifeChangeDTO.setColumn(unit.getColumn());
                    if (needRestoreLife < count) {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, needRestoreLife));
                    } else {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, count));
                    }
                }
            }
        }
        return lifeChangeDTO;
    }

    protected int recoverLife(RegionMes regionMes) {
        return 0;
    }
}
