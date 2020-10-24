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

    @GetMapping("/api/unitLevel/{tempId}")
    public RespJson getUnitLevelByTemp (@PathVariable("tempId") Integer tempId) {
        return RespUtil.successResJson(unitLevelMesService.getUnitLevelByTemp(tempId));
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
