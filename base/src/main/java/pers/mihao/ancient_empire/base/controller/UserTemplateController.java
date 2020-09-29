package pers.mihao.ancient_empire.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import javax.websocket.server.PathParam;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\26 0026 13:54
 */
@RestController
public class UserTemplateController {

    @Autowired
    UserTemplateService userTemplateService;

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    @GetMapping("/api/userTemp/{id}")
    public RespJson selectById(@PathVariable("id") String id) {
        return RespUtil.successResJson(userTemplateService.selectById(id));
    }

}
