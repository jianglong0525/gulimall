package com.guli.search.vo;
import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String key;//全文检索关键字
    private Long catalog3Id;
    /**
     * saleCount_asc/desc
     * skuPrice_asc/desc
     * hotScore_asc/desc
     */
    private String sort;//排序条件
    private Integer hasStock;//是否有存货 0无货 1有货
    private String skuPrice;//价格区间查询 1_500 _500 500_
    private List<Long> brandId;
    private List<String> attrs;//属性筛选
    private Integer pageNum;//页数
    private String _queryString;
}
