package pers.mihao.ancient_empire.core.manger;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import pers.mihao.ancient_empire.common.annotation.ExecuteTime;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.core.dto.ReqUnitIndexDto;
import pers.mihao.ancient_empire.core.eums.WSPath;
import pers.mihao.ancient_empire.core.eums.WsMethodEnum;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.util.WsRespHelper;

/**
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Manger
public class EventDispatcherManger {

    private Logger log = LoggerFactory.getLogger(this.getClass());



    /**
     * 获取移动区域
     * 如果是领主站在自己的城堡且没有移动 就显示action选项
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/game/getMoveArea")
    public void getMoveArea(Principal principal, String msg) {

    }

}
