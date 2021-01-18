package pers.mihao.ancient_empire.startup.config.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.annotation.ValidatedBean;
import pers.mihao.ancient_empire.common.dto.ApiPageDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.util.ValidateUtil;

/**
 * 验证参数的aop
 * @author hspcadmin
 */
@Component
@Aspect
public class BindingResultAspect {

    @Pointcut("execution(public * pers.mihao.ancient_empire..*Controller.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        ApiPageDTO apiPageDTO;
        ApiRequestDTO apiRequestDTO;
        Integer pageSize, pageStart, limitStart;
        for (Object arg : args) {
            if (arg.getClass().getAnnotationsByType(ValidatedBean.class).length > 0) {
                try {
                    ValidateUtil.validateBean(arg);
                } catch (Exception e) {
                    return RespUtil.parsErrResJson(e.getMessage());
                }
            }else if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    FieldError fieldError = result.getFieldError();
                    if(fieldError!=null){
                        return RespUtil.parsErrResJson(fieldError.getDefaultMessage());
                    }else{
                        return RespUtil.parsErrResJson();
                    }
                }
            }else if (arg instanceof ApiPageDTO) {
                apiPageDTO = (ApiPageDTO) arg;
                pageSize = apiPageDTO.getPageSize();
                pageStart = apiPageDTO.getPageStart();
                apiPageDTO.setLimitStart((pageStart - 1) * pageSize);
                apiPageDTO.setLimitCount(pageSize);
                apiPageDTO.setUserId(AuthUtil.getUserId());
            }else if (arg instanceof ApiRequestDTO) {
                apiRequestDTO = (ApiRequestDTO) arg;
                apiRequestDTO.setUserId(AuthUtil.getUserId());
            }
        }
        return joinPoint.proceed();
    }
}
