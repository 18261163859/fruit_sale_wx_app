package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 添加商品DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class ProductAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 商品图片（多张，逗号分隔）
     */
    private String images;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品详情（富文本）
     */
    private String detail;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * VIP价格
     */
    private BigDecimal vipPrice;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否推荐（0-否 1-是）
     */
    private Integer isRecommend;
}