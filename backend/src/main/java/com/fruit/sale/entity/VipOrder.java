package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 星享会员开通订单实体
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vip_order")
public class VipOrder extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会员价格
     */
    private BigDecimal vipPrice;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消
     */
    private Integer orderStatus;

    /**
     * 支付状态：0-未支付，1-已支付
     */
    private Integer payStatus;

    /**
     * 支付方式：wechat-微信，alipay-支付宝
     */
    private String payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 会员时长（天）
     */
    private Integer vipDuration;

    /**
     * 会员生效时间
     */
    private LocalDateTime vipStartTime;

    /**
     * 会员结束时间
     */
    private LocalDateTime vipEndTime;
}
