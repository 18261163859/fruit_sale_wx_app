package com.fruit.sale.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 代理邀请审核DTO
 *
 * @author fruit-sale
 * @since 2025-10-07
 */
@Data
public class ReviewAgentApplicationDTO {

    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    /**
     * 审核状态：1-通过，2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;

    /**
     * 拒绝原因（拒绝时必填）
     */
    private String rejectReason;
}
