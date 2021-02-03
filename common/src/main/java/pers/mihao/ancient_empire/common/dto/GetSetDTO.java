package pers.mihao.ancient_empire.common.dto;

import java.lang.reflect.Method;

/**
 * 用于反射的get setDTO
 * @version 1.0
 * @author mihao
 * @date 2020\10\13 0013 21:50
 */
public class GetSetDTO {

    /**
     * get 方法
     */
    private Method get;
    /**
     * set方法
     */
    private Method set;

    public GetSetDTO() {
    }

    public GetSetDTO(Method get, Method set) {
        this.get = get;
        this.set = set;
    }

    public Method getGet() {
        return get;
    }

    public void setGet(Method get) {
        this.get = get;
    }

    public Method getSet() {
        return set;
    }

    public void setSet(Method set) {
        this.set = set;
    }
}
