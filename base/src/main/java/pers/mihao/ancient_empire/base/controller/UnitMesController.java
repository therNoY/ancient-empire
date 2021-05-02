package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ser.Serializers.Base;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ApiOrderDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveUnitMesDTO;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.vo.UnitMesVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 单位信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@RestController
public class UnitMesController {

    @Autowired
    UnitMesService unitMesService;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitAbilityService unitAbilityService;
    @Autowired
    UnitLevelMesService unitLevelMesService;

    /**
     * 获取 单位信息类表
     *
     * @param pageSize
     * @param pageNow
     * @return
     */
    @PostMapping("/api/unitMes/user/list")
    public RespJson selectUnitMesByCreateUserWithPage(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<UnitMes> page = unitMesService.selectUnitMesByCreateUserWithPage(apiConditionDTO);
        return RespUtil.successPageResJson(page);
    }

    /**
     * 获取用户下载的单位
     * @param conditionDTO
     * @return
     */
    @RequestMapping("/api/unitMes/user/download")
    public RespJson getUserDownloadUnitMesWithPage(@RequestBody ApiConditionDTO conditionDTO){
        IPage<UnitMesVO> page = unitMesService.getUserDownloadUnitMesWithPage(conditionDTO);
        return RespUtil.successPageResJson(page);
    }


    /**
     * 获取用户可以下载的单位
     * @param conditionDTO
     * @return
     */
    @RequestMapping("/api/unitMes/downloadAble/list")
    public RespJson getDownloadAbleUnitMesWithPage(@RequestBody ApiOrderDTO orderDTO){
        IPage<UnitMesVO> page = unitMesService.getDownloadAbleUnitMesWithPage(orderDTO);
        return RespUtil.successPageResJson(page);
    }

    /**
     * 获取 单位信息类表
     *
     * @param pageSize
     * @param pageNow
     * @return
     */
    @PostMapping("/api/unitMes/enable/all")
    public RespJson getUserEnableUnitList(ApiRequestDTO apiRequestDTO) {
        List<UnitMes> unitMesIPage = unitMesService.getUserEnableUnitList(apiRequestDTO.getUserId());
        return RespUtil.successResJson(unitMesIPage);
    }

    /**
     * 根据单位的id获取单位信息详情
     *
     * @param id
     * @return
     */
    @GetMapping("/api/unitMes/{id}")
    public RespJson getUnitMesById(@PathVariable Integer id) {
        return RespUtil.successResJson(unitMesService.getUnitInfo(id, 0));
    }

    /**
     * 保存单位信息
     *
     * @param unitMes
     * @param result
     * @return
     */
    @PutMapping("/api/unitMes")
    public RespJson saveUnitMes(@RequestBody @Validated ReqSaveUnitMesDTO reqSaveUnitMesDTO) {

        Integer unitId = reqSaveUnitMesDTO.getBaseInfo().getId();

        // 1.更新基本信息
        if (BaseConstant.DRAFT.equals(reqSaveUnitMesDTO.getOptType())){
            if (reqSaveUnitMesDTO.getBaseInfo().getStatus().equals(BaseConstant.OFFICIAL)) {
                // 当前是正式版本 新加一个版本作为草稿
                UnitMes unitMes = BeanUtil.deptClone(reqSaveUnitMesDTO.getBaseInfo());
                unitMes.setStatus(BaseConstant.DRAFT);
                unitMes.setVersion(reqSaveUnitMesDTO.getBaseInfo().getVersion() + 1);
                unitMes.setId(null);
                unitMes.setCreateTime(LocalDateTime.now());
                unitMes.setUpdateTime(LocalDateTime.now());
                unitMesService.saveUnitMes(unitMes);
                unitId = unitMes.getId();
            } else {
                // 当前也是草稿版本 直接更新
                unitMesService.updateInfoById(reqSaveUnitMesDTO.getBaseInfo());
            }
        } else {
            if (reqSaveUnitMesDTO.getBaseInfo().getStatus().equals(BaseConstant.OFFICIAL)) {
                // 当前是正式版本 新加一个版本作为草稿
                UnitMes unitMes = BeanUtil.deptClone(reqSaveUnitMesDTO.getBaseInfo());
                unitMes.setStatus(BaseConstant.OFFICIAL);
                unitMes.setVersion(reqSaveUnitMesDTO.getBaseInfo().getVersion() + 1);
                unitMes.setId(null);
                unitMes.setCreateTime(LocalDateTime.now());
                unitMes.setUpdateTime(LocalDateTime.now());
                unitMesService.saveUnitMes(unitMes);
                unitId = unitMes.getId();
            } else {
                // 当前是草稿版本 直接更新
                reqSaveUnitMesDTO.getBaseInfo().setUpdateTime(LocalDateTime.now());
                reqSaveUnitMesDTO.getBaseInfo().setStatus(BaseConstant.OFFICIAL);
                unitMesService.updateInfoById(reqSaveUnitMesDTO.getBaseInfo());
            }
            unitMesService.delMaxVersionCatch(reqSaveUnitMesDTO.getBaseInfo().getType());
        }

        // 2.更新能力信息
        unitAbilityService.updateUnitAbility(unitId, reqSaveUnitMesDTO.getAbilityInfo());
        // 3.更新等级信息
        for (UnitLevelMes levelMes : reqSaveUnitMesDTO.getLevelInfoData()) {
            levelMes.setUnitId(unitId);
            unitLevelMesService.saveUnitLevelMesList(levelMes);
        }

        return RespUtil.successResJson();
    }

    /**
     * 获取一个单位详细信息
     *
     * @param idLevelInfo 单位和等级
     * @return
     */
    @GetMapping("/unitInfo")
    public RespJson getUnitInfo(@RequestParam String idLevelInfo) {

        int index = idLevelInfo.lastIndexOf("_");
        if (index == 0) {
            return RespUtil.error(40010);
        }
        String id = idLevelInfo.substring(0, index);
        Integer level = Integer.valueOf(idLevelInfo.substring(index + 1));
        UnitInfo unitInfo = unitMesService.getUnitInfo(Integer.valueOf(id), level);
        return RespUtil.successResJson(unitInfo);
    }

    /**
     * 删除用户的单位
     * @param id
     * @return
     */
    @DeleteMapping("/api/unitMes")
    public RespJson deleteUserUnit(@RequestParam Integer id){
        UnitMes unitMes = unitMesService.getUnitMesById(id);
        if (unitMes != null && unitMes.getCreateUserId().equals(AuthUtil.getUserId())) {
            // 其他模板也会引用 先判断是否是草稿 草稿直接删除 否则改成禁用,历史版本同样如此操做
            if (unitMes.getStatus().equals(BaseConstant.DRAFT)) {
                unitMesService.removeById(unitMes.getId());
            }
            // 更新低版本为禁用
            unitMesService.updateUnitStatusByType(unitMes.getType(), BaseConstant.DELETE);
        }
        return RespUtil.error();
    }




}
