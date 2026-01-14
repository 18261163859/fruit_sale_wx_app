package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发货 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "发货请求参数")
public class ShipOrderDTO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "快递公司")
    @NotBlank(message = "快递公司不能为空")
    private String expressCompany;

    @Schema(description = "快递单号")
    @NotBlank(message = "快递单号不能为空")
    private String expressNo;

    @Schema(description = "包装前照片URL")
    private String packageBeforeImage;

    @Schema(description = "包装后照片URL")
    private String packageAfterImage;

    @Schema(description = "发货备注")
    private String shipRemark;
}
