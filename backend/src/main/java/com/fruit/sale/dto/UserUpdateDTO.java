package com.fruit.sale.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {
    private String nickname;
    private String phone;
    private Integer userType;
    private Integer agentLevel;
    private Long parentAgentId;
    private BigDecimal commissionRate;
    private Integer integralBalance;
    private LocalDateTime vipExpireTime;
    private Integer status;
    private Integer isShipper;
}
