package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "商品信息")
public class ProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "商品主图")
    private String mainImage;

    @Schema(description = "商品图片列表")
    private String[] images;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品详情")
    private String detail;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "VIP价格")
    private BigDecimal vipPrice;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "销量")
    private Integer salesCount;

    @Schema(description = "状态（0-下架 1-上架）")
    private Integer status;

    @Schema(description = "是否推荐")
    private Integer isRecommend;
}
