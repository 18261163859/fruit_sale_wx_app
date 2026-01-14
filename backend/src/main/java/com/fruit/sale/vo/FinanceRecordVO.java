package com.fruit.sale.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录VO
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Data
public class FinanceRecordVO {
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 交易类型：1-订单收入 2-代理返现 3-积分充值 4-提现
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 余额（对于返现类型，这是代理余额）
     */
    private BigDecimal balance;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
