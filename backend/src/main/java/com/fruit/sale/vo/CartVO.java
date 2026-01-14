package com.fruit.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class CartVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 星享会员价格
     */
    private BigDecimal vipPrice;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格信息（兼容旧数据）
     */
    private String specInfo;

    /**
     * 是否选中（0-否 1-是）
     */
    private Integer isSelected;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品状态（0-下架 1-上架）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}