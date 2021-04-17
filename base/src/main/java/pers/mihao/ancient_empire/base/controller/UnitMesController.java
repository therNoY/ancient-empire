package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dto.ReqSaveUnitMesDTO;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
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
    @PostMapping("/api/unitMes/list")
    public RespJson getUnitMesListWithPage(@RequestBody ApiConditionDTO apiConditionDTO) {
        IPage<UnitMes> unitMesIPage = unitMesService.selectUnitMesWithPage(apiConditionDTO);
        return RespUtil.successPageResJson(unitMesIPage);
    }

    /**
     * 获取 单位信息类表
     *
     * @param pageSize
     * @param pageNow
     * @return
     */
    @PostMapping("/api/unitMes/all")
    public RespJson getUserEnableUnitList(ApiRequestDTO apiRequestDTO) {
        List<UnitMes> unitMesIPage = unitMesService.getUserEnableUnitList(apiRequestDTO.getUserId());
        return RespUtil.successResJson(unitMesIPage);
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
        // 1.更新基本信息
        unitMesService.updateInfoById(reqSaveUnitMesDTO.getBaseInfo());
        // 2.更新能力信息
        unitAbilityService
            .updateUnitAbility(reqSaveUnitMesDTO.getBaseInfo().getId(), reqSaveUnitMesDTO.getAbilityInfo());
        // 3.更新等级信息
        for (UnitLevelMes levelMes : reqSaveUnitMesDTO.getLevelInfoData()) {
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
}
