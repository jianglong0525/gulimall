package com.guli.common.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SkuFullReductionTo {
    private Long skuId;
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    private Integer addOther;
}
