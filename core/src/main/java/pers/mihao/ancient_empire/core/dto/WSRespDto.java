package pers.mihao.ancient_empire.core.dto;

public class WSRespDto {
    private String method;
    private Object value;

    public WSRespDto() {
    }

    public WSRespDto(String method, Object value) {
        this.method = method;
        this.value = value;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
