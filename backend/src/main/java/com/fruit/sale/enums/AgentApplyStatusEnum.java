package com.fruit.sale.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 代理申请状态枚举
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Getter
public enum AgentApplyStatusEnum {

    /**
     * 待审核
     */
    PENDING(0, "待审核"),

    /**
     * 已通过
     */
    APPROVED(1, "已通过"),

    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    AgentApplyStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AgentApplyStatusEnum getByCode(Integer code) {
        for (AgentApplyStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
