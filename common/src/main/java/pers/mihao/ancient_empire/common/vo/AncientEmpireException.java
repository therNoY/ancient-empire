package pers.mihao.ancient_empire.common.vo;

import pers.mihao.ancient_empire.common.constant.BaseConstant;

/**
 * 自定义错误类型
 */
public class AncientEmpireException extends RuntimeException{
    private Integer code;
    private String mes;

    public AncientEmpireException(Integer code) {
        this.code = code;
    }


    public AncientEmpireException(String mes) {
        super(mes);
        this.mes = mes;
    }

    public AncientEmpireException() {
        this.mes = BaseConstant.DEFAULT_ERROR;
    }

    public Integer getCode() {
        return code;
    }

    public String getMes() {
        return mes;
    }
}
