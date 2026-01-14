package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分卡实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("integral_card")
public class IntegralCard extends BaseEntity {

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 兑换码（16位）
     */
    private String cardCode;

    /**
     * 积分额度
     */
    private Integer integralAmount;

    /**
     * 卡状态：0-未使用，1-已使用，2-已过期
     */
    private Integer cardStatus;

    /**
     * 使用用户ID
     */
    @TableField("use_user_id")
    private Long useUserId;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 批次号
     */
    private String batchNo;
}
