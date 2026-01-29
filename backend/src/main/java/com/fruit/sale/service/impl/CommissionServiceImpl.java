package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.sale.common.PageResult;
import com.fruit.sale.entity.CommissionRecord;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.enums.CommissionStatusEnum;
import com.fruit.sale.mapper.CommissionRecordMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.ICommissionService;
import com.fruit.sale.service.IUserService;
import com.fruit.sale.vo.CommissionRecordVO;
import com.fruit.sale.vo.CommissionStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 返现服务实现
 *
 * 返现规则说明（参考pd.md）：
 * 1. 一级代理返现：
 *    - 一级代理自己消费，获得返现（如10%）
 *    - 其下属的二级代理及二级代理下属普通会员和星享会员消费，一级代理也获得返现
 *
 * 2. 二级代理返现：
 *    - 当二级代理自己消费或其名下普通会员及星享会员消费时，一级代理和二级代理都会获得返现
 *    - 例如：一级代理A返现比例10%，二级代理B分成比例10%，当B名下会员C消费100元时：
 *      - 先按10%计算返现总额10元
 *      - 二级代理B获得 10 * 10% = 1元
 *      - 一级代理A获得 10 - 1 = 9元
 *
 * 3. 积分抵扣规则：
 *    - 返现基于实付金额（actualAmount = totalAmount - integralDeductAmount）
 *    - 如果用户使用积分抵扣了部分金额，代理的返现会相应减少
 *
 * @author fruit-sale
 * @since 2025-10-06
 */
@Slf4j
@Service
public class CommissionServiceImpl implements ICommissionService {

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settleCommission(OrderInfo order) {
        if (order == null) {
            log.warn("订单为空，无法结算返现");
            return;
        }

        // 返现基于实付金额（actualAmount），已扣除积分抵扣
        log.info("结算订单{}的返现，实付金额：{}，积分使用：{}",
                order.getOrderNo(), order.getActualAmount(), order.getIntegralUsed());

        UserInfo buyer = userService.getById(order.getUserId());
        if (buyer == null) {
            log.warn("购买用户不存在，userId={}", order.getUserId());
            return;
        }

        log.info("开始结算订单{}的返现，购买用户代理等级：{}", order.getOrderNo(), buyer.getAgentLevel());

        // 根据购买者的代理等级决定返现逻辑
        Integer agentLevel = buyer.getAgentLevel() != null ? buyer.getAgentLevel() : 0;

        if (agentLevel == 1) {
            // 一级代理自己消费，自己获得返现
            settleLevelOneAgentSelfConsume(order, buyer);
        } else if (agentLevel == 2) {
            // 二级代理消费，一级和二级都获得返现
            settleLevelTwoAgentConsume(order, buyer);
        } else {
            // 普通会员或星享会员消费，查找上级代理
            settleMemberConsume(order, buyer);
        }
    }

    /**
     * 一级代理自己消费的返现结算
     */
    private void settleLevelOneAgentSelfConsume(OrderInfo order, UserInfo agent) {
        if (agent.getCommissionRate() == null || agent.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("一级代理{}的返现比例未设置或为0", agent.getId());
            return;
        }

        // 一级代理自己消费，获得全部返现
        BigDecimal commissionAmount = calculateCommissionAmount(order.getActualAmount(), agent.getCommissionRate());
        createCommissionRecord(order, agent.getId(), 1, // 一级代理
                agent.getCommissionRate(), commissionAmount);

        log.info("一级代理{}自己消费，返现金额：{}", agent.getId(), commissionAmount);
    }

    /**
     * 二级代理消费的返现结算
     */
    private void settleLevelTwoAgentConsume(OrderInfo order, UserInfo level2Agent) {
        if (level2Agent.getParentAgentId() == null) {
            log.warn("二级代理{}的上级代理ID为空", level2Agent.getId());
            return;
        }

        UserInfo level1Agent = userService.getById(level2Agent.getParentAgentId());
        if (level1Agent == null) {
            log.warn("二级代理{}的上级代理不存在", level2Agent.getId());
            return;
        }

        if (level1Agent.getCommissionRate() == null || level1Agent.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("一级代理{}的返现比例未设置或为0", level1Agent.getId());
            return;
        }

        // 计算总返现额 = 订单金额 * 一级代理返现比例
        BigDecimal totalCommission = calculateCommissionAmount(order.getActualAmount(), level1Agent.getCommissionRate());

        // 计算二级代理分成
        BigDecimal level2Commission = BigDecimal.ZERO;
        BigDecimal level1Commission = totalCommission;

        if (level2Agent.getCommissionRate() != null && level2Agent.getCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
            // 二级代理的分成 = 总返现额 * (二级代理分成比例 / 100)
            level2Commission = calculateCommissionAmount(totalCommission, level2Agent.getCommissionRate());
            // 一级代理获得 = 总返现额 - 二级代理分成
            level1Commission = totalCommission.subtract(level2Commission);
        }

        // 创建一级代理返现记录
        createCommissionRecord(order, level1Agent.getId(), 1, // 一级代理
                level1Agent.getCommissionRate(), level1Commission);

        // 创建二级代理返现记录
        if (level2Commission.compareTo(BigDecimal.ZERO) > 0) {
            createCommissionRecord(order, level2Agent.getId(), 2, // 二级代理
                    level2Agent.getCommissionRate(), level2Commission);
        }

        log.info("二级代理{}消费，一级代理{}返现：{}，二级代理返现：{}",
                level2Agent.getId(), level1Agent.getId(), level1Commission, level2Commission);
    }

    /**
     * 普通会员或星享会员消费的返现结算
     */
    private void settleMemberConsume(OrderInfo order, UserInfo member) {
        // 查找邀请人（上级代理）
        if (member.getInviterUserId() == null) {
            log.info("用户{}没有邀请人，无需返现", member.getId());
            return;
        }

        UserInfo inviter = userService.getById(member.getInviterUserId());
        if (inviter == null) {
            log.warn("用户{}的邀请人不存在", member.getId());
            return;
        }

        Integer inviterAgentLevel = inviter.getAgentLevel() != null ? inviter.getAgentLevel() : 0;

        if (inviterAgentLevel == 2) {
            // 邀请人是二级代理，需要同时给一级代理和二级代理返现
            if (inviter.getParentAgentId() == null) {
                log.warn("二级代理{}的上级代理ID为空", inviter.getId());
                return;
            }

            UserInfo level1Agent = userService.getById(inviter.getParentAgentId());
            if (level1Agent == null || level1Agent.getCommissionRate() == null) {
                log.warn("一级代理不存在或返现比例未设置");
                return;
            }

            // 计算总返现额
            BigDecimal totalCommission = calculateCommissionAmount(order.getActualAmount(), level1Agent.getCommissionRate());

            // 计算二级代理分成
            BigDecimal level2Commission = BigDecimal.ZERO;
            BigDecimal level1Commission = totalCommission;

            if (inviter.getCommissionRate() != null && inviter.getCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
                // 二级代理分成 = 总返现额 * (二级代理分成比例 / 100)
                level2Commission = calculateCommissionAmount(totalCommission, inviter.getCommissionRate());
                level1Commission = totalCommission.subtract(level2Commission);
            }

            // 创建返现记录
            createCommissionRecord(order, level1Agent.getId(), 1, // 一级代理
                    level1Agent.getCommissionRate(), level1Commission);

            if (level2Commission.compareTo(BigDecimal.ZERO) > 0) {
                createCommissionRecord(order, inviter.getId(), 2, // 二级代理
                        inviter.getCommissionRate(), level2Commission);
            }

            log.info("会员{}消费，一级代理{}返现：{}，二级代理{}返现：{}",
                    member.getId(), level1Agent.getId(), level1Commission, inviter.getId(), level2Commission);

        } else if (inviterAgentLevel == 1) {
            // 邀请人是一级代理，只给一级代理返现
            if (inviter.getCommissionRate() == null || inviter.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("一级代理{}的返现比例未设置或为0", inviter.getId());
                return;
            }

            BigDecimal commissionAmount = calculateCommissionAmount(order.getActualAmount(), inviter.getCommissionRate());
            createCommissionRecord(order, inviter.getId(), 1, // 一级代理
                    inviter.getCommissionRate(), commissionAmount);

            log.info("会员{}消费，一级代理{}返现：{}", member.getId(), inviter.getId(), commissionAmount);
        } else {
            log.info("用户{}的邀请人不是代理，无需返现", member.getId());
        }
    }

    /**
     * 计算返现金额
     *
     * @param amount 订单金额
     * @param rate   返现比例（支持小数形式0.10或百分比形式10，都表示10%）
     * @return 返现金额
     */
    private BigDecimal calculateCommissionAmount(BigDecimal amount, BigDecimal rate) {
        // 如果比例 <= 1，认为是小数形式（0.10 = 10%），直接相乘
        // 如果比例 > 1，认为是百分比形式（10 = 10%），需要除以100
        if (rate.compareTo(BigDecimal.ONE) <= 0) {
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        } else {
            return amount.multiply(rate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
    }

    /**
     * 创建返现记录
     *
     * @param order           订单信息
     * @param agentId         代理用户ID
     * @param agentLevel      代理级别（1-一级代理，2-二级代理）
     * @param rate            返现比例
     * @param commissionAmount 返现金额
     */
    private void createCommissionRecord(OrderInfo order, Long agentId, Integer agentLevel,
                                        BigDecimal rate, BigDecimal commissionAmount) {
        CommissionRecord record = new CommissionRecord();
        record.setRecordNo("COMM" + System.currentTimeMillis() + agentId);
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setOrderAmount(order.getActualAmount());
        record.setAgentId(agentId);
        record.setAgentLevel(agentLevel);
        record.setCommissionRate(rate);
        record.setCommissionAmount(commissionAmount);
        record.setStatus(CommissionStatusEnum.SETTLED);
        record.setSettleTime(LocalDateTime.now());
        commissionRecordMapper.insert(record);

        // 更新代理的累计返现金额
        UserInfo agent = userService.getById(agentId);
        if (agent != null) {
            BigDecimal currentTotal = agent.getTotalCommissionAmount() != null
                    ? agent.getTotalCommissionAmount() : BigDecimal.ZERO;
            agent.setTotalCommissionAmount(currentTotal.add(commissionAmount));
            userInfoMapper.updateById(agent);
        }

        log.info("创建返现记录成功：订单{}，代理{}，金额{}", order.getOrderNo(), agentId, commissionAmount);
    }

    @Override
    public PageResult<CommissionRecordVO> getCommissionRecords(Long agentId, Long current, Long size) {
        Page<CommissionRecord> page = new Page<>(current, size);

        LambdaQueryWrapper<CommissionRecord> commissionRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(agentId>0) {
            commissionRecordLambdaQueryWrapper.eq(CommissionRecord::getAgentId, agentId);
        }
        Page<CommissionRecord> recordPage = commissionRecordMapper.selectPage(page,
                commissionRecordLambdaQueryWrapper.orderByDesc(CommissionRecord::getCreateTime));

        List<CommissionRecordVO> voList = recordPage.getRecords().stream()
                .map(record -> BeanUtil.copyProperties(record, CommissionRecordVO.class))
                .collect(Collectors.toList());

        return PageResult.build(voList, recordPage.getTotal(),
                recordPage.getCurrent(), recordPage.getSize());
    }

    @Override
    public CommissionStatisticsVO getCommissionStatistics(Long agentId) {
        UserInfo agent = userService.getById(agentId);
        if (agent == null) {
            return new CommissionStatisticsVO();
        }

        CommissionStatisticsVO vo = new CommissionStatisticsVO();
        vo.setTotalCommissionAmount(agent.getTotalCommissionAmount() != null
                ? agent.getTotalCommissionAmount() : BigDecimal.ZERO);

        // 统计已结算的返现总额
        List<CommissionRecord> settledRecords = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, agentId)
                        .eq(CommissionRecord::getStatus, CommissionStatusEnum.SETTLED));

        BigDecimal settledAmount = settledRecords.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setSettledAmount(settledAmount);

        // 统计返现笔数
        Long recordCount = commissionRecordMapper.selectCount(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, agentId));
        vo.setRecordCount(recordCount != null ? recordCount.intValue() : 0);

        return vo;
    }

    @Override
    public PageResult<CommissionRecordVO> getSubordinateCommissions(Long agentId, Long current, Long size) {
        UserInfo agent = userService.getById(agentId);
        if (agent == null) {
            return PageResult.build(List.of(), 0L, current, size);
        }

        // 查询下级用户ID列表
        List<Long> subordinateIds;
        Integer agentLevel = agent.getAgentLevel() != null ? agent.getAgentLevel() : 0;

        if (agentLevel == 1) {
            // 一级代理：查询所有以其为parentAgentId的二级代理，以及所有以其为inviterUserId的普通会员
            List<UserInfo> level2Agents = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getParentAgentId, agentId));

            List<UserInfo> directMembers = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, agentId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0)));

            subordinateIds = level2Agents.stream().map(UserInfo::getId).collect(Collectors.toList());
            subordinateIds.addAll(directMembers.stream().map(UserInfo::getId).collect(Collectors.toList()));

        } else if (agentLevel == 2) {
            // 二级代理：查询所有以其为inviterUserId的普通会员
            subordinateIds = userInfoMapper.selectList(
                    new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviterUserId, agentId)
                            .and(w -> w.isNull(UserInfo::getAgentLevel).or().eq(UserInfo::getAgentLevel, 0)))
                    .stream().map(UserInfo::getId).collect(Collectors.toList());
        } else {
            // 非代理，无下级
            return PageResult.build(List.of(), 0L, current, size);
        }

        if (subordinateIds.isEmpty()) {
            return PageResult.build(List.of(), 0L, current, size);
        }

        // 查询这些下级用户相关的订单的返现记录
        Page<CommissionRecord> page = new Page<>(current, size);
        Page<CommissionRecord> recordPage = commissionRecordMapper.selectPage(page,
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getAgentId, agentId)
                        .orderByDesc(CommissionRecord::getCreateTime));

        List<CommissionRecordVO> voList = recordPage.getRecords().stream()
                .map(record -> BeanUtil.copyProperties(record, CommissionRecordVO.class))
                .collect(Collectors.toList());

        return PageResult.build(voList, recordPage.getTotal(),
                recordPage.getCurrent(), recordPage.getSize());
    }
}
