package com.fruit.sale.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售趋势VO
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Data
public class SalesTrendVO {
    /**
     * 日期
     */
    private String date;

    /**
     * 销售额
     */
    private BigDecimal amount;

    /**
     * 订单数
     */
    private Integer count;
}
