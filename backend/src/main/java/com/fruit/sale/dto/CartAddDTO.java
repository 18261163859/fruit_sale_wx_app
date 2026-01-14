package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加购物车DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class CartAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 规格信息（兼容旧数据）
     */
    private String specInfo;
}