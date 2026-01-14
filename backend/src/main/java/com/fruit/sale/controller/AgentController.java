package com.fruit.sale.controller;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.Result;
import com.fruit.sale.dto.AgentApplyDTO;
import com.fruit.sale.dto.CommissionApplicationDTO;
import com.fruit.sale.dto.InviteSubAgentDTO;
import com.fruit.sale.service.IAgentService;
import com.fruit.sale.service.ICommissionService;
import com.fruit.sale.vo.AgentStatVO;
import com.fruit.sale.vo.AgentTeamVO;
import com.fruit.sale.vo.CommissionApplicationVO;
import com.fruit.sale.vo.CommissionRecordVO;
import com.fruit.sale.vo.CommissionStatisticsVO;
import com.fruit.sale.vo.CommissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代理管理控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "代理管理", description = "代理相关接口")
@RestController
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private IAgentService agentService;

    @Autowired
    private ICommissionService commissionService;

    @Operation(summary = "申请成为二级代理", description = "星享会员可申请成为二级代理")
    @PostMapping("/apply")
    public Result<String> applyAgent(HttpServletRequest request, @RequestBody AgentApplyDTO agentApplyDTO) {
        Long userId = (Long) request.getAttribute("userId");
        agentApplyDTO.setUserId(userId);
        agentService.applyAgent(userId, agentApplyDTO);
        return Result.success("申请已提交，等待审核");
    }

    @Operation(summary = "获取我的团队", description = "获取我的下级代理和会员列表")
    @GetMapping("/my-team")
    public Result<List<AgentTeamVO>> getMyTeam(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<AgentTeamVO> team = agentService.getMyTeam(userId);
        return Result.success(team);
    }

    @Operation(summary = "获取返现记录", description = "获取我的返现记录列表")
    @GetMapping("/commission/list")
    public Result<List<CommissionVO>> getCommissionList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CommissionVO> commissionList = agentService.getCommissionList(userId);
        return Result.success(commissionList);
    }

    @Operation(summary = "获取返现记录（分页）", description = "分页查询我的返现记录")
    @GetMapping("/commission/records")
    public Result<PageResult<CommissionRecordVO>> getCommissionRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<CommissionRecordVO> records = commissionService.getCommissionRecords(userId, current, size);
        return Result.success(records);
    }

    @Operation(summary = "获取返现统计", description = "获取我的返现统计信息")
    @GetMapping("/commission/statistics")
    public Result<CommissionStatisticsVO> getCommissionStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CommissionStatisticsVO statistics = commissionService.getCommissionStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取下级返现记录", description = "查看下级用户带来的返现记录")
    @GetMapping("/commission/subordinate")
    public Result<PageResult<CommissionRecordVO>> getSubordinateCommissions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<CommissionRecordVO> records = commissionService.getSubordinateCommissions(userId, current, size);
        return Result.success(records);
    }

    @Operation(summary = "获取代理统计数据", description = "获取代理的统计信息")
    @GetMapping("/statistics")
    public Result<AgentStatVO> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        AgentStatVO statistics = agentService.getStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "一级代理邀请二级代理", description = "一级代理通过手机号邀请用户成为二级代理")
    @PostMapping("/invite-sub-agent")
    public Result<String> inviteSubAgent(HttpServletRequest request, @Valid @RequestBody InviteSubAgentDTO dto) {
        Long inviterId = (Long) request.getAttribute("userId");
        agentService.inviteSubAgent(inviterId, dto);
        return Result.success("邀请申请已提交，请等待审核");
    }

    @Operation(summary = "获取我发起的邀请申请列表", description = "查看我发起的邀请二级代理申请记录")
    @GetMapping("/invite-applications")
    public Result<List<com.fruit.sale.vo.AgentApplicationVO>> getMyInviteApplications(HttpServletRequest request) {
        Long inviterId = (Long) request.getAttribute("userId");
        List<com.fruit.sale.vo.AgentApplicationVO> applications = agentService.getMyInviteApplications(inviterId);
        return Result.success(applications);
    }

    @Operation(summary = "提交返现申请", description = "代理提交返现申请")
    @PostMapping("/commission-application")
    public Result<String> submitCommissionApplication(HttpServletRequest request, @Valid @RequestBody CommissionApplicationDTO dto) {
        Long agentId = (Long) request.getAttribute("userId");
        agentService.submitCommissionApplication(agentId, dto);
        return Result.success("返现申请已提交，请等待审核");
    }

    @Operation(summary = "获取我的返现申请列表", description = "查看我提交的返现申请记录")
    @GetMapping("/commission-applications")
    public Result<List<CommissionApplicationVO>> getMyCommissionApplications(HttpServletRequest request) {
        Long agentId = (Long) request.getAttribute("userId");
        List<CommissionApplicationVO> applications = agentService.getMyCommissionApplications(agentId);
        return Result.success(applications);
    }
}