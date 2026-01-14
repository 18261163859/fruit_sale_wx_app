package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建分享DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class ShareCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 分享渠道（1-微信好友 2-微信群 3-复制链接）
     */
    private Integer shareChannel;
}