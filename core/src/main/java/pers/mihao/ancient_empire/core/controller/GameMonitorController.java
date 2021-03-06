package pers.mihao.ancient_empire.core.controller;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.controller.dto.GetMonitorInDTO;
import pers.mihao.ancient_empire.core.controller.dto.GetMonitorOutDTO;
import pers.mihao.ancient_empire.core.dto.MonitorDTO;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.manger.net.RoomSessionManger;

/**
 * 游戏监控的
 *
 * @author mihao
 * @version 1.0
 * @date 2021\1\30 0030 14:40
 */
@RestController
public class GameMonitorController {

    @Autowired
    GameCoreManger gameCoreManger;
    @Autowired
    GameSessionManger gameSessionManger;
    @Autowired
    RoomSessionManger roomSessionManger;


    /**
     *
     */
    @RequestMapping("/admin/monitor/room/list")
    public void getGameRoomList() {

    }

    @RequestMapping("/api/monitor")
    public Object getGameDetailById(@RequestBody MonitorDTO monitorDTO) {
        GameContext gameContext = gameCoreManger.getGameContextById(monitorDTO.getGameId());
        if (StringUtil.isBlack(monitorDTO.getGameId())) {
            gameContext = gameCoreManger.getOneGame();
        }

        CommonHandler commonHandler = new CommonHandler();
        commonHandler.setGameContext(gameContext);
        if (CommonConstant.YES.equals(monitorDTO.getCurrArmy())) {
            Army army = BeanUtil.deptClone(commonHandler.currArmy());
            if (StringUtil.isNotBlack(monitorDTO.getCurrUnitType())) {
                army.setUnits(
                    army.getUnits().stream().filter(unit -> unit.getType().equals(monitorDTO.getCurrUnitType()))
                        .collect(Collectors.toList()));
            }
            return army;
        } else if (CommonConstant.YES.equals(monitorDTO.getCurrUnit())) {
            return commonHandler.currUnit();
        } else if (CommonConstant.YES.equals(monitorDTO.getAll())) {
            return gameContext;
        } else if (CommonConstant.YES.equals(monitorDTO.getNoMap())) {
            UserRecord record = BeanUtil.deptClone(gameContext.getUserRecord());
            record.setGameMap(null);
            if (StringUtil.isNotBlack(monitorDTO.getCurrArmyIndex())) {
                int index = Integer.parseInt(monitorDTO.getCurrArmyIndex());
                List<Army> armyList = new ArrayList<>();
                armyList.add(record.getArmyList().get(index));
                record.setArmyList(armyList);
            }
            return record;
        } else if (CommonConstant.YES.equals(monitorDTO.getSession())) {
            return getSessionMessage();
        }
        return null;
    }

    private JSONObject getSessionMessage() {
        JSONObject jsonObject = new JSONObject();
        Map gameSessionMap = (Map) ReflectUtil.getValueByFieldName(gameSessionManger, "gameSessionMap");
        Map roomSessionMap = (Map) ReflectUtil.getValueByFieldName(roomSessionManger, "roomSessionMap");
        jsonObject.put("gameSession", gameSessionMap);
        jsonObject.put("roomSessionMap", roomSessionMap);
        return jsonObject;
    }


    /**
     *
     * @return
     */
    @RequestMapping("/admin/monitor/record/list")
    public List<GetMonitorOutDTO> getRunningRecord(@RequestBody ApiConditionDTO apiConditionDTO){
        // 当前活跃的全部游戏
        List<GameContext> list = gameCoreManger.getAllGameContextList();
        if (StringUtil.isNotBlack(apiConditionDTO.getCondition())) {
            // 条件过滤
            list = list.stream()
                .filter(gameContext -> gameContext.getGameId().contains(apiConditionDTO.getCondition())
                    || gameContext.getUserRecord().getCreateUserId().toString().contains(apiConditionDTO.getCondition())
                    || gameContext.getUserRecord().getCurrPlayer().contains(apiConditionDTO.getCondition())
                    || gameContext.getUserRecord().getRecordName().contains(apiConditionDTO.getCondition()))
                .collect(Collectors.toList());
        }
        // 分页
        if (list != null && list.size() > apiConditionDTO.getLimitStart()) {
            int count = Math.min(apiConditionDTO.getLimitCount(), list.size() - apiConditionDTO.getLimitStart());
            list = list.subList(apiConditionDTO.getLimitStart(), count);
        } else {
            list = new ArrayList<>();
        }

        List<GetMonitorOutDTO> res = new ArrayList<>();

        for (GameContext gameContext : list) {
            GetMonitorOutDTO getMonitorOutDTO = new GetMonitorOutDTO();
            getMonitorOutDTO.setRecordName(gameContext.getUserRecord().getRecordName());
            getMonitorOutDTO.setUuid(gameContext.getUserRecord().getUuid());
            getMonitorOutDTO.setCreateTime(gameContext.getUserRecord().getCreateTime());
            getMonitorOutDTO.setCreateUserId(gameContext.getUserRecord().getCreateUserId());
            getMonitorOutDTO.setStatusMachine(gameContext.getStatusMachine());
            getMonitorOutDTO.setSubStatusMachine(gameContext.getSubStatusMachine());
            res.add(getMonitorOutDTO);
        }
        return res;
    }

    @RequestMapping("/api/runRecord/{uuid}")
    public UserRecord getOneRunningRecord(@PathVariable("uuid") String uuid){
        GameContext gameContext = gameCoreManger.getGameContextById(uuid);
        return gameContext.getUserRecord();
    }

}
