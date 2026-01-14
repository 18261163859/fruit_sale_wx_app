package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "订单统计")
public class OrderStatisticsVO {

    @Schema(description = "待付款订单数")
    private Integer pendingPaymentCount;

    @Schema(description = "待发货订单数")
    private Integer pendingShipmentCount;

    @Schema(description = "待收货订单数")
    private Integer shippedCount;

    @Schema(description = "已完成订单数")
    private Integer completedCount;

    @Schema(description = "累计消费金额")
    private BigDecimal totalAmount;

    @Schema(description = "累计返现金额")
    private BigDecimal totalCommission;
}
