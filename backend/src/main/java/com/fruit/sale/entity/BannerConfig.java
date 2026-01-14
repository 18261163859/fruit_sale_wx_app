package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图配置实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("banner_config")
public class BannerConfig extends BaseEntity {

    /**
     * 轮播图标题
     */
    @TableField("banner_title")
    private String title;

    /**
     * 轮播图图片
     */
    @TableField("banner_image")
    private String imageUrl;

    /**
     * 视频URL（当banner_type=2时使用）
     */
    @TableField("video_url")
    private String videoUrl;

    /**
     * 轮播图类型（1-图片 2-视频）
     */
    @TableField("banner_type")
    private Integer bannerType;

    /**
     * 跳转链接
     */
    @TableField("link_url")
    private String linkUrl;

    /**
     * 跳转类型（1-商品详情 2-分类页 3-外部链接）
     */
    @TableField("link_type")
    private Integer linkType;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sort;

    /**
     * 状态（0-禁用 1-启用）
     */
    private Integer status;
}
