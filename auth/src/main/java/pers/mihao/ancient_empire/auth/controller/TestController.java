package pers.mihao.ancient_empire.auth.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

@RestController
public class TestController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get/{id}")
    public RespJson getTest(@PathVariable String id) {
        log.info("get id: {}", id);
        return RespUtil.successResJson();
    }

    @PostMapping("/post")
    public RespJson postTest(@RequestBody Dog dog) {
        log.info("post dog: {}", dog);
        return RespUtil.successResJson();
    }

    @DeleteMapping("/delete/{id}")
    public RespJson deleteTest(@PathVariable String id) {
        log.info("delete id: {}", id);
        return RespUtil.successResJson();
    }

    @PutMapping("/put")
    public RespJson putTest(@RequestBody Dog dog) {
        log.info("put dog: {}", dog);
        return RespUtil.successResJson();
    }

    @RequestMapping("/root/get")
    public RespJson getRootMes() {
        log.info("只有管理员才能看到");
        return RespUtil.successResJson();
    }

    @RequestMapping("/api/get")
    public RespJson getApiMes() {
        log.info("只有登录过才能看到");
        return RespUtil.successResJson();
    }
}
