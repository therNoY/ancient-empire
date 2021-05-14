package pers.mihao.ancient_empire.startup.config.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.annotation.ValidatedBean;
import pers.mihao.ancient_empire.common.dto.ApiPageDTO;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;
import pers.mihao.ancient_empire.common.util.ValidateUtil;

/**
 * 验证参数 设置信息 aop
 *
 * @author hspcadmin
 */
@Component
@Aspect
public class ControllerFilterAspect {

    Logger log = LoggerFactory.getLogger(ControllerFilterAspect.class);

    @Pointcut("execution(public * pers.mihao.ancient_empire..*Controller.*(..))")
    public void AllController() {
    }

    @Around("AllController()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        // 补充参数
        setParameter(args);
        // 验证参数
        verificationParameters(args);
        // 执行服务
        return joinPoint.proceed();
    }

    /**
     * 补充参数
     *
     * @param args
     */
    private void setParameter(Object[] args) {

        ApiPageDTO apiPageDTO;
        ApiRequestDTO apiRequestDTO;
        Integer pageSize, pageStart, limitStart;

        for (Object arg : args) {
            if (arg instanceof ApiPageDTO) {
                apiPageDTO = (ApiPageDTO) arg;
                pageSize = apiPageDTO.getPageSize();
                pageStart = apiPageDTO.getPageStart();
                apiPageDTO.setLimitStart((pageStart - 1) * pageSize);
                apiPageDTO.setLimitCount(pageSize);
                apiPageDTO.setUserId(AuthUtil.getUserId());
            } else if (arg instanceof ApiRequestDTO) {
                apiRequestDTO = (ApiRequestDTO) arg;
                apiRequestDTO.setUserId(AuthUtil.getUserId());
            }
        }
    }

    /**
     * 验证参数
     *
     * @param args
     */
    private void verificationParameters(Object[] args) {
        for (Object arg : args) {
            if (arg.getClass().getAnnotationsByType(ValidatedBean.class).length > 0) {
                ValidateUtil.validateBean(arg);
            }
        }
    }
}
