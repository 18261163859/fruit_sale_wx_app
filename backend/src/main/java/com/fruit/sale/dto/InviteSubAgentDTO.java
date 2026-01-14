package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 邀请二级代理 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "邀请二级代理请求参数")
public class InviteSubAgentDTO {

    @Schema(description = "被邀请人手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "返现比例（百分比，如5表示5%）")
    private BigDecimal commissionRate;
}
