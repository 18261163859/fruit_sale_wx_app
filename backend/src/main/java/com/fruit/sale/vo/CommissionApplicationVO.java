package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 返现申请VO
 */
@Data
@Schema(description = "返现申请信息")
public class CommissionApplicationVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "代理ID")
    private Long agentId;

    @Schema(description = "代理昵称")
    private String agentNickname;

    @Schema(description = "代理手机号")
    private String agentPhone;

    @Schema(description = "申请返现金额")
    private BigDecimal commissionAmount;

    @Schema(description = "审批状态：0-待审核，1-已通过，2-已拒绝，3-已返现")
    private Integer status;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "审核人ID")
    private Long reviewerId;

    @Schema(description = "审核人昵称")
    private String reviewerNickname;

    @Schema(description = "审核时间")
    private Long reviewTime;

    @Schema(description = "返现时间")
    private Long transferTime;

    @Schema(description = "返现操作人ID")
    private Long transferAdminId;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "账户名")
    private String accountName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "申请时间")
    private Long createTime;
}
