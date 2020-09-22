package pers.mihao.ancient_empire.common.util;


import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.HashMap;
import java.util.Map;
import pers.mihao.ancient_empire.common.config.ErrorCode;
import pers.mihao.ancient_empire.common.vo.RespJson;

public class RespUtil {

    /**
     * 返回默认的操作成功
     * @return
     */
    public static RespJson successResJson () {
        return new RespJson(0,"ok");
    }

    /**
     * 返回有信息的结果
     * @param object
     * @return
     */
    public static RespJson successResJson (Object object) {
        return new RespJson(0,"ok",object);
    }

    /**
     * 返回两个信息的结果
     * @param object
     * @return
     */
    public static RespJson successResJson (String key1, Object object, String key2, Object object2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key1, object);
        map.put(key2, object2);
        return new RespJson(0,"ok",map);
    }

    /**
     * 返回有信息的结果
     * @param page
     * @return
     */
    public static RespJson successPageResJson (IPage page) {
        Map map = new HashMap();
        map.put("pageCount", page.getTotal());
        map.put("data", page.getRecords());
        return new RespJson(0,"ok",map);
    }

    /**
     * 返回有错误码和错误原因的结果
     * @param mes
     * @return
     */
    public static RespJson error() {
        return error(500);
    }

    /**
     * 返回有错误码和错误原因的结果
     * @param mes
     * @return
     */
    public static RespJson error(String mes) {
        return new RespJson(-1, mes);
    }

    /**
     * 返回有错误码和错误原因的结果
     * @param errCode
     * @return
     */
    public static RespJson error(Integer errCode) {
        return new RespJson(errCode, ErrorCode.getErrorMes(errCode));
    }

    /**
     * 参数错误的统一返回类
     * @return
     */
    public static RespJson parsErrResJson (String mess) {
        return new RespJson(40010, mess);
    }

    /**
     * 参数错误的统一返回类
     * @return
     */
    public static RespJson parsErrResJson () {
        return new RespJson(40010, "参数错误");
    }
}
