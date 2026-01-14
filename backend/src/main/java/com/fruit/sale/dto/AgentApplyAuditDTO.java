package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 审批代理申请DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class AgentApplyAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    private Long applyId;

    /**
     * 审批状态（1-通过 2-拒绝）
     */
    private Integer status;

    /**
     * 审批备注
     */
    private String reviewRemark;
}