package pers.mihao.ancient_empire.base.controller;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.dto.ApiPageDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import java.util.List;

@RestController
public class UserRecordController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;

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
        return RespUtil.successResJson(userRecord);
    }


    /**
     * 用户 登陆过保存临时地图
     */
    @PostMapping("/api/tempRecord")
    public RespJson saveTempRecord(@NotBlank String uuid) {
        // 判断是否存在
        boolean isSave = userRecordService.saveTempRecord(uuid);
        if (isSave) {
            return RespUtil.successResJson();
        }
        return RespUtil.error(41000);
    }

    /**
     * 保存地图
     */
    @PostMapping("/api/record")
    public RespJson saveRecord(@RequestBody @Validated ReqSaveRecordDto saveRecordDto, BindingResult result) {
        // 判断是否存在
        boolean isSave = userRecordService.saveRecord(saveRecordDto);
        if (isSave) {
            return RespUtil.successResJson();
        }
        return RespUtil.error(41000);
    }

    @PostMapping("/api/userRecord/list")
    public RespJson listUserRecordWithPage(ApiConditionDTO apiConditionDTO) {
        List<UserRecord> list = userRecordService.listUserRecordWithPage(apiConditionDTO);
        return RespUtil.successResJson(list);
    }
}
