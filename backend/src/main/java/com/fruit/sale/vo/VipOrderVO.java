package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 星享会员订单 VO
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Data
@Schema(description = "星享会员订单信息")
public class VipOrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "会员价格")
    private BigDecimal vipPrice;

    @Schema(description = "实付金额")
    private BigDecimal actualAmount;

    @Schema(description = "订单状态：0-待支付，1-已支付，2-已取消")
    private Integer orderStatus;

    @Schema(description = "订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "支付状态：0-未支付，1-已支付")
    private Integer payStatus;

    @Schema(description = "支付状态描述")
    private String payStatusDesc;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "交易流水号")
    private String transactionId;

    @Schema(description = "会员时长（天）")
    private Integer vipDuration;

    @Schema(description = "会员生效时间")
    private LocalDateTime vipStartTime;

    @Schema(description = "会员结束时间")
    private LocalDateTime vipEndTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
