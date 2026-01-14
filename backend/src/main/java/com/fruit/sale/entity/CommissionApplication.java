package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 返现申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commission_application")
public class CommissionApplication extends BaseEntity {

    /**
     * 代理ID（申请人）
     */
    private Long agentId;

    /**
     * 申请返现金额
     */
    private BigDecimal commissionAmount;

    /**
     * 审批状态：0-待审核，1-已通过，2-已拒绝，3-已返现
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 返现时间
     */
    private LocalDateTime transferTime;

    /**
     * 返现操作人ID
     */
    private Long transferAdminId;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 备注
     */
    private String remark;
}
