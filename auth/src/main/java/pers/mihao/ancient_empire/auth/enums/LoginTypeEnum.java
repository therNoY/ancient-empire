package pers.mihao.ancient_empire.auth.enums;

/**
 * @Author mh32736
 * @Date 2021/5/23 11:58
 */
public enum  LoginTypeEnum {

    /**
     * PcH5
     */
    PC_H5(1),

    /**
     * 微信小程序
     */
    MP_WE_CHAT(2);

    Integer code;

    LoginTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
