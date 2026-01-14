package com.fruit.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享记录VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class ShareRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享记录ID
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
     * 商品图片
     */
    private String productImage;

    /**
     * 分享链接
     */
    private String shareLink;

    /**
     * 分享渠道
     */
    private Integer shareChannel;

    /**
     * 是否转化（0-否 1-是）
     */
    private Integer isConverted;

    /**
     * 转化用户昵称
     */
    private String convertedUserNickname;

    /**
     * 转化订单编号
     */
    private String convertedOrderNo;

    /**
     * 奖励积分
     */
    private Integer rewardIntegral;

    /**
     * 是否已奖励
     */
    private Integer isRewarded;

    /**
     * 奖励时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rewardTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}