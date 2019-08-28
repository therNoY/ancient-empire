package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.websocket.service.MoveAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WebSocketService implements ApplicationContextAware {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;

    private ApplicationContext ac;

    /**
     * 获取单位的移动范围
     * @param unitIndex
     * @return
     */
    public List<Position> getMoveArea(String uuid, ReqUnitIndexDto unitIndex) {
        Long start = new Date().getTime();
        // 1.获取record
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        // 2.构造一个帮助类对象
        MoveAreaService moveAreaService = new MoveAreaService(userRecord, unitIndex, ac);
        List<Position> positions = moveAreaService.getMovePosition();
        log.info("获取移动区域所用时间：{}", new Date().getTime() - start);
        return positions;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    /**
     * 获取移动路径
     * @param moveDto
     * @return
     */
    public List<Position> getMovePath(ReqMoveDto moveDto) {
        // 获取移动路径
        return null;
    }
}
