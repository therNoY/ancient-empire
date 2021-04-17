package pers.mihao.ancient_empire.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import org.apache.ibatis.annotations.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDTO;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

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
     * record另存为
     */
    @PostMapping("/api/record/saveAs")
    public RespJson recordSaveAs(@RequestBody ReqSaveRecordDTO reqSaveRecordDto) {
        UserRecord userRecord = userRecordService.getRecordById(reqSaveRecordDto.getUuid());
        userRecord.setRecordName(reqSaveRecordDto.getName());
        userRecord.setCreateTime(LocalDateTime.now());
        userRecord.setUnSave(BaseConstant.NO);
        userRecord.setCreateUserId(reqSaveRecordDto.getUserId());
        userRecord.setUuid(StringUtil.getUUID());
        userRecordService.saveRecord(userRecord);
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
    public RespJson saveRecord(@RequestBody @Validated ReqSaveRecordDTO saveRecordDto, BindingResult result) {
        // 判断是否存在
        boolean isSave = userRecordService.saveRecord(saveRecordDto);
        if (isSave) {
            return RespUtil.successResJson();
        }
        return RespUtil.error(41000);
    }

    /**
     * 查询用户保存的record
     * @param apiConditionDTO
     * @return
     */
    @PostMapping("/api/record/list")
    public RespJson listUserRecordWithPage(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<UserRecord> iPage = userRecordService.listUserRecordWithPage(apiConditionDTO);
        return RespUtil.successPageResJson(iPage);
    }

    /**
     * 查询用户保存的record
     * @param apiConditionDTO
     * @return
     */
    @DeleteMapping("/api/record/{uuid}")
    public RespJson listUserRecordWithPage(@PathVariable("uuid") String uuid) {
        userRecordService.delById(uuid, AuthUtil.getUserId());
        return RespUtil.successResJson();
    }
}
