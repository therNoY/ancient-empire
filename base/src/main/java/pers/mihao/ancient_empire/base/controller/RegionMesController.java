package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 地形信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@RestController
public class RegionMesController {

    @Autowired
    RegionMesService regionMesService;

    /**
     * 获取所有的地形
     * @param pageSize
     * @param pageNow
     * @return
     */
    @GetMapping("/root/region")
    public RespJson getRegionMesList(@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page<RegionMes> page = new Page<>(pageNow, pageSize);
        IPage<RegionMes> unitMesIPage = regionMesService.getList(page);
        return RespUtil.successPageResJson(unitMesIPage);
    }

    /**
     * 保存Region
     * @param regionMes
     * @param result
     * @return
     */
    @PutMapping("/root/region")
    public RespJson saveRegion(@RequestBody @Validated RegionMes regionMes, BindingResult result) {
        regionMesService.saveUnitMes(regionMes);
        return RespUtil.successResJson();
    }


    @GetMapping("/regionInfo")
    public RespJson getRegionInfo(@RequestParam  String type) {
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(type);
        return RespUtil.successResJson(regionMes);
    }
}
