package pers.mihao.ancient_empire.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTemplateRelation;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTemplateRelationService;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @author mihao
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

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    @GetMapping("/api/userTemp/{id}")
    public RespJson selectById(@PathVariable("id") String id) {
        return RespUtil.successResJson(userTemplateService.selectById(id));
    }

    /**
     * 获取用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/page")
    public RespJson getAllTemplateWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        IPage<UserTemplate> templateList = userTemplateService.getUserTemplateWithPage(reqUserTemplateDTO);
        return RespUtil.successPageResJson(templateList);
    }

    /**
     * 获取可以下载用户的模板
     *
     * @return
     */
    @PostMapping("/api/userTemp/download/page")
    public RespJson getDownloadAbleTempWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        IPage<UserTemplate> templateList = userTemplateService.getDownloadAbleTempWithPage(reqUserTemplateDTO);
        return RespUtil.successPageResJson(templateList);
    }

    /**
     * 获取用户下载的模板
     *
     * @return
     */
    @PostMapping("/api/userAttentionTemp/page")
    public RespJson getAttentionTemplateWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        IPage<UserTemplate> templateList = userTemplateService.getAttentionTemplateWithPage(reqUserTemplateDTO);
        return RespUtil.successPageResJson(templateList);
    }

    /**
     * 获取用户模板下的所有单位 不传模板查所有
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
     * @param templateIdDTO
     * @return
     */
    @PostMapping("/api/userTemp/addAbleUnitList")
    public RespJson getAddAbleUnitList(@RequestBody TemplateIdDTO templateIdDTO) {
        // 获取当前用户创建的
        List<UnitMes> units = unitMesService.getUnitListByCreateUser(AuthUtil.getUserId());
        List<UnitMes> defaultUnis = unitMesService.getDefaultUnitList();
        // 返回代添加的
        return RespUtil.successResJson(CollectionUtil.assignDistinct(UnitMes::getId, units, defaultUnis).stream().filter(unitMes -> {
            if (templateIdDTO.getFilter() != null){
                return !templateIdDTO.getFilter().contains(unitMes.getId().toString());
            }
            return true;
        }).collect(Collectors.toList()));
    }


    /**
     * 保存模板信息
     * @param unitTemplateRelation
     * @return
     */
    @PostMapping("/api/userTemp/saveTemplate")
    public RespJson saveTemplateInfo(@RequestBody ReqSaveUserTemplateDTO reqSaveUserTemplateDTO) {
        // 1.保存模板信息
        UserTemplate template = (UserTemplate) BeanUtil.copyValueToParent(reqSaveUserTemplateDTO, UserTemplate.class);
        // 不是点击取消的保存 而是保存草稿
        if (!Boolean.TRUE.equals(reqSaveUserTemplateDTO.getCancelSave()) && (0 == template.getStatus())) {
            template.setStatus(1);
        }
        userTemplateService.updateById(template);

        // 2.保存单位模板关系
        QueryWrapper<UnitTemplateRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("temp_id", reqSaveUserTemplateDTO.getId());
        unitTemplateRelationService.remove(wrapper);

        List<UnitTemplateRelation> relations = new ArrayList<>();
        for (Integer unitId : reqSaveUserTemplateDTO.getRelationUnitList()) {
            UnitTemplateRelation relation = new UnitTemplateRelation();
            relation.setTempId(reqSaveUserTemplateDTO.getId());
            relation.setUnitId(unitId);
            relations.add(relation);
        }
        unitTemplateRelationService.saveBatch(relations);

        return RespUtil.successResJson();
    }

    @DeleteMapping("/api/userTemp/{id}")
    public RespJson removeUserTemplate(@PathVariable("id") String id){
        userTemplateService.deleteUserTemplate(AuthUtil.getUserId(), id);
        return RespUtil.successResJson();
    }

    @DeleteMapping("/api/userTempAttention/{id}")
    public RespJson removeUserTemplateAttention(@PathVariable("id") String id){
        userTempAttentionService.removeUserAttention(AuthUtil.getUserId(), id);
        return RespUtil.successResJson();
    }

    /**
     * 获取当前用户的草稿模板 没有则创建一个
     * @return
     */
    @GetMapping("/api/userTemp/draftTemplate")
    public RespJson getUserDraftTemplate(){
        UserTemplate draftTemp = userTemplateService.getUserDraftTemplate(AuthUtil.getUserId());
        if (draftTemp == null) {
            // 为空就创建一个草稿模板
            // 1.获取系统默认模板
            UserTemplate defaultTemp = userTemplateService.getById(1);
            defaultTemp.setLinkNum(null);
            defaultTemp.setCountStart(null);
            defaultTemp.setId(null);
            draftTemp = BeanUtil.deptClone(defaultTemp);
            draftTemp.setUserId(AuthUtil.getUserId());
            draftTemp.setStatus(0);
            userTemplateService.save(draftTemp);
            // 2.模板绑定默认单位
            List<UnitMes> defaultUnitMes =  unitMesService.getDefaultUnitList();
            UnitTemplateRelation relation;
            for (UnitMes unitMes : defaultUnitMes) {
                relation = new UnitTemplateRelation();
                relation.setUnitId(unitMes.getId());
                relation.setTempId(draftTemp.getId());
                unitTemplateRelationService.save(relation);
            }
        }
        TemplateIdDTO templateIdDTO = new TemplateIdDTO();
        templateIdDTO.setTemplateId(draftTemp.getId().toString());
        draftTemp.setBindUintList(userTemplateService.getUserAllTempUnit(templateIdDTO));
        return RespUtil.successResJson(draftTemp);
    }

    @PostMapping("/api/userTemp/downloadTemp")
    public RespJson downloadTemp(@RequestBody UserTempAttention userTempAttention){
        userTempAttention.setUserId(AuthUtil.getUserId());
        userTempAttention.setCreateTime(LocalDateTime.now());
        userTempAttentionService.saveOrUpdate(userTempAttention);
        return RespUtil.successResJson();
    }

}
