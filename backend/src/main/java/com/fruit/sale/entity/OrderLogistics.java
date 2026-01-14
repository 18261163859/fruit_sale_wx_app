package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单物流实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_logistics")
public class OrderLogistics extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 发货人ID
     */
    private Long shipperId;

    /**
     * 发货时间
     */
    private java.time.LocalDateTime shipTime;

    /**
     * 发货备注
     */
    private String shipRemark;

    /**
     * 包装前照片URL
     */
    private String packageBeforeImage;

    /**
     * 包装后照片URL
     */
    private String packageAfterImage;
}
