package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fruit.sale.enums.OrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_info")
public class OrderInfo extends BaseEntity {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 折扣金额（星享会员95折）
     */
    private BigDecimal discountAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralDeductAmount;

    /**
     * 使用积分数量
     */
    private Integer integralUsed;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 订单状态（0-待付款 1-待发货 2-已发货 3-已完成 4-已取消）
     */
    private OrderStatusEnum orderStatus;

    /**
     * 支付状态（0-未支付 1-已支付）
     */
    private Integer payStatus;

    /**
     * 支付方式（1-微信支付 2-支付宝）
     */
    private Integer payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 第三方支付交易号
     */
    private String transactionId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 收货省份
     */
    private String receiverProvince;

    /**
     * 收货城市
     */
    private String receiverCity;

    /**
     * 收货区/县
     */
    private String receiverDistrict;

    /**
     * 收货详细地址
     */
    private String receiverAddress;

    /**
     * 配送方式
     */
    private Integer deliveryType;

    /**
     * 买家备注
     */
    private String buyerRemark;

    /**
     * 佣金是否已结算
     */
    private Integer isCommissionSettled;

    /**
     * 佣金结算时间
     */
    private LocalDateTime commissionSettleTime;
}
