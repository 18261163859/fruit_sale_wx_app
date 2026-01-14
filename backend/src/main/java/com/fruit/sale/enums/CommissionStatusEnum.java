package com.fruit.sale.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 返现状态枚举
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Getter
public enum CommissionStatusEnum {

    /**
     * 待结算
     */
    PENDING(0, "待结算"),

    /**
     * 已结算
     */
    SETTLED(1, "已结算"),

    /**
     * 已取消
     */
    CANCELLED(2, "已取消");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    CommissionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CommissionStatusEnum getByCode(Integer code) {
        for (CommissionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
