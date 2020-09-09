package pers.mihao.ancient_empire.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.startup.rabbit_consumer.MongoCdrConsumer;

/**
 * 项目需要初始化的数据 比如读取全局的配置文件
 */
@Component
public class InitApplication implements CommandLineRunner, ApplicationContextAware {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    MongoCdrConsumer mongoCdrConsumer;

    @Override
    public void run(String... args) throws Exception {
        log.info("处理初始化工作");
        initAdminSetting();
        initAppCatch();
        mongoCdrConsumer.router();
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.setApplicationContext(applicationContext);
    }


}
