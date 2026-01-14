package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fruit.sale.enums.CommissionStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 返现记录实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commission_record")
public class CommissionRecord extends BaseEntity {

    /**
     * 返现记录编号
     */
    private String recordNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 获得返现的代理ID
     */
    @TableField("agent_user_id")
    private Long agentId;

    /**
     * 代理级别（2-一级代理 3-二级代理）
     */
    private Integer agentLevel;

    /**
     * 返现比例
     */
    private BigDecimal commissionRate;

    /**
     * 返现金额
     */
    private BigDecimal commissionAmount;

    /**
     * 结算状态（0-待结算 1-已结算 2-已取消）
     */
    @TableField("settle_status")
    private CommissionStatusEnum status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;
}
