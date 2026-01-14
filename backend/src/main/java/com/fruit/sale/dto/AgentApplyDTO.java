package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理申请 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "代理申请请求参数")
public class AgentApplyDTO {

    @Schema(description = "申请人ID")
    @NotNull(message = "申请人ID不能为空")
    private Long userId;

    @Schema(description = "申请代理级别（2-一级代理 3-二级代理）")
    @NotNull(message = "申请级别不能为空")
    private Integer applyLevel;

    @Schema(description = "推荐人ID（二级代理必填）")
    private Long recommenderId;

    @Schema(description = "返现比例（二级代理申请时由一级代理设定）")
    private BigDecimal commissionRate;

    @Schema(description = "申请理由")
    private String applyReason;
}
