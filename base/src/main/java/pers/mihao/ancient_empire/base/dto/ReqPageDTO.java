package pers.mihao.ancient_empire.base.dto;

/**
 * 基础分页请求
 */
public class ReqPageDTO {

    private Integer pageNow;

    private Integer pageSize;


    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
