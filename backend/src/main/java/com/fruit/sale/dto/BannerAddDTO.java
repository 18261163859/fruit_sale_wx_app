package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 轮播图添加DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class BannerAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 轮播图图片URL
     */
    private String imageUrl;

    /**
     * 视频URL（轮播类型为视频时使用）
     */
    private String videoUrl;

    /**
     * 轮播类型：1-图片，2-视频
     */
    private Integer bannerType;

    /**
     * 跳转类型（1-商品详情 2-分类页 3-外部链接 4-不跳转）
     */
    private Integer linkType;

    /**
     * 跳转链接或商品ID
     */
    private String linkValue;

    /**
     * 排序
     */
    private Integer sort;
}