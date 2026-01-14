package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 返现申请审批DTO
 */
@Data
@Schema(description = "返现申请审批请求")
public class ReviewCommissionApplicationDTO {

    @Schema(description = "申请ID", required = true)
    private Long applicationId;

    @Schema(description = "审批状态：1-通过，2-拒绝", required = true)
    private Integer status;

    @Schema(description = "拒绝原因（status=2时必填）")
    private String rejectReason;
}
