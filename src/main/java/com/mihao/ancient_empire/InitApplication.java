package com.mihao.ancient_empire;

import com.mihao.ancient_empire.service.UserRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目需要初始化的数据 比如读取全局的配置文件
 */
@Component
public class InitApplication implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;

    @Override
    public void run(String... args) throws Exception {
        log.info("处理初始化工作");
        initAdminSetting();
        initAppCatch();
    }

    private void initAppCatch() {
        userRecordService.initMap();
    }

    /**
     * 初始化管理员的自定义配置
     */
    private void initAdminSetting() {
        log.info("获取用户个性化设计.....");
    }
}
