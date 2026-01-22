package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品信息实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_info")
public class ProductInfo extends BaseEntity {

    /**
     * 商品编号
     */
    private String productNo;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * VIP价格
     */
    private BigDecimal vipPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0-下架 1-上架）
     */
    private Integer status;

    /**
     * 是否推荐（0-否 1-是）
     */
    private Integer isRecommend;

    /**
     * 商品详情（富文本）
     */
    private String productDetail;
}
