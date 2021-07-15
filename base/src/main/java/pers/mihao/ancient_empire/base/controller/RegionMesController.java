package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.service.RegionMesService;

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
    public IPage<RegionMes> getRegionMesList(@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page<RegionMes> page = new Page<>(pageNow, pageSize);
        return regionMesService.getList(page);
    }

    /**
     * 保存Region
     * @param regionMes
     * @return
     */
    @PutMapping("/root/region")
    public void saveRegion(@RequestBody RegionMes regionMes) {
        regionMesService.saveUnitMes(regionMes);
    }


    @GetMapping("/regionInfo")
    public RegionMes getRegionInfo(@RequestParam  String type) {
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(type);
        return regionMes;
    }
}
