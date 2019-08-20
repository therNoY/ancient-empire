package com.mihao.ancient_empire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

/**
 * 项目需要初始化的数据 比如读取全局的配置文件
 */
public class InitApplication implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        initAdminSetting();
    }

    /**
     * 初始化管理员的自定义配置
     */
    private void initAdminSetting() {
        log.info("获取用户个性化设计.....");
    }
}
