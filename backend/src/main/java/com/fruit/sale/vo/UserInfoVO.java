package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "用户信息")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatarUrl;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户类型")
    private Integer userType;

    @Schema(description = "代理等级：0-非代理，1-一级代理，2-二级代理")
    private Integer agentLevel;

    @Schema(description = "星享会员过期时间")
    private String vipExpireTime;

    @Schema(description = "积分余额")
    private Integer integralBalance;

    @Schema(description = "返现比例")
    private BigDecimal commissionRate;

    @Schema(description = "上级代理ID")
    private Long parentAgentId;

    @Schema(description = "推荐人ID")
    private Long inviterUserId;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "是否为发货人员：0-否，1-是")
    private Integer isShipper;
}
