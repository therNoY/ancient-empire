package com.mihao.ancient_empire.controller;

import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRecordController {

    @Autowired
    UserRecordService userRecordService;

    /**
     * 用户选择地图 设置初始化地图
     * @param reqInitMapDto
     * @param result
     * @return
     */
    @PostMapping("/api/record/init")
    public RespJson initMapRecord(@RequestBody @Validated ReqInitMapDto reqInitMapDto, BindingResult result) {
        UserRecord record = userRecordService.initMapRecord(reqInitMapDto);
        return RespHelper.successResJson(record);
    }

    /**
     * 建立WS 连接
     * @return
     */
    @PostMapping("/api/tempRecord")
    public RespJson saveTempRecord() {
        return null;
    }
}
