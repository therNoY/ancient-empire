package pers.mihao.ancient_empire.base.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.mihao.ancient_empire.common.dto.ApiPageDTO;

import java.util.List;

/**
 * @version 1.0
 * @author mihao
 * @date 2021\1\14 0014 23:39
 */
public class IPageHelper {



    public static <T> IPage<T> toPage(List<T> list, ApiPageDTO apiPageDTO) {
        IPage<T> page = new Page<T>();
        page.setTotal(apiPageDTO.getPageCount());
        page.setRecords(list);
        return page;
    }
}
