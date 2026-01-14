package com.fruit.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 返现记录VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class CommissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 返现比例
     */
    private BigDecimal commissionRate;

    /**
     * 返现金额
     */
    private BigDecimal commissionAmount;

    /**
     * 结算状态
     */
    private Integer status;

    /**
     * 结算时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settleTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;
}