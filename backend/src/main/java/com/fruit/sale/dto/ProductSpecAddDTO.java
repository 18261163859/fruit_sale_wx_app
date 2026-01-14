package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 添加商品规格DTO
 *
 * @author fruit-sale
 * @since 2025-10-03
 */
@Data
public class ProductSpecAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格价格
     */
    private BigDecimal price;

    /**
     * VIP价格
     */
    private BigDecimal vipPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 排序
     */
    private Integer sortOrder;
}
