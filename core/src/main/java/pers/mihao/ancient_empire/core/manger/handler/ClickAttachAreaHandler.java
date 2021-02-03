package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 点击攻击区域
 *
 * @version 1.0
 * @author mihao
 * @date 2020\10\24 0024 22:59
 */
public class ClickAttachAreaHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        if (currUnit().getAbilities().contains(AbilityEnum.DESTROYER.ability())) {
            Region region = getRegionBySite(gameEvent.getInitiateSite());
            if (region.getType().equals(RegionEnum.TOWN.type()) && !colorIsCamp(region.getColor())) {
                // 展示攻击指针
                commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ATTACH_POINT, gameEvent.getInitiateSite());
                gameContext.setStatusMachine(StatusMachineEnum.WILL_ATTACH_REGION);
            }
        }
    }


}
