package pers.mihao.ancient_empire.startup.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.mihao.ancient_empire.common.util.RespHelper;
import pers.mihao.ancient_empire.common.vo.MyException;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * 配置捕获全局异常
 */
@ControllerAdvice
public class MyControllerAdvice {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    /*@ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {
        Map map = new HashMap();
        map.put("code", 100);
        map.put("msg", ex.getMessage());
        return map;
    }*/

    /**
     * 拦截捕捉自定义异常 MyException.class
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public RespJson myErrorHandler(MyException ex) {
        log.error("", ex);
        return RespHelper.errResJson(ex.getMessage());
    }

}