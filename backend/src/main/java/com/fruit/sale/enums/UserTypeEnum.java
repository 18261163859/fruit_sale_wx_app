package com.fruit.sale.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户类型枚举
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Getter
public enum UserTypeEnum {

    /**
     * 普通会员
     */
    NORMAL(1, "普通会员"),

    /**
     * 星享会员
     */
    STAR(2, "星享会员"),

    /**
     * 一级代理（废弃，请使用agentLevel字段）
     */
    @Deprecated
    AGENT_LEVEL_1(3, "一级代理"),

    /**
     * 二级代理（废弃，请使用agentLevel字段）
     */
    @Deprecated
    AGENT_LEVEL_2(4, "二级代理");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    UserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserTypeEnum getByCode(Integer code) {
        for (UserTypeEnum userType : values()) {
            if (userType.getCode().equals(code)) {
                return userType;
            }
        }
        return null;
    }
}
