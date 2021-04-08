package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mh32736
 * @Date 2021/4/8 17:51
 */
public class InitUserRecordDTO extends ApiRequestDTO {

    private String recordId;

    /**
     * 游戏类型
     */
    private String gameType;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}
