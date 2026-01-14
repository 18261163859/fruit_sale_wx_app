package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "创建订单请求参数")
public class CreateOrderDTO {

    @Schema(description = "收货地址ID")
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    @Schema(description = "订单商品列表")
    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemDTO> items;

    @Schema(description = "使用积分数量")
    private Integer useIntegral = 0;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "分享人ID")
    private Long sharerId;

    @Data
    @Schema(description = "订单商品明细")
    public static class OrderItemDTO {

        @Schema(description = "商品ID")
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @Schema(description = "规格ID")
        private Long specId;

        @Schema(description = "购买数量")
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;
    }
}
