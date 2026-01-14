package com.fruit.sale.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 待付款
     */
    PENDING_PAYMENT(0, "待付款"),

    /**
     * 待发货
     */
    PENDING_SHIPMENT(1, "待发货"),

    /**
     * 已发货
     */
    SHIPPED(2, "已发货"),

    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),

    /**
     * 已取消
     */
    CANCELLED(4, "已取消");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
