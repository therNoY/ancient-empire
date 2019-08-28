package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.dto.ws_dto.WSRespDto;

public class WsRespHelper {

    public static WSRespDto init(String method, Object value) {
        return new WSRespDto(method, value);
    }
}
