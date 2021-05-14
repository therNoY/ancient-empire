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
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

@RestController
public class TestController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get/{id}")
    public void getTest(@PathVariable String id) {
        log.info("get id: {}", id);
    }

    @PostMapping("/post")
    public void postTest(@RequestBody Dog dog) {
        log.info("post dog: {}", dog);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTest(@PathVariable String id) {
        log.info("delete id: {}", id);
    }

    @PutMapping("/put")
    public void putTest(@RequestBody Dog dog) {
        log.info("put dog: {}", dog);
    }

    @RequestMapping("/root/get")
    public void getRootMes() {
        log.info("只有管理员才能看到");
    }

    @RequestMapping("/api/get")
    public void getApiMes() {
        log.info("只有登录过才能看到");
    }
}
