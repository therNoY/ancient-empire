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
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTemplateRelation;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTemplateRelationService;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.common.vo.RespJson;

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
    UnitTemplateRelationService unitTemplateRelationService;

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    @GetMapping("/api/userTemp/{id}")
    public RespJson selectById(@PathVariable("id") Integer id) {
        return RespUtil.successResJson(userTemplateService.getTemplateById(id));
    }

    /**
     * 获取用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/page")
    public RespJson getAllTemplateWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        IPage<UserTemplateVO> templateList = userTemplateService.getUserTemplateWithPage(reqUserTemplateDTO);
        return RespUtil.successPageResJson(templateList);
    }

    /**
     * 获取可以下载用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/download/page")
    public RespJson downloadAbleTempWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        IPage<UserTemplateVO> templateList = userTemplateService.getDownloadAbleTempWithPage(reqUserTemplateDTO);
        return RespUtil.successPageResJson(templateList);
    }



    /**
     * 获取用户模板下的所有单位 不传模板查所有
     *
     * @param templateIdDTO
     * @return
     */
    @PostMapping("/api/userTemp/unitList")
    public RespJson getUserAllTempUnit(@RequestBody TemplateIdDTO templateIdDTO) {
        List<UnitMes> unitList = userTemplateService.getUserAllTempUnit(templateIdDTO);
        return RespUtil.successResJson(unitList);
    }

    /**
     * 获取可以添加的单位
     *
     * @param templateIdDTO
     * @return
     */
    @PostMapping("/api/userTemp/addAbleUnitList")
    public RespJson getAddAbleUnitList(@RequestBody TemplateIdDTO templateIdDTO) {
        // 获取当前用户创建的
        List<UnitMes> units = unitMesService.getUserEnableUnitList(AuthUtil.getUserId());
        List<UnitMes> defaultUnis = unitMesService.getBaseUnitList();
        // 返回代添加的
        return RespUtil.successResJson(
            CollectionUtil.assignDistinct(UnitMes::getId, units, defaultUnis).stream().filter(unitMes -> {
                if (templateIdDTO.getFilter() != null) {
                    return !templateIdDTO.getFilter().contains(unitMes.getId().toString());
                }
                return true;
            }).collect(Collectors.toList()));
    }


    /**
     * 保存模板信息
     *
     * @param reqSaveUserTemplateDTO
     * @return
     */
    @PostMapping("/api/userTemp/saveTemplate")
    public RespJson saveTemplateInfo(@RequestBody ReqSaveUserTemplateDTO reqSaveUserTemplateDTO) {
        if (!reqSaveUserTemplateDTO.getUserId().equals(AuthUtil.getUserId())) {
            log.error("用户权限异常{}, {}", reqSaveUserTemplateDTO, AuthUtil.getUserId());
            throw new AncientEmpireException("用户权限异常");
        }
        userTemplateService.saveTemplateInfo(reqSaveUserTemplateDTO);
        return RespUtil.successResJson();
    }

    @PostMapping("/api/userTempAttention/version/revert")
    public RespJson revertTemplateVersion(@RequestBody TemplateIdDTO idDTO) {
        UserTemplate userTemplate = userTemplateService.getTemplateById(idDTO.getTemplateId());
        if (idDTO.getUserId().equals(userTemplate.getUserId()) && userTemplate.getStatus().equals(VersionConstant.DRAFT)) {
            userTemplateService.removeById(userTemplate);
            userTemplateService.delTemplateCatch(userTemplate);
        }
        return RespUtil.successResJson();
    }

    /**
     * 根据模板删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/userTemp/{id}")
    public RespJson removeUserTemplate(@PathVariable("id") Integer id) {
        UserTemplate userTemplate = userTemplateService.getTemplateById(id);
        if (!AuthUtil.getUserId().equals(userTemplate.getUserId())) {
            log.error("权限不足{}, {}", userTemplate, AuthUtil.getUserId());
            return RespUtil.error();
        }
        userTemplateService.deleteUserTemplate(userTemplate);
        return RespUtil.successResJson();
    }


    /**
     * 获取当前用户的草稿模板 没有则创建一个
     *
     * @return
     */
    @GetMapping("/api/userTemp/draftTemplate")
    public RespJson getUserDraftTemplate() {
        UserTemplateVO draftTemp = userTemplateService.getUserDraftTemplate(AuthUtil.getUserId());
        if (draftTemp == null) {
            // 为空就创建一个草稿模板
            // 1.获取系统默认模板
            UserTemplate defaultTemp = userTemplateService.getById(1);
            defaultTemp.setId(null);
            draftTemp = BeanUtil.copyValueFromParent(defaultTemp, UserTemplateVO.class);
            draftTemp.setUserId(AuthUtil.getUserId());
            draftTemp.setStatus(VersionConstant.DRAFT);
            draftTemp.setVersion(0);
            userTemplateService.save(draftTemp);
            // 2.模板绑定默认单位
            List<UnitMes> defaultUnitMes = unitMesService.getBaseUnitList();
            UnitTemplateRelation relation;
            for (UnitMes unitMes : defaultUnitMes) {
                relation = new UnitTemplateRelation();
                relation.setUnitId(unitMes.getId());
                relation.setTempId(draftTemp.getId());
                unitTemplateRelationService.save(relation);
            }
        }
        TemplateIdDTO templateIdDTO = new TemplateIdDTO();
        templateIdDTO.setTemplateId(draftTemp.getId());
        draftTemp.setBindUintList(userTemplateService.getUserAllTempUnit(templateIdDTO));
        return RespUtil.successResJson(draftTemp);
    }

}
