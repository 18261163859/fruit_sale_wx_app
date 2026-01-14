package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设置用户为代理DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class SetAgentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 代理级别（2-一级代理 3-二级代理）
     */
    private Integer agentLevel;

    /**
     * 返现比例
     */
    private java.math.BigDecimal commissionRate;

    /**
     * 上级代理ID（二级代理必填）
     */
    private Long parentAgentId;
}