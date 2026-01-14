package com.fruit.sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代理统计响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "代理统计信息")
public class AgentStatVO {

    @Schema(description = "代理ID")
    private Long agentId;

    @Schema(description = "代理昵称")
    private String nickname;

    @Schema(description = "代理级别")
    private Integer agentLevel;

    @Schema(description = "累计返现金额")
    private BigDecimal totalCommission;

    @Schema(description = "本月返现金额")
    private BigDecimal monthCommission;

    @Schema(description = "待结算金额")
    private BigDecimal pendingCommission;

    @Schema(description = "可申请返现余额")
    private BigDecimal availableAmount;

    @Schema(description = "下级代理数量")
    private Integer subAgentCount;

    @Schema(description = "下级普通会员数量")
    private Integer subMemberCount;

    @Schema(description = "下级代理列表（仅一级代理）")
    private List<SubAgentVO> subAgents;

    @Data
    @Schema(description = "下级代理信息")
    public static class SubAgentVO {

        @Schema(description = "代理ID")
        private Long agentId;

        @Schema(description = "代理昵称")
        private String nickname;

        @Schema(description = "返现比例")
        private BigDecimal commissionRate;

        @Schema(description = "累计返现")
        private BigDecimal totalCommission;

        @Schema(description = "下级会员数")
        private Integer memberCount;
    }
}
