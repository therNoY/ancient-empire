package pers.mihao.ancient_empire.startup;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import pers.mihao.ancient_empire.common.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.base.controller.GameFileController;
import pers.mihao.ancient_empire.base.dao.UserTemplateDAO;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;

/**
 * 项目需要初始化的数据 比如读取全局的配置文件
 */
@Component
public class InitApplication implements CommandLineRunner, ApplicationContextAware {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UserMapService userMapService;
    @Autowired
    RegionMesService regionMesService;

    @Autowired
    GameFileController gameFileController;

    @Override
    public void run(String... args) throws Exception {
        log.info("处理初始化工作");
        initAdminSetting();
        initAppCatch();
        delNoUseImg();
    }

    /**
     * 删除无用的图片
     */
    private void delNoUseImg() {

        gameFileController.delTempFile();

        List<Pair<Class, String[]>> delList = new ArrayList<>();
        delList.add(new Pair<>(UserTemplateDAO.class, new String[]{"summonAnimation", "attachAnimation", "summonAnimation", "levelupAnimation"}));
//        doDel(delList);
    }

    private void doDel(List<Pair<Class, String[]>> delList) {
        UserTemplateDAO userTemplateDAO;
        CompletableFuture.runAsync(()->{
            BaseMapper mapper;
            List<Object> object;
            for (Pair<Class, String[]> pair : delList) {
                mapper = (BaseMapper) ApplicationContextHolder.getBean(pair.getKey());
                mapper.selectList(new QueryWrapper());
            }
        });

    }

    private void initAppCatch() {
        AbstractRobot.registerRobotActiveHandler();
    }

    /**
     * 初始化管理员的自定义配置
     */
    private void initAdminSetting() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.setApplicationContext(applicationContext);
    }


}
