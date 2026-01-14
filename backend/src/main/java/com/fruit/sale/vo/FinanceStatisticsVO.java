package com.fruit.sale.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计VO
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Data
public class FinanceStatisticsVO {
    /**
     * 总收入（所有已完成订单的实付金额）
     */
    private BigDecimal totalIncome;

    /**
     * 总返现（所有已结算的返现金额）
     */
    private BigDecimal totalCashback;

    /**
     * 总提现（暂时为0，后续实现提现功能后更新）
     */
    private BigDecimal totalWithdraw;

    /**
     * 净利润（总收入 - 总返现 - 总提现）
     */
    private BigDecimal netProfit;
}
