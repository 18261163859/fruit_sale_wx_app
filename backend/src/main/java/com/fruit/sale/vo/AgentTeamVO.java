package com.fruit.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理团队成员VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class AgentTeamVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 代理等级：0-非代理，1-一级代理，2-二级代理
     */
    private Integer agentLevel;

    /**
     * 累计消费金额
     */
    private BigDecimal totalConsumeAmount;

    /**
     * 累计返现金额
     */
    private BigDecimal totalCommissionAmount;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}