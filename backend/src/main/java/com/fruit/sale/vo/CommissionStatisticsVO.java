package com.fruit.sale.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 返现统计VO
 *
 * @author fruit-sale
 * @since 2025-10-06
 */
@Data
public class CommissionStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 累计返现总额
     */
    private BigDecimal totalCommissionAmount;

    /**
     * 已结算返现金额
     */
    private BigDecimal settledAmount;

    /**
     * 待结算返现金额
     */
    private BigDecimal pendingAmount;

    /**
     * 返现记录数
     */
    private Integer recordCount;

    public CommissionStatisticsVO() {
        this.totalCommissionAmount = BigDecimal.ZERO;
        this.settledAmount = BigDecimal.ZERO;
        this.pendingAmount = BigDecimal.ZERO;
        this.recordCount = 0;
    }
}
