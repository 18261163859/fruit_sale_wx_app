package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务流水记录实体
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_record")
public class FinanceRecord extends BaseEntity {

    /**
     * 流水号
     */
    private String recordNo;

    /**
     * 记录类型：vip_income-会员收入，order_income-订单收入，commission_expense-佣金支出，integral_expense-积分支出，refund_expense-退款支出
     */
    private String recordType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 收支类型：1-收入，2-支出
     */
    private Integer incomeType;

    /**
     * 关联订单号
     */
    private String relatedOrderNo;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 记录日期
     */
    private LocalDate recordDate;
}
