package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.dto.ws_dto.LevelDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LevelHelper {

    static Integer level0 = Integer.valueOf(AppConfig.get("level0"));
    static Integer level1 = Integer.valueOf(AppConfig.get("level1"));
    static Integer level2 = Integer.valueOf(AppConfig.get("level2"));
    static Integer level3 = Integer.valueOf(AppConfig.get("level3"));

    private static Integer[] levels = new Integer[]{level0, level1, level2, level3};

    /**
     * 处理单位升级
     * @param level
     */
    public void handleLevel(LevelDto levelDto, Integer level) {
        if (levelDto.getEndExperience() >= levels[level]) {
            levelDto.setLeaveUp(true);
            levelDto.setEndExperience(levelDto.getEndExperience() - levels[level]);
        }else {
            levelDto.setLeaveUp(false);
        }
    }
}
