package com.mihao.ancient_empire.controller;


import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import com.mihao.ancient_empire.common.util.RespHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get/{id}")
    public RespJson getTest(@PathVariable String id) {
        log.info("get id: {}", id);
        return RespHelper.successResJson();
    }

    @PostMapping("/post")
    public RespJson postTest(@RequestBody Dog dog) {
        log.info("post dog: {}", dog);
        return RespHelper.successResJson();
    }

    @DeleteMapping("/delete/{id}")
    public RespJson deleteTest(@PathVariable String id) {
        log.info("delete id: {}", id);
        return RespHelper.successResJson();
    }

    @PutMapping("/put")
    public RespJson putTest(@RequestBody Dog dog) {
        log.info("put dog: {}", dog);
        return RespHelper.successResJson();
    }

    @RequestMapping("/root/get")
    public RespJson getRootMes() {
        log.info("只有管理员才能看到");
        return RespHelper.successResJson();
    }

    @RequestMapping("/api/get")
    public RespJson getApiMes() {
        log.info("只有登录过才能看到");
        return RespHelper.successResJson();
    }
}
