package pers.mihao.ancient_empire.base.controller;

import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import pers.mihao.ancient_empire.common.bo.record_dto.ReqSaveRecordDto;
import com.mihao.ancient_empire.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.auth.service.UnitMesService;
import pers.mihao.ancient_empire.auth.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
public class UserRecordController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    MqHelper mqHelper;

    /**
     * 用户选择地图 设置初始化地图 获取初始化地图的Id
     *
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
     *
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


    /**
     * 用户 登陆过保存临时地图
     */
    @PostMapping("/api/tempRecord")
    public RespJson saveTempRecord(@NotBlank String uuid) {
        // 判断是否存在
        boolean isSave = userRecordService.saveTempRecord(uuid);
        if (isSave) {
            return RespHelper.successResJson();
        }
        return RespHelper.errResJson(41000);
    }

    /**
     * 保存地图
     */
    @PostMapping("/api/record")
    public RespJson saveRecord(@RequestBody @Validated ReqSaveRecordDto saveRecordDto, BindingResult result) {
        // 判断是否存在
        boolean isSave = userRecordService.saveRecord(saveRecordDto);
        if (isSave) {
            return RespHelper.successResJson();
        }
        return RespHelper.errResJson(41000);
    }
}
