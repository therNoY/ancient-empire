package pers.mihao.ancient_empire.startup.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * 配置捕获全局异常
 * @author mihao
 */
@ControllerAdvice
public class MyControllerAdvice {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 拦截捕捉自定义异常 Throwable.class
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public RespJson allErrorHandler(Exception ex) {
        log.error("", ex);
        if (ex instanceof AeException) {
            AeException e = (AeException) ex;
            return RespUtil.error(e.getCode(), e.getMes());
        }
        return RespUtil.error(ex.getMessage());
    }

}