package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 单位等级信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@RestController
public class UnitLevelMesController {

    @Autowired
    UnitLevelMesService unitLevelMesService;

    /**
     * 获取单个单位的等级信息
     * @param unitId
     * @return
     */
    @GetMapping("/api/unitLevel/{unitId}")
    public RespJson getUnitLevelByUnid (@PathVariable("unitId") Integer unitId) {
        return RespUtil.successResJson(unitLevelMesService.getUnitLevelInfoById(unitId));
    }

    /**
     * 获取模板中所有的单位等级信息
     * @param tempId
     * @return
     */
    @GetMapping("/api/unitLevel/list/{tempId}")
    public RespJson getAllUnitLevelByTemp (@PathVariable("tempId") Integer tempId) {
        return RespUtil.successResJson(unitLevelMesService.getAllUnitLevelInfoByTempId(tempId));
    }

    @GetMapping("/root/unitLevel")
    public RespJson getUnitLevelMesList (@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page page = new Page<>(pageNow, pageSize);
        return RespUtil.successPageResJson(unitLevelMesService.getUnitLevelMesList(page));
    }

    @PutMapping("/root/unitLevel")
    public RespJson saveUnitLevelMesList (@RequestBody @Validated UnitLevelMes unitLevelMes, BindingResult result) {
        unitLevelMesService.saveUnitLevelMesList(unitLevelMes);
        return RespUtil.successResJson();
    }
}
