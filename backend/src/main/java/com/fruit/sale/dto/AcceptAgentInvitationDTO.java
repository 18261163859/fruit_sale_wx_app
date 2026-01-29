package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 接受代理邀请 DTO（被邀请者使用）
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "接受代理邀请请求参数")
public class AcceptAgentInvitationDTO {

    @Schema(description = "邀请人的邀请码")
    @NotBlank(message = "邀请码不能为空")
    private String agentInviteCode;

    @Schema(description = "返现比例（百分比）")
    private BigDecimal commissionRate;
}
