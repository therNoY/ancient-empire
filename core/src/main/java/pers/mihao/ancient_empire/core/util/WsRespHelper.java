package pers.mihao.ancient_empire.core.util;

import com.mihao.ancient_empire.common.config.ErrorCode;
import pers.mihao.ancient_empire.common.constant.WsMethodEnum;
import pers.mihao.ancient_empire.common.bo.ws_dto.WSRespDto;

/**
 * 帮助构建 WS 统一返回值
 */
public class WsRespHelper {

    public static WSRespDto success(String method, Object value) {
        return new WSRespDto(method, value);
    }

    public static WSRespDto error(Integer errCode) {
        String codeMes = ErrorCode.getErrorMes(errCode);
        return new WSRespDto(WsMethodEnum.ERROR.type(), codeMes);
    }
}
