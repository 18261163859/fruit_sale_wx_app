package com.fruit.sale.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据VO
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Data
public class DashboardStatisticsVO {
    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 总销售额
     */
    private BigDecimal totalSales;

    /**
     * 总用户数
     */
    private Integer totalUsers;

    /**
     * 总代理数
     */
    private Integer totalAgents;

    /**
     * 今日订单数
     */
    private Integer todayOrders;

    /**
     * 今日销售额
     */
    private BigDecimal todaySales;

    /**
     * 今日新增用户数
     */
    private Integer todayUsers;
}
