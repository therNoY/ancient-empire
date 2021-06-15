package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * 基础命令实现类
 * @Author mh32736
 * @Date 2020/9/10 17:43
 */
public abstract class AbstractCommand implements Command {

    /**
     * 发送消息类型枚举
     */
    private SendTypeEnum sendTypeEnum;

    private Integer order;

    private Boolean isAsync;

    @Override
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public Boolean isAsync() {
        return isAsync;
    }

    public void setAsync(Boolean async) {
        isAsync = async;
    }

    @Override
    public SendTypeEnum getSendTypeEnum() {
        return sendTypeEnum;
    }

    public void setSendTypeEnum(SendTypeEnum sendTypeEnum) {
        this.sendTypeEnum = sendTypeEnum;
    }

    @Override
    public Command beforeSend(User user) {
        return this;
    }

    protected UserSettingService getUserSettingService(){
        return ApplicationContextHolder.getBean(UserSettingService.class);
    }
}
