package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 积分记录响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "积分记录")
public class IntegralRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "积分变动类型：1-购物获得 2-分享获得 3-兑换卡充值 4-消费抵扣")
    private Integer changeType;

    @Schema(description = "积分变动类型文本")
    private String changeTypeText;

    @Schema(description = "积分变动数量（正数为增加，负数为减少）")
    private Integer changeAmount;

    @Schema(description = "变动后余额")
    private Integer afterBalance;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;
}
