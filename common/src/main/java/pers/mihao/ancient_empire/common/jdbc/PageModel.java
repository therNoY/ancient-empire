package pers.mihao.ancient_empire.common.jdbc;

import java.util.List;

/**
 * 查询分页的基础结果集合
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 10:33
 */
public class PageModel<T> {

    private Integer pageNow;

    private Integer pageSize;

    /**
     * 结果集合
     */
    private List<T> result;

    /**
     * 总数
     */
    private Long count;

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

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
