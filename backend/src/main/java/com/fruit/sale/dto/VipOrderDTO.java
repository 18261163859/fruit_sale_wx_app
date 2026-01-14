package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 会员订单支付 DTO
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Data
@Schema(description = "会员订单支付参数")
public class VipOrderDTO {

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "支付方式：wechat-微信，alipay-支付宝")
    @NotBlank(message = "支付方式不能为空")
    private String payType;
}
