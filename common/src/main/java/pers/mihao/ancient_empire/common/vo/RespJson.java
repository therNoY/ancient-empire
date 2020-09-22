package pers.mihao.ancient_empire.common.vo;

public class RespJson {
    private Integer resCode;
    private String resMes;
    private Object resVal;

    public RespJson() {
    }

    public RespJson(Integer resCode, String resMes) {
        this.resCode = resCode;
        this.resMes = resMes;
    }

    public RespJson(Integer resCode, String resMes, Object resVal) {
        this.resCode = resCode;
        this.resMes = resMes;
        this.resVal = resVal;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getResMes() {
        return resMes;
    }

    public void setResMes(String resMes) {
        this.resMes = resMes;
    }

    public Object getResVal() {
        return resVal;
    }

    public void setResVal(Object resVal) {
        this.resVal = resVal;
    }

    @Override
    public String toString() {
        return "RespJson{" +
                "resCode=" + resCode +
                ", resMes='" + resMes + '\'' +
                ", resVal=" + resVal +
                '}';
    }
}
