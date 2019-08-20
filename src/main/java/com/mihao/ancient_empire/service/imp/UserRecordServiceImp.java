package com.mihao.ancient_empire.service.imp;

import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.InitMap;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.entity.mongo.UserMap;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.mongo.dao.UserRecordRepository;
import com.mihao.ancient_empire.service.UserMapService;
import com.mihao.ancient_empire.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRecordServiceImp implements UserRecordService {

    @Autowired
    UserRecordRepository userRecordRepository;

    @Autowired
    UserMapService userMapService;

    /**
     * 获取初始化地图记录
     * @param reqInitMapDto
     * @return
     */
    @Override
    public UserRecord initMapRecord(ReqInitMapDto reqInitMapDto) {
        UserRecord userRecord = new UserRecord();
        userRecord.setMaxPop(reqInitMapDto.getMaxPop());
        // 1.获取地图
        UserMap userMap = userMapService.getEncounterMapById(reqInitMapDto.getUuid());
        if (userMap == null) {
            return null;
        }
        // 1.设置初始化地图
        InitMap map = new InitMap(userMap.getRow(), userMap.getColumn(), userMap.getRegions());
        userRecord.setInitMap(map);
        // 2.设置初始化军队 完善军队信息
        List<Army> armyList = reqInitMapDto.getArmyList();
        for (int i = 0; i < armyList.size(); i++) {
            Army army = armyList.get(i);
            army.setId(i);
            List<Unit> units = new ArrayList<>();
            String color = army.getColor();
            userMap.getUnits().stream()
                    .filter(baseUnit -> baseUnit.getColor().equals(color))
                    .forEach(baseUnit -> {
                        Unit unit = new Unit(baseUnit.getType(), baseUnit.getRow(), baseUnit.getColumn());
                        units.add(unit);
                    });
            army.setUnits(units);
            army.setMoney(reqInitMapDto.getMoney());
        }
        // 3.返回地图记录
        userRecord.setArmyList(armyList);
        return userRecord;
    }
}
