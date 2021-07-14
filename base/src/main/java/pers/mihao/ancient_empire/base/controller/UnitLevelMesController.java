package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;

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
     *
     * @param unitId
     * @return
     */
    @GetMapping("/api/unitLevel/{unitId}")
    public List<UnitLevelMes> getUnitLevelByUnid(@PathVariable("unitId") Integer unitId) {
        return unitLevelMesService.getUnitLevelInfoById(unitId);
    }

    /**
     * 获取模板中所有的单位等级信息
     *
     * @param tempId
     * @return
     */
    @GetMapping("/api/unitLevel/list/{tempId}")
    public Map<String, UnitLevelMes> getAllUnitLevelByTemp(@PathVariable("tempId") Integer tempId) {
        return unitLevelMesService.getAllUnitLevelInfoByTempId(tempId);
    }
}
