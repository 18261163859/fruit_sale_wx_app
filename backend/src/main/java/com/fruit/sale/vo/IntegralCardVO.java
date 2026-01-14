package com.fruit.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分卡VO（包含使用用户信息）
 *
 * @author fruit-sale
 * @since 2024-10-01
 */
@Data
public class IntegralCardVO {

    /**
     * ID
     */
    private Long id;

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
    private Long useUserId;

    /**
     * 使用用户名称（关联查询）
     */
    private String username;

    /**
     * 使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDeleted;
}
