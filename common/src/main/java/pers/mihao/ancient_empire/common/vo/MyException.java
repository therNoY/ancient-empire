package pers.mihao.ancient_empire.common.vo;

public class MyException extends RuntimeException{
    private Integer code;
    private String mes;

    public MyException(Integer code) {
        this.code = code;
    }


    public MyException(String mes) {
        super(mes);
        this.mes = mes;
    }

    public MyException() {
        this.mes = "请联系管理员";
    }

    public Integer getCode() {
        return code;
    }

    public String getMes() {
        return mes;
    }
}
