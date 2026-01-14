package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理申请实体
 *
 * @author fruit-sale
 * @since 2025-10-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_application")
public class AgentApplication extends BaseEntity {

    /**
     * 邀请人ID（一级代理）
     */
    private Long inviterId;

    /**
     * 被邀请人ID
     */
    private Long inviteeId;

    /**
     * 被邀请人手机号
     */
    private String inviteePhone;

    /**
     * 返现比例
     */
    private BigDecimal commissionRate;

    /**
     * 审核状态：0-待审核，1-已通过，2-已拒绝
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
}
