package pers.mihao.ancient_empire.common.dto;

import java.lang.reflect.Method;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\13 0013 21:50
 */
public class GetSetDTO {
    private Method get;
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
