package pers.mihao.ancient_empire.base.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;

/**
 * <p>
 * 能力信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@RestController
public class UnitAbilityController {


    @Autowired
    UnitAbilityService unitAbilityService;

    @Autowired
    AbilityService abilityService;

    /**
     * 获取所有能力信息
     *
     * @return
     */
    @GetMapping("/api/unitAbility/{unitId}")
    public List<Ability> getUnitLevelByTemp(@PathVariable("unitId") Integer unitId) {
        return abilityService.getUnitAbilityList(unitId);
    }

}
