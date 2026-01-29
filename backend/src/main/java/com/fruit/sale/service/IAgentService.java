package com.fruit.sale.service;

import com.fruit.sale.dto.AcceptAgentInvitationDTO;
import com.fruit.sale.dto.AgentApplyDTO;
import com.fruit.sale.dto.CommissionApplicationDTO;
import com.fruit.sale.dto.InviteSubAgentDTO;
import com.fruit.sale.dto.ReviewAgentApplicationDTO;
import com.fruit.sale.dto.ReviewCommissionApplicationDTO;
import com.fruit.sale.vo.AgentApplicationVO;
import com.fruit.sale.vo.AgentStatVO;
import com.fruit.sale.vo.AgentTeamVO;
import com.fruit.sale.vo.CommissionApplicationVO;
import com.fruit.sale.vo.CommissionVO;

import java.util.List;

/**
 * 代理服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IAgentService {

    /**
     * 申请成为二级代理
     */
    void applyAgent(Long userId, AgentApplyDTO agentApplyDTO);

    /**
     * 获取我的团队
     */
    List<AgentTeamVO> getMyTeam(Long userId);

    /**
     * 获取返现记录列表
     */
    List<CommissionVO> getCommissionList(Long userId);

    /**
     * 获取代理统计数据
     */
    AgentStatVO getStatistics(Long userId);

    /**
     * 一级代理邀请二级代理
     */
    void inviteSubAgent(Long inviterId, InviteSubAgentDTO dto);

    /**
     * 被邀请者接受代理邀请（通过分享链接）
     */
    void acceptAgentInvitation(Long inviteeId, AcceptAgentInvitationDTO dto);

    /**
     * 获取代理邀请申请列表（后台管理）
     */
    List<AgentApplicationVO> getApplicationList(Integer status);

    /**
     * 审核代理邀请申请（后台管理）
     */
    void reviewApplication(Long reviewerId, ReviewAgentApplicationDTO dto);

    /**
     * 获取我发起的邀请申请列表（小程序端）
     */
    List<AgentApplicationVO> getMyInviteApplications(Long inviterId);

    /**
     * 提交返现申请
     */
    void submitCommissionApplication(Long agentId, CommissionApplicationDTO dto);

    /**
     * 获取我的返现申请列表（小程序端）
     */
    List<CommissionApplicationVO> getMyCommissionApplications(Long agentId);

    /**
     * 获取返现申请列表（后台管理）
     */
    List<CommissionApplicationVO> getCommissionApplicationList(Integer status);

    /**
     * 审核返现申请（后台管理）
     */
    void reviewCommissionApplication(Long reviewerId, ReviewCommissionApplicationDTO dto);

    /**
     * 标记返现申请为已返现（后台管理）
     */
    void markAsTransferred(Long adminId, Long applicationId);
}