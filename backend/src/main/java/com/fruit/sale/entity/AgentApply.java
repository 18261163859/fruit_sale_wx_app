package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fruit.sale.enums.AgentApplyStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理申请实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_apply")
public class AgentApply extends BaseEntity {

    /**
     * 申请人ID
     */
    private Long userId;

    /**
     * 申请代理级别（2-一级代理 3-二级代理）
     */
    private Integer applyLevel;

    /**
     * 推荐人ID（一级代理推荐二级代理时填写）
     */
    private Long recommenderId;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 返现比例（二级代理申请时由一级代理设定）
     */
    private BigDecimal commissionRate;

    /**
     * 审核状态（0-待审核 1-已通过 2-已拒绝）
     */
    private AgentApplyStatusEnum status;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核备注
     */
    private String reviewRemark;
}
