package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新购物车DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class CartUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品数量
     */
    private Integer quantity;
}