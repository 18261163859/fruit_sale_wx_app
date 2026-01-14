package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "商品查询参数")
public class ProductQueryDTO {

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "状态（0-下架 1-上架）")
    private Integer status;

    @Schema(description = "是否推荐（0-否 1-是）")
    private Integer isRecommend;

    @Schema(description = "当前页码", defaultValue = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", defaultValue = "10")
    private Long size = 10L;
}
