package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量生成积分卡DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class IntegralCardBatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 生成数量
     */
    private Integer count;

    /**
     * 积分额度
     */
    private Integer integralAmount;

    /**
     * 过期天数（为空表示永久）
     */
    private Integer expireDays;

    /**
     * 批次备注
     */
    private String batchRemark;
}