package pers.mihao.ancient_empire.common.dto;

/**
 * 请求分页信息
 * @version 1.0
 * @auther mihao
 * @date 2020\12\1 0001 22:21
 */
public class ApiPageDTO extends ApiRequestDTO{

    /**
     * 分页
     */
    private Integer pageStart = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;

    /**
     * mysql分页的limit start
     */
    private Integer limitStart;


    /**
     * mysql分页的size
     */
    private Integer limitCount;

    /**
     * 分页最大数
     */
    private Long pageCount;

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart = limitStart;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Long getPageCount() {
        return pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }
}
