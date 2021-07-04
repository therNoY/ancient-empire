package pers.mihao.ancient_empire.common.log;

import java.util.Arrays;
import pers.mihao.ancient_empire.common.log.enums.LogFieldEnum;
import pers.mihao.ancient_empire.common.log.invoke.PersistentLogInvoke;
import pers.mihao.ancient_empire.common.log.invoke.PersistentLogToDataBaseInvoke;

/**
 * @Author mh32736
 * @Date 2021/6/30 9:57
 */
public abstract class AbstractLog {

    private String LogId;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 触发用户
     */
    private Integer triggerUserId;

    /**
     * 类型
     */
    private String serviceName;

    /**
     * 调用方法
     */
    private String invokeMethod;

    /**
     * 表名
     */
    protected String tableName;

    /**
     * 数据源
     */
    private String dataSource;

    /**
     * 执行时间
     */
    private Long execTime;

    /**
     * 是否异步
     */
    private Boolean isAsync;

    /**
     * 是否开启下划线
     */
    private Boolean isUnderscore;

    private Object[] args;
    private Object res;

    /**
     * 是否转下划线
     *
     * @return
     */
    public boolean isUnderscore() {
        return isUnderscore;
    }

    public void setUnderscore(Boolean underscore) {
        isUnderscore = underscore;
    }

    /**
     * 是否异步
     *
     * @return
     */
    public boolean isAsync() {
        return isAsync;
    }


    public void setAsync(Boolean async) {
        isAsync = async;
    }

    public String getLogId() {
        return LogId;
    }

    public void setLogId(String logId) {
        LogId = logId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getTriggerUserId() {
        return triggerUserId;
    }

    public void setTriggerUserId(Integer triggerUserId) {
        this.triggerUserId = triggerUserId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }


    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }

    @Override
    public String toString() {
        return "AbstractLog{" +
            ", createTime='" + createTime + '\'' +
            ", triggerUserId=" + triggerUserId +
            ", invokeMethod='" + invokeMethod + '\'' +
            ", execTime=" + execTime +
            '}';
    }

    /**
     * 日志表的名字
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 支持的默认扩展字段 默认都需要
     *
     * @return
     */
    public LogFieldEnum[] getExtLogField() {
        return Arrays.stream(LogFieldEnum.values()).filter(LogFieldEnum::isDefaultAdd).toArray(LogFieldEnum[]::new);
    }

    /**
     * 扩展日志解析类
     *
     * @return
     */
    public Class<? extends LogParseHandle> getExtendLogParseHandle() {
        return DefaultLogParseHandle.class;
    }

    /**
     * 获取持久化日志的执行器
     *
     * @return
     */
    public Class<? extends PersistentLogInvoke> getInvoke() {
        return PersistentLogToDataBaseInvoke.class;
    }

}
