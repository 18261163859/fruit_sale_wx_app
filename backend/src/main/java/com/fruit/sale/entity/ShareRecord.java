package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分享记录实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("share_record")
public class ShareRecord extends BaseEntity {

    /**
     * 分享人ID
     */
    private Long sharerId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 访问人ID（可为空，未注册用户）
     */
    private Long visitorId;

    /**
     * 是否成交（0-否 1-是）
     */
    private Integer isDeal;

    /**
     * 成交订单ID
     */
    private Long orderId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 获得积分（订单金额的5%）
     */
    private Integer rewardIntegral;
}
