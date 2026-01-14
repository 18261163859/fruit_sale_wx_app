package com.fruit.sale.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理邀请申请VO
 *
 * @author fruit-sale
 * @since 2025-10-07
 */
@Data
public class AgentApplicationVO {

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 邀请人昵称
     */
    private String inviterNickname;

    /**
     * 邀请人手机号
     */
    private String inviterPhone;

    /**
     * 被邀请人ID
     */
    private Long inviteeId;

    /**
     * 被邀请人昵称
     */
    private String inviteeNickname;

    /**
     * 被邀请人手机号
     */
    private String inviteePhone;

    /**
     * 返现比例
     */
    private BigDecimal commissionRate;

    /**
     * 审核状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核人昵称
     */
    private String reviewerNickname;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 申请时间
     */
    private LocalDateTime createTime;
}
