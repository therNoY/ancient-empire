package pers.mihao.ancient_empire.core.dto.ai;

import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;

public class EndTurnResult extends ActiveResult{

    public EndTurnResult(String recordId) {
        super.setRecordId(recordId);
        super.setResultEnum(AiActiveEnum.END_TURN);
    }

}
