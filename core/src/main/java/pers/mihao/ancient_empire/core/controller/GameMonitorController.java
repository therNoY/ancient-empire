package pers.mihao.ancient_empire.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;
import pers.mihao.ancient_empire.core.dto.MonitorDTO;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏监控的
 *
 * @version 1.0
 * @author mihao
 * @date 2021\1\30 0030 14:40
 */
@RestController
public class GameMonitorController {

    @Autowired
    GameCoreManger gameCoreManger;

    @RequestMapping("/api/monitor")
    public RespJson getGameDetailById(@RequestBody MonitorDTO monitorDTO) {
        GameContext gameContext = gameCoreManger.getGameSessionById(monitorDTO.getGameId());
        if (StringUtil.isBlack(monitorDTO.getGameId())) {
            gameContext = gameCoreManger.getOneGame();
        }

        CommonHandler commonHandler = new CommonHandler();
        commonHandler.setGameContext(gameContext);
        if (BaseConstant.YES.equals(monitorDTO.getCurrArmy())) {
            Army army = BeanUtil.deptClone(commonHandler.currArmy());
            if (StringUtil.isNotBlack(monitorDTO.getCurrUnitType())) {
                army.setUnits(army.getUnits().stream().filter(unit -> unit.getType().equals(monitorDTO.getCurrUnitType()))
                        .collect(Collectors.toList()));
            }
            return RespUtil.successResJson(army);
        } else if (BaseConstant.YES.equals(monitorDTO.getCurrUnit())) {
            return RespUtil.successResJson(commonHandler.currUnit());
        } else if (BaseConstant.YES.equals(monitorDTO.getAll())) {
            return RespUtil.successResJson(gameContext);
        } else if (BaseConstant.YES.equals(monitorDTO.getNoMap())) {
            UserRecord record = BeanUtil.deptClone(gameContext.getUserRecord());
            record.setGameMap(null);
            if (StringUtil.isNotBlack(monitorDTO.getCurrArmyIndex())) {
                int index = Integer.parseInt(monitorDTO.getCurrArmyIndex());
                List<Army> armyList = new ArrayList<>();
                armyList.add(record.getArmyList().get(index));
                record.setArmyList(armyList);
            }
            return RespUtil.successResJson(record);
        }
        return null;
    }

}
