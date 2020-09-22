package pers.mihao.ancient_empire.common.util;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

/**
 * 以静态变量保存Spring ApplicationContext 方便在不将单例类交给Spring 管理时使用被Spring 管理的类
 */
public class ApplicationContextHolder {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.如果有多个Bean符合Class, 取出第一个.
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        Map beanMaps = applicationContext.getBeansOfType(clazz);
        if (beanMaps != null && !beanMaps.isEmpty()) {
            return (T) beanMaps.values().iterator().next();
        } else {
            return null;
        }
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new AncientEmpireException("applicationContext未注入");
        }
    }
}