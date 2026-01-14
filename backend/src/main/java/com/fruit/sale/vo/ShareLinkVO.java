package com.fruit.sale.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分享链接VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class ShareLinkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享记录ID
     */
    private Long shareId;

    /**
     * 分享链接
     */
    private String shareLink;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;
}