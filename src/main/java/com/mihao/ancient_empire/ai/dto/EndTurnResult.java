package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;

public class EndTurnResult extends ActiveResult{

    public EndTurnResult(String recordId) {
        super.setRecordId(recordId);
        super.setResultEnum(AiActiveEnum.END_TURN);
    }

}
