package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;

public class EndTurnResult extends ActiveResult{

    public EndTurnResult(String recordId) {
        super.setRecordId(recordId);
        super.setResultEnum(AiActiveEnum.END_TURN);
    }

}
