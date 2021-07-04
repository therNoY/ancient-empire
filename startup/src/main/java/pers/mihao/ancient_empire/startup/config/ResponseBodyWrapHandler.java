package pers.mihao.ancient_empire.startup.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * @Author mihao
 * @Date 2021/5/14 23:05
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return delegate.supportsReturnType(methodParameter);
    }

    /**
     * 包装处理返回值
     * @param o
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @throws Exception
     */
    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter,
        ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        Object packageObj;
        if (o instanceof RespJson) {
            packageObj = o;
        } else if (o instanceof IPage) {
            IPage page = (IPage) o;
            packageObj = RespUtil.successPageResJson(page);
        } else {
            packageObj = RespUtil.successResJson(o);
        }
        delegate.handleReturnValue(packageObj, methodParameter, modelAndViewContainer, nativeWebRequest);
    }

}
