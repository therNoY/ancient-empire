package pers.mihao.ancient_empire.core.manger.commend;

/**
 * @Author mh32736
 * @Date 2020/9/10 17:43
 */
public abstract class AbstractCommend implements Commend{

    protected String taskType;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
