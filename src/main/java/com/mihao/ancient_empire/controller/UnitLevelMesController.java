package com.mihao.ancient_empire.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.dto.ReqPageDto;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/root/unitLevel")
    public RespJson getUnitLevelMesList (@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page page = new Page<>(pageNow, pageSize);
        return RespHelper.successPageResJson(unitLevelMesService.getUnitLevelMesList(page));
    }

    @PutMapping("/root/unitLevel")
    public RespJson saveUnitLevelMesList (@RequestBody @Validated UnitLevelMes unitLevelMes, BindingResult result) {
        unitLevelMesService.saveUnitLevelMesList(unitLevelMes);
        return RespHelper.successResJson();
    }
}
