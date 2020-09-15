package pers.mihao.ancient_empire.startup.config.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pers.mihao.ancient_empire.common.util.RespUtil;

/**
 * 验证参数的aop
 */
@Component
@Aspect
public class BindingResultAspect {

    @Pointcut("execution(public * pers.mihao.ancient_empire.*.controller.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    FieldError fieldError = result.getFieldError();
                    if(fieldError!=null){
                        return RespUtil.parsErrResJson(fieldError.getDefaultMessage());
                    }else{
                        return RespUtil.parsErrResJson();
                    }
                }
            }
        }
        return joinPoint.proceed();
    }
}
