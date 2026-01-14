package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_info")
public class UserInfo extends BaseEntity {

    /**
     * 用户编号
     */
    private String userNo;

    /**
     * 手机号
     */
    @TableField(value = "phone", insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.ALWAYS)
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 微信OpenID
     */
    private String openid;

    /**
     * 微信UnionID
     */
    private String unionid;

    /**
     * 用户类型：1-普通会员，2-星享会员，3-一级代理，4-二级代理
     */
    private Integer userType;

    /**
     * 星享会员过期时间
     */
    private LocalDateTime vipExpireTime;

    /**
     * 积分余额
     */
    private Integer integralBalance;

    /**
     * 累计消费金额
     */
    private BigDecimal totalConsumeAmount;

    /**
     * 累计返现金额
     */
    private BigDecimal totalCommissionAmount;

    /**
     * 邀请码（代理专用）
     */
    private String inviteCode;

    /**
     * 邀请人用户ID
     */
    @TableField("inviter_user_id")
    private Long inviterUserId;

    /**
     * 代理层级：1-一级代理，2-二级代理
     */
    private Integer agentLevel;

    /**
     * 上级代理用户ID
     */
    private Long parentAgentId;

    /**
     * 返现比例（百分比）
     */
    private BigDecimal commissionRate;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 是否为发货人员：0-否，1-是
     */
    private Integer isShipper;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
