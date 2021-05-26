package pers.mihao.ancient_empire.common.vo;

import pers.mihao.ancient_empire.common.constant.CommonConstant;

/**
 * 自定义错误类型
 *
 * @author mihao
 */
public class AeException extends RuntimeException {

    private Integer code;
    private String mes;


    public AeException(Exception e) {
        super(e);
    }

    public AeException(Integer code) {
        super("错误码： " + code);
        this.code = code;
    }


    public AeException(String mes) {
        super(mes);
        this.mes = mes;
    }

    public AeException(Integer code, String mes) {
        this.code = code;
        this.mes = mes;
    }

    public AeException() {
        this.mes = CommonConstant.DEFAULT_ERROR;
    }

    public Integer getCode() {
        return code;
    }

    public String getMes() {
        return mes;
    }
}
