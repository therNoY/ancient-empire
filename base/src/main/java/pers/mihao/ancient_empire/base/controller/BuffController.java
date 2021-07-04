package pers.mihao.ancient_empire.base.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.Buff;
import pers.mihao.ancient_empire.base.service.BuffService;

/**
 * <p>
 * buff信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@RestController
public class BuffController {

    @Autowired
    BuffService buffService;


    @GetMapping("/buff/get")
    public Buff getBuffByType(@RequestParam String type){
        return buffService.getByType(type);
    }

}
