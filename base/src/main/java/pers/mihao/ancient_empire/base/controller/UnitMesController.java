package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
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
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dto.ApiOrderDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveUnitMesDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.vo.UnitMesVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
import pers.mihao.ancient_empire.common.vo.AeException;

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
    public IPage<UnitMes> selectUnitMesByCreateUserWithPage(@RequestBody ApiConditionDTO apiConditionDTO) {
        return unitMesService.selectUnitMesByCreateUserWithPage(apiConditionDTO);
    }

    /**
     * 获取用户下载的单位
     *
     * @param conditionDTO
     * @return
     */
    @RequestMapping("/api/unitMes/user/download")
    public IPage<UnitMesVO> getUserDownloadUnitMesWithPage(@RequestBody ApiConditionDTO conditionDTO) {
        return unitMesService.getUserDownloadUnitMesWithPage(conditionDTO);
    }


    /**
     * 获取用户可以下载的单位
     *
     * @param conditionDTO
     * @return
     */
    @RequestMapping("/api/unitMes/downloadAble/list")
    public IPage<UnitMesVO> getDownloadAbleUnitMesWithPage(@RequestBody ApiOrderDTO orderDTO) {
        return unitMesService.getDownloadAbleUnitMesWithPage(orderDTO);
    }

    /**
     * 获取 单位信息类表
     *
     * @param pageSize
     * @param pageNow
     * @return
     */
    @PostMapping("/api/unitMes/enable/all")
    public List<UnitMes> getUserEnableUnitList(ApiRequestDTO apiRequestDTO) {
        return unitMesService.getUserEnableUnitList(apiRequestDTO.getUserId());
    }

    /**
     * 根据单位的id获取单位信息详情
     *
     * @param id
     * @return
     */
    @GetMapping("/api/unitMes/{id}")
    public UnitInfo getUnitMesById(@PathVariable Integer id) {
        return unitMesService.getUnitInfo(id, 0);
    }

    /**
     * 保存单位信息
     *
     * @param unitMes
     * @param result
     * @return
     */
    @PutMapping("/api/unitMes")
    public void saveUnitMes(@RequestBody @Validated ReqSaveUnitMesDTO reqSaveUnitMesDTO) {
        unitMesService.saveUnitInfo(reqSaveUnitMesDTO);
    }

    /**
     * 获取一个单位详细信息
     *
     * @param idLevelInfo 单位和等级
     * @return
     */
    @GetMapping("/unitInfo")
    public UnitInfo getUnitInfo(@RequestParam String idLevelInfo) {

        int index = idLevelInfo.lastIndexOf("_");
        if (index == 0) {
            throw new AeException(40010);
        }
        String id = idLevelInfo.substring(0, index);
        Integer level = Integer.valueOf(idLevelInfo.substring(index + 1));
        UnitInfo unitInfo = unitMesService.getUnitInfo(Integer.valueOf(id), level);
        return unitInfo;
    }

    /**
     * 删除用户的单位(逻辑)
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/unitMes")
    public void deleteUserUnit(@RequestParam Integer id) {
        UnitMes unitMes = unitMesService.getUnitMesById(id);
        if (unitMes != null && unitMes.getCreateUserId().equals(LoginUserHolder.getUserId())) {
            // 其他模板也会引用 先判断是否是草稿 草稿直接删除 否则改成禁用,历史版本同样如此操做
            if (unitMes.getStatus().equals(VersionConstant.DRAFT)) {
                unitMesService.removeById(unitMes.getId());
            }
            // 更新低版本为禁用
            unitMesService.updateUnitStatusByType(unitMes.getType(), VersionConstant.DELETE);
        }
    }


}
