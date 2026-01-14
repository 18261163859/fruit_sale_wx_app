package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品规格实体类
 *
 * @author fruit-sale
 * @since 2025-10-03
 */
@Data
@TableName("product_spec")
public class ProductSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格名称（如1斤装、5斤装）
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
     * 排序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;
}
