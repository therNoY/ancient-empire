package pers.mihao.ancient_empire.core.manger.command;

/**
 * 基础命令实现类
 * @Author mh32736
 * @Date 2020/9/10 17:43
 */
public abstract class AbstractCommand implements Command {

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
}
