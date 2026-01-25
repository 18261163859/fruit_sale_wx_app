package com.fruit.sale.vo;

import com.fruit.sale.enums.OrderStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号")
    private String receiverPhone;

    @Schema(description = "收货省份")
    private String receiverProvince;

    @Schema(description = "收货城市")
    private String receiverCity;

    @Schema(description = "收货区县")
    private String receiverDistrict;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "折扣金额")
    private BigDecimal discountAmount;

    @Schema(description = "积分抵扣金额")
    private BigDecimal integralAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "使用积分数量")
    private Integer useIntegral;

    @Schema(description = "订单状态")
    private OrderStatusEnum orderStatus;

    @Schema(description = "快递单号")
    private String expressNo;

    @Schema(description = "快递公司")
    private String expressCompany;

    @Schema(description = "订单商品列表")
    private List<OrderItemVO> items;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "发货备注")
    private String shipRemark;

    @Schema(description = "包装前照片")
    private String packageBeforeImage;

    @Schema(description = "包装后照片")
    private String packageAfterImage;

    @Data
    @Schema(description = "订单商品明细")
    public static class OrderItemVO {

        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品名称")
        private String productName;

        @Schema(description = "商品图片")
        private String productImage;

        @Schema(description = "商品价格（原价）")
        private BigDecimal productPrice;

        @Schema(description = "实际成交价（VIP折扣后）")
        private BigDecimal actualPrice;

        @Schema(description = "购买数量")
        private Integer quantity;

        @Schema(description = "小计金额")
        private BigDecimal subtotalAmount;

        @Schema(description = "规格名称")
        private String specName;
    }
}
