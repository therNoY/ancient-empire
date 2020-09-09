package pers.mihao.ancient_empire.core;

import java.util.function.Supplier;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.core.dto.ai.ActiveResult;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;
import pers.mihao.ancient_empire.core.handel.ai.AiActiveHandle;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

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
        this.record = GameCoreManger.getRecordCatch(recordId);
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
