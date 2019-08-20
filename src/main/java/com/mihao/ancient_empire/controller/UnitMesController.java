package com.mihao.ancient_empire.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.service.UnitMesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/root/unit")
    public RespJson getUnitMesList(@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page<UnitMes> page = new Page<>(pageNow, pageSize);
        IPage<UnitMes> unitMesIPage = unitMesService.getList(page);
        return RespHelper.successPageResJson(unitMesIPage);
    }

    @PutMapping("/root/unit")
    public RespJson saveUnit(@RequestBody @Validated UnitMes unitMes, BindingResult result) {
        unitMesService.saveUnitMes(unitMes);
        return RespHelper.successResJson();
    }
}
