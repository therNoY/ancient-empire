package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.dto.ws_dto.WSRespDto;

/**
 * 帮助构建 WS 统一返回值
 */
public class WsRespHelper {

    public static WSRespDto success(String method, Object value) {
        return new WSRespDto(method, value);
    }
}
