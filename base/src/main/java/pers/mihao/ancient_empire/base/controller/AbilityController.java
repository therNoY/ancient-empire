package pers.mihao.ancient_empire.base.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.service.AbilityService;

/**
 * <p>
 * 单位信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@RestController
public class AbilityController {

    @Autowired
    AbilityService abilityService;

    /**
     *
     * 获取所有能力信息
     * @return
     */
    @GetMapping("/api/ability/list")
    public List<Ability> getAllAbility () {
        return abilityService.list();
    }

}
