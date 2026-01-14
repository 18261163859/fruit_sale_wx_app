package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.dto.AgentApplyDTO;
import com.fruit.sale.dto.CommissionApplicationDTO;
import com.fruit.sale.dto.InviteSubAgentDTO;
import com.fruit.sale.dto.ReviewAgentApplicationDTO;
import com.fruit.sale.dto.ReviewCommissionApplicationDTO;
import com.fruit.sale.entity.AgentApply;
import com.fruit.sale.entity.AgentApplication;
import com.fruit.sale.entity.CommissionApplication;
import com.fruit.sale.entity.CommissionRecord;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.enums.AgentApplyStatusEnum;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.enums.UserTypeEnum;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.AgentApplyMapper;
import com.fruit.sale.mapper.AgentApplicationMapper;
import com.fruit.sale.mapper.CommissionApplicationMapper;
import com.fruit.sale.mapper.CommissionRecordMapper;
import com.fruit.sale.mapper.OrderInfoMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IAgentService;
import com.fruit.sale.vo.AgentApplicationVO;
import com.fruit.sale.vo.AgentStatVO;
import com.fruit.sale.vo.AgentTeamVO;
import com.fruit.sale.vo.CommissionApplicationVO;
import com.fruit.sale.vo.CommissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class AgentServiceImpl implements IAgentService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AgentApplyMapper agentApplyMapper;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    @Autowired
    private AgentApplicationMapper agentApplicationMapper;

    @Autowired
    private CommissionApplicationMapper commissionApplicationMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAgent(Long userId, AgentApplyDTO agentApplyDTO) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 必须是星享会员才能申请代理（通过vip_expire_time判断）
        if (user.getVipExpireTime() == null || !user.getVipExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("必须先开通星享会员才能申请代理");
        }

        // 检查是否已经是代理
        if (user.getAgentLevel() != null && user.getAgentLevel() > 0) {
            throw new BusinessException("您已经是代理，无需重复申请");
        }

        // 检查是否有待审核的申请
        LambdaQueryWrapper<AgentApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentApply::getUserId, userId)
                .eq(AgentApply::getStatus, AgentApplyStatusEnum.PENDING);
        AgentApply existApply = agentApplyMapper.selectOne(wrapper);
        if (existApply != null) {
            throw new BusinessException("您有待审核的代理申请，请勿重复提交");
        }

        // 验证推荐人
        if (agentApplyDTO.getRecommenderId() != null) {
            UserInfo recommender = userInfoMapper.selectById(agentApplyDTO.getRecommenderId());
            if (recommender == null || (recommender.getAgentLevel() == null || recommender.getAgentLevel() != 1)) {
                throw new BusinessException("推荐人不存在或不是一级代理");
            }
        }

        // 创建申请记录
        AgentApply apply = new AgentApply();
        apply.setUserId(userId);
        apply.setApplyLevel(agentApplyDTO.getApplyLevel());
        apply.setRecommenderId(agentApplyDTO.getRecommenderId());
        apply.setApplyReason(agentApplyDTO.getApplyReason());
        apply.setCommissionRate(agentApplyDTO.getCommissionRate());
        apply.setStatus(AgentApplyStatusEnum.PENDING);
        agentApplyMapper.insert(apply);
    }

    @Override
    public List<AgentTeamVO> getMyTeam(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<AgentTeamVO> result = new ArrayList<>();

        if (user.getAgentLevel() != null && user.getAgentLevel() == 1) {
            // 一级代理：查看所有二级代理和其下属会员
            List<UserInfo> subAgents = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getParentAgentId, userId)
                            .eq(UserInfo::getAgentLevel, 2)
            );

            for (UserInfo subAgent : subAgents) {
                AgentTeamVO vo = BeanUtil.copyProperties(subAgent, AgentTeamVO.class);
                // 计算该代理的业绩
                BigDecimal totalCommission = calculateTotalCommission(subAgent.getId());
                vo.setTotalCommissionAmount(totalCommission);
                result.add(vo);
            }

            // 查看直属普通会员（非代理用户）
            List<UserInfo> members = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, userId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0))
            );

            for (UserInfo member : members) {
                AgentTeamVO vo = BeanUtil.copyProperties(member, AgentTeamVO.class);
                // 计算该会员的累计消费额
                BigDecimal totalConsume = calculateTotalConsumeAmount(member.getId());
                vo.setTotalConsumeAmount(totalConsume);
                result.add(vo);
            }

        } else if (user.getAgentLevel() != null && user.getAgentLevel() == 2) {
            // 二级代理：查看自己下属的普通会员
            List<UserInfo> members = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, userId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0))
            );

            for (UserInfo member : members) {
                AgentTeamVO vo = BeanUtil.copyProperties(member, AgentTeamVO.class);
                // 计算该会员的累计消费额
                BigDecimal totalConsume = calculateTotalConsumeAmount(member.getId());
                vo.setTotalConsumeAmount(totalConsume);
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<CommissionVO> getCommissionList(Long userId) {
        List<CommissionRecord> records = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, userId)
                        .orderByDesc(CommissionRecord::getCreateTime)
        );

        return records.stream()
                .map(record -> BeanUtil.copyProperties(record, CommissionVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AgentStatVO getStatistics(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        AgentStatVO vo = new AgentStatVO();
        vo.setAgentId(userId);
        vo.setNickname(user.getNickname());

        // 设置代理级别
        if (user.getAgentLevel() != null) {
            vo.setAgentLevel(user.getAgentLevel());
        } else {
            vo.setAgentLevel(0);
        }

        // 累计返现金额
        BigDecimal totalCommission = calculateTotalCommission(userId);
        vo.setTotalCommission(totalCommission);

        // 本月返现金额
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<CommissionRecord> monthRecords = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, userId)
                        .between(CommissionRecord::getCreateTime, monthStart, monthEnd)
        );
        BigDecimal monthCommission = monthRecords.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setMonthCommission(monthCommission);

        // 待结算金额
        List<CommissionRecord> pendingRecords = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, userId)
                        .eq(CommissionRecord::getStatus, 0)
        );
        BigDecimal pendingCommission = pendingRecords.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setPendingCommission(pendingCommission);

        // 计算可用余额 = 累计收益 - 已申请金额（待审核+已通过）
        LambdaQueryWrapper<CommissionApplication> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(CommissionApplication::getAgentId, userId)
                .in(CommissionApplication::getStatus, 0, 1);  // 待审核(0) 和 已通过(1)
        List<CommissionApplication> applications = commissionApplicationMapper.selectList(appWrapper);

        BigDecimal appliedAmount = applications.stream()
                .map(CommissionApplication::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableAmount = totalCommission.subtract(appliedAmount);
        vo.setAvailableAmount(availableAmount.max(BigDecimal.ZERO));  // 确保不为负数

        // 下级统计
        if (user.getAgentLevel() != null && user.getAgentLevel() == 1) {
            int subAgentCount = userInfoMapper.selectCount(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getParentAgentId, userId)
                            .eq(UserInfo::getAgentLevel, 2)
            ).intValue();
            vo.setSubAgentCount(subAgentCount);

            int subMemberCount = userInfoMapper.selectCount(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, userId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0))
            ).intValue();
            vo.setSubMemberCount(subMemberCount);

        } else if (user.getAgentLevel() != null && user.getAgentLevel() == 2) {
            vo.setSubAgentCount(0);
            int subMemberCount = userInfoMapper.selectCount(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, userId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0))
            ).intValue();
            vo.setSubMemberCount(subMemberCount);
        }

        return vo;
    }

    /**
     * 计算总返现金额
     */
    private BigDecimal calculateTotalCommission(Long agentId) {
        List<CommissionRecord> records = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, agentId)
        );
        return records.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算用户累计消费额（只统计已完成的订单）
     */
    private BigDecimal calculateTotalConsumeAmount(Long userId) {
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED)
        );
        return orders.stream()
                .map(OrderInfo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inviteSubAgent(Long inviterId, InviteSubAgentDTO dto) {
        // 1. 验证邀请人是否为一级代理
        UserInfo inviter = userInfoMapper.selectById(inviterId);
        if (inviter == null) {
            throw new BusinessException("用户不存在");
        }
        if (inviter.getAgentLevel() == null || inviter.getAgentLevel() != 1) {
            throw new BusinessException("只有一级代理才能邀请二级代理");
        }

        // 2. 根据手机号查找被邀请人
        LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserInfo::getPhone, dto.getPhone());
        UserInfo invitee = userInfoMapper.selectOne(userWrapper);

        if (invitee == null) {
            throw new BusinessException("该手机号用户不存在，请确认用户已注册");
        }

        // 3. 验证被邀请人是否已是代理
        if (invitee.getAgentLevel() != null && invitee.getAgentLevel() > 0) {
            throw new BusinessException("该用户已经是代理，无法重复邀请");
        }

        // 4. 检查被邀请人是否已有上级代理
        if (invitee.getParentAgentId() != null) {
            throw new BusinessException("该用户已有上级代理，无法重复绑定");
        }

        // 5. 检查是否已有待审核的申请
        LambdaQueryWrapper<AgentApplication> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(AgentApplication::getInviterId, inviterId)
                .eq(AgentApplication::getInviteeId, invitee.getId())
                .eq(AgentApplication::getStatus, 0); // 待审核
        AgentApplication existingApp = agentApplicationMapper.selectOne(appWrapper);
        if (existingApp != null) {
            throw new BusinessException("该用户已有待审核的邀请申请");
        }

        // 6. 验证返现比例
        BigDecimal commissionRate = dto.getCommissionRate();
        if (commissionRate != null) {
            if (commissionRate.compareTo(BigDecimal.ZERO) <= 0
                || commissionRate.compareTo(new BigDecimal("100")) > 0) {
                throw new BusinessException("返现比例必须在0-100之间");
            }
        } else {
            // 默认返现比例
            commissionRate = new BigDecimal("5.00");
        }

        // 7. 创建邀请申请记录（待审核）
        AgentApplication application = new AgentApplication();
        application.setInviterId(inviterId);
        application.setInviteeId(invitee.getId());
        application.setInviteePhone(dto.getPhone());
        application.setCommissionRate(commissionRate);
        application.setStatus(0); // 待审核
        agentApplicationMapper.insert(application);

        log.info("一级代理 {} 提交了邀请用户 {} 成为二级代理的申请，等待审核", inviterId, invitee.getId());
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        String code;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            code = "A" + RandomUtil.randomNumbers(7);
            attempts++;

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getInviteCode, code);
            Long count = userInfoMapper.selectCount(wrapper);

            if (count == 0) {
                return code;
            }

            if (attempts >= maxAttempts) {
                throw new BusinessException("生成邀请码失败，请重试");
            }
        } while (true);
    }

    @Override
    public List<AgentApplicationVO> getApplicationList(Integer status) {
        LambdaQueryWrapper<AgentApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(AgentApplication::getStatus, status);
        }
        wrapper.orderByDesc(AgentApplication::getCreateTime);

        List<AgentApplication> applications = agentApplicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            AgentApplicationVO vo = new AgentApplicationVO();
            vo.setId(app.getId());
            vo.setInviterId(app.getInviterId());
            vo.setInviteeId(app.getInviteeId());
            vo.setInviteePhone(app.getInviteePhone());
            vo.setCommissionRate(app.getCommissionRate());
            vo.setStatus(app.getStatus());
            vo.setRejectReason(app.getRejectReason());
            vo.setReviewerId(app.getReviewerId());
            vo.setReviewTime(app.getReviewTime());
            vo.setCreateTime(app.getCreateTime());

            // 查询邀请人信息
            UserInfo inviter = userInfoMapper.selectById(app.getInviterId());
            if (inviter != null) {
                vo.setInviterNickname(inviter.getNickname());
                vo.setInviterPhone(inviter.getPhone());
            }

            // 查询被邀请人信息
            UserInfo invitee = userInfoMapper.selectById(app.getInviteeId());
            if (invitee != null) {
                vo.setInviteeNickname(invitee.getNickname());
            }

            // 查询审核人信息
            if (app.getReviewerId() != null) {
                UserInfo reviewer = userInfoMapper.selectById(app.getReviewerId());
                if (reviewer != null) {
                    vo.setReviewerNickname(reviewer.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(Long reviewerId, ReviewAgentApplicationDTO dto) {
        // 1. 查询申请记录
        AgentApplication application = agentApplicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("申请记录不存在");
        }

        // 2. 检查申请状态
        if (application.getStatus() != 0) {
            throw new BusinessException("该申请已经被审核过了");
        }

        // 3. 验证审核状态
        if (dto.getStatus() != 1 && dto.getStatus() != 2) {
            throw new BusinessException("审核状态无效");
        }

        // 4. 如果是拒绝，必须填写拒绝原因
        if (dto.getStatus() == 2 && (dto.getRejectReason() == null || dto.getRejectReason().trim().isEmpty())) {
            throw new BusinessException("拒绝时必须填写拒绝原因");
        }

        // 5. 更新申请状态
        application.setStatus(dto.getStatus());
        application.setRejectReason(dto.getRejectReason());
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        agentApplicationMapper.updateById(application);

        // 6. 如果审核通过，升级用户为二级代理
        if (dto.getStatus() == 1) {
            UserInfo invitee = userInfoMapper.selectById(application.getInviteeId());
            if (invitee == null) {
                throw new BusinessException("被邀请人不存在");
            }

            // 再次检查用户状态（防止在审核期间用户状态发生变化）
            if (invitee.getAgentLevel() != null && invitee.getAgentLevel() > 0) {
                throw new BusinessException("该用户已经是代理");
            }

            if (invitee.getParentAgentId() != null) {
                throw new BusinessException("该用户已有上级代理");
            }

            // 升级为二级代理
            invitee.setAgentLevel(2);  // 代理层级为2
            invitee.setParentAgentId(application.getInviterId());  // 设置上级代理
            invitee.setCommissionRate(application.getCommissionRate());

            // 生成邀请码（如果还没有）
            if (invitee.getInviteCode() == null || invitee.getInviteCode().isEmpty()) {
                invitee.setInviteCode(generateInviteCode());
            }

            userInfoMapper.updateById(invitee);

            log.info("审核通过：用户 {} 成功升级为二级代理，上级代理为 {}", invitee.getId(), application.getInviterId());
        } else {
            log.info("审核拒绝：申请ID {} 被拒绝，原因：{}", application.getId(), dto.getRejectReason());
        }
    }

    @Override
    public List<AgentApplicationVO> getMyInviteApplications(Long inviterId) {
        // 查询我发起的邀请申请
        LambdaQueryWrapper<AgentApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentApplication::getInviterId, inviterId);
        wrapper.orderByDesc(AgentApplication::getCreateTime);

        List<AgentApplication> applications = agentApplicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            AgentApplicationVO vo = new AgentApplicationVO();
            vo.setId(app.getId());
            vo.setInviterId(app.getInviterId());
            vo.setInviteeId(app.getInviteeId());
            vo.setInviteePhone(app.getInviteePhone());
            vo.setCommissionRate(app.getCommissionRate());
            vo.setStatus(app.getStatus());
            vo.setRejectReason(app.getRejectReason());
            vo.setReviewerId(app.getReviewerId());
            vo.setReviewTime(app.getReviewTime());
            vo.setCreateTime(app.getCreateTime());

            // 查询被邀请人信息
            UserInfo invitee = userInfoMapper.selectById(app.getInviteeId());
            if (invitee != null) {
                vo.setInviteeNickname(invitee.getNickname());
            }

            // 查询审核人信息
            if (app.getReviewerId() != null) {
                UserInfo reviewer = userInfoMapper.selectById(app.getReviewerId());
                if (reviewer != null) {
                    vo.setReviewerNickname(reviewer.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitCommissionApplication(Long agentId, CommissionApplicationDTO dto) {
        // 验证用户是否是代理
        UserInfo agent = userInfoMapper.selectById(agentId);
        if (agent == null) {
            throw new BusinessException("用户不存在");
        }

        // 通过agentLevel判断是否是代理
        if (agent.getAgentLevel() == null || agent.getAgentLevel() == 0) {
            throw new BusinessException("只有代理才能申请返现");
        }

        // 验证申请金额
        if (dto.getCommissionAmount() == null || dto.getCommissionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("申请金额必须大于0");
        }

        // 校验累计收益是否足够
        BigDecimal totalCommission = agent.getTotalCommissionAmount();
        if (totalCommission == null) {
            totalCommission = BigDecimal.ZERO;
        }

        // 计算已申请的总金额（包括待审核和已通过的）
        LambdaQueryWrapper<CommissionApplication> sumWrapper = new LambdaQueryWrapper<>();
        sumWrapper.eq(CommissionApplication::getAgentId, agentId)
                .in(CommissionApplication::getStatus, 0, 1);  // 待审核(0) 和 已通过(1)
        List<CommissionApplication> approvedApplications = commissionApplicationMapper.selectList(sumWrapper);

        BigDecimal appliedAmount = approvedApplications.stream()
                .map(CommissionApplication::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 可用余额 = 累计收益 - 已申请金额
        BigDecimal availableAmount = totalCommission.subtract(appliedAmount);

        if (dto.getCommissionAmount().compareTo(availableAmount) > 0) {
            throw new BusinessException(String.format("申请金额超过可用余额！累计收益：%.2f元，已申请：%.2f元，可用余额：%.2f元",
                    totalCommission.doubleValue(), appliedAmount.doubleValue(), availableAmount.doubleValue()));
        }

        // 检查是否有待审核的申请
        LambdaQueryWrapper<CommissionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommissionApplication::getAgentId, agentId)
                .eq(CommissionApplication::getStatus, 0);
        CommissionApplication existingApplication = commissionApplicationMapper.selectOne(wrapper);
        if (existingApplication != null) {
            throw new BusinessException("您有待审核的返现申请，请勿重复提交");
        }

        // 创建返现申请记录
        CommissionApplication application = new CommissionApplication();
        application.setAgentId(agentId);
        application.setCommissionAmount(dto.getCommissionAmount());
        application.setBankName(dto.getBankName());
        application.setBankAccount(dto.getBankAccount());
        application.setAccountName(dto.getAccountName());
        application.setRemark(dto.getRemark());
        application.setStatus(0);  // 待审核

        commissionApplicationMapper.insert(application);
        log.info("代理 {} 提交了返现申请，金额：{}，可用余额：{}", agentId, dto.getCommissionAmount(), availableAmount);
    }

    @Override
    public List<CommissionApplicationVO> getMyCommissionApplications(Long agentId) {
        LambdaQueryWrapper<CommissionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommissionApplication::getAgentId, agentId);
        wrapper.orderByDesc(CommissionApplication::getCreateTime);

        List<CommissionApplication> applications = commissionApplicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            CommissionApplicationVO vo = new CommissionApplicationVO();
            vo.setId(app.getId());
            vo.setAgentId(app.getAgentId());
            vo.setCommissionAmount(app.getCommissionAmount());
            vo.setStatus(app.getStatus());
            vo.setRejectReason(app.getRejectReason());
            vo.setReviewerId(app.getReviewerId());
            vo.setBankName(app.getBankName());
            vo.setBankAccount(app.getBankAccount());
            vo.setAccountName(app.getAccountName());
            vo.setRemark(app.getRemark());

            if (app.getReviewTime() != null) {
                vo.setReviewTime(app.getReviewTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }
            if (app.getTransferTime() != null) {
                vo.setTransferTime(app.getTransferTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }
            if (app.getCreateTime() != null) {
                vo.setCreateTime(app.getCreateTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }

            vo.setTransferAdminId(app.getTransferAdminId());

            // 查询审核人信息
            if (app.getReviewerId() != null) {
                UserInfo reviewer = userInfoMapper.selectById(app.getReviewerId());
                if (reviewer != null) {
                    vo.setReviewerNickname(reviewer.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CommissionApplicationVO> getCommissionApplicationList(Integer status) {
        LambdaQueryWrapper<CommissionApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(CommissionApplication::getStatus, status);
        }
        wrapper.orderByDesc(CommissionApplication::getCreateTime);

        List<CommissionApplication> applications = commissionApplicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            CommissionApplicationVO vo = new CommissionApplicationVO();
            vo.setId(app.getId());
            vo.setAgentId(app.getAgentId());
            vo.setCommissionAmount(app.getCommissionAmount());
            vo.setStatus(app.getStatus());
            vo.setRejectReason(app.getRejectReason());
            vo.setReviewerId(app.getReviewerId());
            vo.setBankName(app.getBankName());
            vo.setBankAccount(app.getBankAccount());
            vo.setAccountName(app.getAccountName());
            vo.setRemark(app.getRemark());

            if (app.getReviewTime() != null) {
                vo.setReviewTime(app.getReviewTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }
            if (app.getTransferTime() != null) {
                vo.setTransferTime(app.getTransferTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }
            if (app.getCreateTime() != null) {
                vo.setCreateTime(app.getCreateTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli());
            }

            vo.setTransferAdminId(app.getTransferAdminId());

            // 查询代理信息
            UserInfo agent = userInfoMapper.selectById(app.getAgentId());
            if (agent != null) {
                vo.setAgentNickname(agent.getNickname());
                vo.setAgentPhone(agent.getPhone());
            }

            // 查询审核人信息
            if (app.getReviewerId() != null) {
                UserInfo reviewer = userInfoMapper.selectById(app.getReviewerId());
                if (reviewer != null) {
                    vo.setReviewerNickname(reviewer.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCommissionApplication(Long reviewerId, ReviewCommissionApplicationDTO dto) {
        // 查询申请记录
        CommissionApplication application = commissionApplicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("返现申请不存在");
        }

        // 检查申请状态
        if (application.getStatus() != 0) {
            throw new BusinessException("该申请已被审核，无法重复审核");
        }

        // 验证审批状态
        if (dto.getStatus() != 1 && dto.getStatus() != 2) {
            throw new BusinessException("审批状态参数错误");
        }

        // 如果是拒绝，检查拒绝理由
        if (dto.getStatus() == 2 && (dto.getRejectReason() == null || dto.getRejectReason().trim().isEmpty())) {
            throw new BusinessException("拒绝申请时必须填写拒绝理由");
        }

        // 更新申请记录
        application.setStatus(dto.getStatus());
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        if (dto.getStatus() == 2) {
            application.setRejectReason(dto.getRejectReason());
        }

        commissionApplicationMapper.updateById(application);

        if (dto.getStatus() == 1) {
            log.info("返现申请 {} 审核通过，金额：{}", application.getId(), application.getCommissionAmount());
        } else {
            log.info("返现申请 {} 被拒绝，原因：{}", application.getId(), dto.getRejectReason());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsTransferred(Long adminId, Long applicationId) {
        // 查询申请记录
        CommissionApplication application = commissionApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("返现申请不存在");
        }

        // 检查申请状态，只有已通过的申请才能标记为已返现
        if (application.getStatus() != 1) {
            throw new BusinessException("只有审核通过的申请才能标记为已返现");
        }

        // 更新为已返现状态
        application.setStatus(3);
        application.setTransferTime(LocalDateTime.now());
        application.setTransferAdminId(adminId);

        commissionApplicationMapper.updateById(application);

        log.info("返现申请 {} 已标记为已返现，金额：{}，操作人：{}", applicationId, application.getCommissionAmount(), adminId);
    }
}