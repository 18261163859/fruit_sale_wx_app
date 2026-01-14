package com.fruit.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录实体
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@TableName("integral_record")
public class IntegralRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水编号
     */
    private String recordNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分变动类型（1-充值 2-消费获得 3-分享奖励 4-抵扣消费 5-管理员调整）
     */
    private Integer changeType;

    /**
     * 积分变动数量（正数为增加，负数为减少）
     */
    private Integer changeAmount;

    /**
     * 变动前余额
     */
    private Integer balanceBefore;

    /**
     * 变动后余额
     */
    private Integer balanceAfter;

    /**
     * 关联订单编号（消费获得或抵扣时）
     */
    private String relatedOrderNo;

    /**
     * 关联充值卡编号（充值时）
     */
    private String relatedCardNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
