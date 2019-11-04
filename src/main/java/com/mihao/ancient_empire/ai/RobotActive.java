package com.mihao.ancient_empire.ai;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.ActiveResult;
import com.mihao.ancient_empire.ai.handle.AiActiveHandle;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.function.Supplier;

/**
 * 任务调度的提交类 工具人
 */
public class RobotActive implements Supplier<ActiveResult> {

    private UserRecord record;
    private AiActiveEnum activeEnum;

    public RobotActive(UserRecord record, AiActiveEnum activeEnum) {
        this.record = record;
        this.activeEnum = activeEnum;
    }

    public RobotActive(String recordId, AiActiveEnum activeEnum) {
        this.record = RobotManger.getRecordCatch(recordId);
        this.activeEnum = activeEnum;
    }

    @Override
    public ActiveResult get() {
        ActiveResult activeResult = AiActiveHandle.getInstance(record, this.activeEnum).getActiveResult(this.record);
        return activeResult;
    }

    public UserRecord getRecord() {
        return record;
    }

    public void setRecord(UserRecord record) {
        this.record = record;
    }


    public AiActiveEnum getActiveEnum() {
        return activeEnum;
    }

    public void setActiveEnum(AiActiveEnum activeEnum) {
        this.activeEnum = activeEnum;
    }
}
