package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shopping_cart")
public class ShoppingCart extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

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

    /**
     * 是否选中（0-否 1-是）
     */
    private Integer isSelected;
}