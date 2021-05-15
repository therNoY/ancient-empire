package pers.mihao.ancient_empire.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.RespUserMapDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTemplateRelationService;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

/**
 * @author mihao
 * @version 1.0
 * @date 2020\9\26 0026 13:54
 */
@RestController
public class UserTemplateController {

    @Autowired
    UserTemplateService userTemplateService;

    @Autowired
    UserTempAttentionService userTempAttentionService;

    @Autowired
    UnitMesService unitMesService;

    @Autowired
    RegionMesService regionMesService;

    @Autowired
    UnitTemplateRelationService unitTemplateRelationService;

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    @GetMapping("/api/userTemp/{id}")
    public UserTemplate selectById(@PathVariable("id") Integer id) {
        return userTemplateService.getTemplateById(id);
    }

    /**
     * 获取用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/page")
    public IPage<UserTemplateVO> getAllTemplateWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        return userTemplateService.getUserTemplateWithPage(reqUserTemplateDTO);
    }

    /**
     * 获取可以下载用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/download/page")
    public IPage<UserTemplateVO> downloadAbleTempWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        return userTemplateService.getDownloadAbleTempWithPage(reqUserTemplateDTO);
    }


    /**
     * 获取用户模板下的所有单位 不传模板查所有
     *
     * @param templateIdDTO
     * @return
     */
    @PostMapping("/api/userTemp/unitList")
    public RespUserMapDTO getUserAllTempUnit(@RequestBody TemplateIdDTO templateIdDTO) {
        List<UnitMes> unitList = userTemplateService.getUnitListByTempId(templateIdDTO);
        List<RegionMes> regionMes = regionMesService.getEnableRegionByTempId(templateIdDTO.getTemplateId());
        RespUserMapDTO userMapDTO = new RespUserMapDTO();
        userMapDTO.setUnitMesList(unitList);
        userMapDTO.setRegionMes(regionMes);
        return userMapDTO;
    }

    /**
     * 获取可以添加的单位
     *
     * @param templateIdDTO
     * @return
     */
    @PostMapping("/api/userTemp/addAbleUnitList")
    public List<UnitMes> getAddAbleUnitList(@RequestBody TemplateIdDTO templateIdDTO) {
        // 获取当前用户创建的
        List<UnitMes> units = unitMesService.getUserEnableUnitList(LoginUserHolder.getUserId());
        List<UnitMes> defaultUnis = unitMesService.getBaseUnitList();
        // 返回代添加的
        return
            CollectionUtil.assignDistinct(UnitMes::getId, units, defaultUnis).stream().filter(unitMes -> {
                if (templateIdDTO.getFilter() != null) {
                    return !templateIdDTO.getFilter().contains(unitMes.getId().toString());
                }
                return true;
            }).collect(Collectors.toList());
    }


    /**
     * 保存模板信息
     *
     * @param reqSaveUserTemplateDTO
     * @return
     */
    @PostMapping("/api/userTemp/saveTemplate")
    public void saveTemplateInfo(@RequestBody ReqSaveUserTemplateDTO reqSaveUserTemplateDTO) {
        if (!reqSaveUserTemplateDTO.getUserId().equals(LoginUserHolder.getUserId())) {
            log.error("用户权限异常{}, {}", reqSaveUserTemplateDTO, LoginUserHolder.getUserId());
            throw new AeException("用户权限异常");
        }
        userTemplateService.saveTemplateInfo(reqSaveUserTemplateDTO);
    }

    @PostMapping("/api/userTempAttention/version/revert")
    public void revertTemplateVersion(@RequestBody TemplateIdDTO idDTO) {
        UserTemplate userTemplate = userTemplateService.getTemplateById(idDTO.getTemplateId());
        if (idDTO.getUserId().equals(userTemplate.getUserId()) && userTemplate.getStatus()
            .equals(VersionConstant.DRAFT)) {
            userTemplateService.removeById(userTemplate);
            userTemplateService.delTemplateCatch(userTemplate);
        }
    }

    /**
     * 根据模板删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/userTemp/{id}")
    public void removeUserTemplate(@PathVariable("id") Integer id) {
        UserTemplate userTemplate = userTemplateService.getTemplateById(id);
        if (!LoginUserHolder.getUserId().equals(userTemplate.getUserId())) {
            log.error("权限不足{}, {}", userTemplate, LoginUserHolder.getUserId());
            throw new AeException();
        }
        userTemplateService.deleteUserTemplate(userTemplate);
    }


    /**
     * 获取当前用户的草稿模板 没有则创建一个
     *
     * @return
     */
    @GetMapping("/api/userTemp/draftTemplate")
    public UserTemplateVO getUserDraftTemplate() {
        UserTemplateVO draftTemp = userTemplateService.getUserDraftTemplate(LoginUserHolder.getUserId());
        TemplateIdDTO templateIdDTO = new TemplateIdDTO();
        templateIdDTO.setTemplateId(draftTemp.getId());
        draftTemp.setBindUintList(userTemplateService.getUnitListByTempId(templateIdDTO));
        return draftTemp;
    }

}
