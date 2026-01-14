package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 返现申请DTO
 */
@Data
@Schema(description = "返现申请请求")
public class CommissionApplicationDTO {

    @Schema(description = "申请返现金额", required = true)
    private BigDecimal commissionAmount;

    @Schema(description = "银行名称", required = true)
    private String bankName;

    @Schema(description = "银行账号", required = true)
    private String bankAccount;

    @Schema(description = "账户名", required = true)
    private String accountName;

    @Schema(description = "备注")
    private String remark;
}
