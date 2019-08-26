package com.mihao.ancient_empire.controller;

import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRecordController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;

    /**
     * 用户选择地图 设置初始化地图 获取初始化地图的Id
     * @param reqInitMapDto
     * @param result
     * @return
     */
    @PostMapping("/record/init")
    public RespJson initMapRecord(@RequestBody @Validated ReqInitMapDto reqInitMapDto, BindingResult result) {
        String recordId = userRecordService.initMapRecord(reqInitMapDto);
        return RespHelper.successResJson(recordId);
    }

    /**
     * 建立WS 连接
     * @return
     */
    @PostMapping("/tempRecord")
    public RespJson saveTempRecord() {
        return null;
    }

    /**
     * 获取record信息
     */
    @GetMapping("/record/{uuid}")
    public RespJson getRecordById(@PathVariable("uuid") String uuid) {
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        return RespHelper.successResJson(userRecord);
    }
}
