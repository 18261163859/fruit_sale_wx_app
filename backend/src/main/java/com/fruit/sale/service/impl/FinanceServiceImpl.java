package com.fruit.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.common.PageResult;
import com.fruit.sale.entity.CommissionApplication;
import com.fruit.sale.entity.CommissionRecord;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.entity.VipOrder;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.mapper.CommissionApplicationMapper;
import com.fruit.sale.mapper.CommissionRecordMapper;
import com.fruit.sale.mapper.OrderInfoMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.mapper.VipOrderMapper;
import com.fruit.sale.service.IFinanceService;
import com.fruit.sale.vo.FinanceRecordVO;
import com.fruit.sale.vo.FinanceStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务服务实现
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Slf4j
@Service
public class FinanceServiceImpl implements IFinanceService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    @Autowired
    private CommissionApplicationMapper commissionApplicationMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private VipOrderMapper vipOrderMapper;

    @Override
    public FinanceStatisticsVO getStatistics(LocalDate startDate, LocalDate endDate) {
        FinanceStatisticsVO vo = new FinanceStatisticsVO();

        // 构建时间范围条件
        LocalDateTime startDateTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;

        // 总收入：已完成订单的实付金额
        LambdaQueryWrapper<OrderInfo> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED);
        if (startDateTime != null && endDateTime != null) {
            orderWrapper.between(OrderInfo::getCreateTime, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            orderWrapper.ge(OrderInfo::getCreateTime, startDateTime);
        } else if (endDateTime != null) {
            orderWrapper.le(OrderInfo::getCreateTime, endDateTime);
        }

        List<OrderInfo> completedOrders = orderInfoMapper.selectList(orderWrapper);
        BigDecimal orderIncome = completedOrders.stream()
                .map(OrderInfo::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 会员收入：已支付的会员订单
        LambdaQueryWrapper<VipOrder> vipOrderWrapper = new LambdaQueryWrapper<>();
        vipOrderWrapper.eq(VipOrder::getPayStatus, 1);
        if (startDateTime != null && endDateTime != null) {
            vipOrderWrapper.between(VipOrder::getPayTime, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            vipOrderWrapper.ge(VipOrder::getPayTime, startDateTime);
        } else if (endDateTime != null) {
            vipOrderWrapper.le(VipOrder::getPayTime, endDateTime);
        }

        List<VipOrder> vipOrders = vipOrderMapper.selectList(vipOrderWrapper);
        BigDecimal vipIncome = vipOrders.stream()
                .map(VipOrder::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总收入 = 订单收入 + 会员收入
        BigDecimal totalIncome = orderIncome.add(vipIncome);
        vo.setTotalIncome(totalIncome);

        // 总返现：所有已结算的返现金额 + 已通过返现申请的金额
        BigDecimal totalCashback = BigDecimal.ZERO;

        // 1. commission_record中已结算的返现金额
        LambdaQueryWrapper<CommissionRecord> commissionWrapper = new LambdaQueryWrapper<>();
        commissionWrapper.isNotNull(CommissionRecord::getSettleTime);
        if (startDateTime != null && endDateTime != null) {
            commissionWrapper.between(CommissionRecord::getSettleTime, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            commissionWrapper.ge(CommissionRecord::getSettleTime, startDateTime);
        } else if (endDateTime != null) {
            commissionWrapper.le(CommissionRecord::getSettleTime, endDateTime);
        }

        List<CommissionRecord> commissions = commissionRecordMapper.selectList(commissionWrapper);
        for (CommissionRecord commission : commissions) {
            totalCashback = totalCashback.add(commission.getCommissionAmount());
        }

        // 2. commission_application中已通过/已返现的金额
        LambdaQueryWrapper<CommissionApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.in(CommissionApplication::getStatus, 1, 3); // 1-已通过，3-已返现
        if (startDateTime != null && endDateTime != null) {
            applicationWrapper.between(CommissionApplication::getReviewTime, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            applicationWrapper.ge(CommissionApplication::getReviewTime, startDateTime);
        } else if (endDateTime != null) {
            applicationWrapper.le(CommissionApplication::getReviewTime, endDateTime);
        }

        List<CommissionApplication> applications = commissionApplicationMapper.selectList(applicationWrapper);
        for (CommissionApplication application : applications) {
            totalCashback = totalCashback.add(application.getCommissionAmount());
        }

        vo.setTotalCashback(totalCashback);

        // 总提现：暂时为0
        vo.setTotalWithdraw(BigDecimal.ZERO);

        // 净利润
        BigDecimal netProfit = totalIncome.subtract(totalCashback).subtract(vo.getTotalWithdraw());
        vo.setNetProfit(netProfit);

        return vo;
    }

    @Override
    public PageResult<FinanceRecordVO> getRecords(Integer page, Integer pageSize, Integer type,
                                                   LocalDate startDate, LocalDate endDate) {
        List<FinanceRecordVO> allRecords = new ArrayList<>();

        // 构建时间范围条件
        LocalDateTime startDateTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;

        // 如果没有指定类型或类型为1（订单收入），查询订单数据
        if (type == null || type == 1) {
            LambdaQueryWrapper<OrderInfo> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED);
            if (startDateTime != null && endDateTime != null) {
                orderWrapper.between(OrderInfo::getCreateTime, startDateTime, endDateTime);
            } else if (startDateTime != null) {
                orderWrapper.ge(OrderInfo::getCreateTime, startDateTime);
            } else if (endDateTime != null) {
                orderWrapper.le(OrderInfo::getCreateTime, endDateTime);
            }

            List<OrderInfo> orders = orderInfoMapper.selectList(orderWrapper);

            // 批量获取用户信息
            List<Long> userIds = orders.stream().map(OrderInfo::getUserId).distinct().collect(Collectors.toList());
            Map<Long, UserInfo> userMap = userIds.isEmpty() ? Map.of() :
                    userInfoMapper.selectBatchIds(userIds).stream()
                            .collect(Collectors.toMap(UserInfo::getId, u -> u));

            for (OrderInfo order : orders) {
                FinanceRecordVO record = new FinanceRecordVO();
                record.setId(order.getId());
                record.setUserId(order.getUserId());
                UserInfo user = userMap.get(order.getUserId());
                record.setUsername(user != null ? user.getNickname() : "未知用户");
                record.setType(1); // 订单收入
                record.setAmount(order.getActualAmount());
                record.setBalance(null); // 订单收入不涉及余额
                record.setOrderNo(order.getOrderNo());
                record.setRemark("订单收入");
                record.setCreatedAt(order.getCreateTime());
                allRecords.add(record);
            }
        }

        // 如果没有指定类型或类型为2（代理返现），查询返现数据
        if (type == null || type == 2) {
            // 1. 查询commission_record（订单产生的返现记录）
            LambdaQueryWrapper<CommissionRecord> commissionWrapper = new LambdaQueryWrapper<>();
            commissionWrapper.isNotNull(CommissionRecord::getSettleTime);
            if (startDateTime != null && endDateTime != null) {
                commissionWrapper.between(CommissionRecord::getSettleTime, startDateTime, endDateTime);
            } else if (startDateTime != null) {
                commissionWrapper.ge(CommissionRecord::getSettleTime, startDateTime);
            } else if (endDateTime != null) {
                commissionWrapper.le(CommissionRecord::getSettleTime, endDateTime);
            }

            List<CommissionRecord> commissions = commissionRecordMapper.selectList(commissionWrapper);

            // 批量获取代理信息
            List<Long> agentIds = commissions.stream().map(CommissionRecord::getAgentId).distinct().collect(Collectors.toList());

            // 批量获取订单信息
            List<Long> orderIds = commissions.stream().map(CommissionRecord::getOrderId).distinct().collect(Collectors.toList());
            Map<Long, OrderInfo> orderMap = orderIds.isEmpty() ? Map.of() :
                    orderInfoMapper.selectBatchIds(orderIds).stream()
                            .collect(Collectors.toMap(OrderInfo::getId, o -> o));

            for (CommissionRecord commission : commissions) {
                agentIds.add(commission.getAgentId());
            }

            // 2. 查询commission_application（代理申请的返现，状态为已通过或已返现）
            LambdaQueryWrapper<CommissionApplication> applicationWrapper = new LambdaQueryWrapper<>();
            applicationWrapper.in(CommissionApplication::getStatus, 1, 3); // 1-已通过，3-已返现
            if (startDateTime != null && endDateTime != null) {
                applicationWrapper.between(CommissionApplication::getReviewTime, startDateTime, endDateTime);
            } else if (startDateTime != null) {
                applicationWrapper.ge(CommissionApplication::getReviewTime, startDateTime);
            } else if (endDateTime != null) {
                applicationWrapper.le(CommissionApplication::getReviewTime, endDateTime);
            }

            List<CommissionApplication> applications = commissionApplicationMapper.selectList(applicationWrapper);

            // 收集所有代理ID
            for (CommissionApplication app : applications) {
                agentIds.add(app.getAgentId());
            }

            // 批量获取所有代理信息
            Map<Long, UserInfo> agentMap = agentIds.isEmpty() ? Map.of() :
                    userInfoMapper.selectBatchIds(agentIds.stream().distinct().collect(Collectors.toList())).stream()
                            .collect(Collectors.toMap(UserInfo::getId, u -> u));

            // 处理commission_record数据
            for (CommissionRecord commission : commissions) {
                FinanceRecordVO record = new FinanceRecordVO();
                record.setId(commission.getId());
                record.setUserId(commission.getAgentId());
                UserInfo agent = agentMap.get(commission.getAgentId());
                record.setUsername(agent != null ? agent.getNickname() : "未知代理");
                record.setType(2); // 代理返现
                record.setAmount(commission.getCommissionAmount());
                record.setBalance(agent != null ? agent.getTotalCommissionAmount() : BigDecimal.ZERO);
                OrderInfo order = orderMap.get(commission.getOrderId());
                record.setOrderNo(order != null ? order.getOrderNo() : null);
                record.setRemark("订单返现（" + (commission.getAgentLevel() == 1 ? "一级" : "二级") + "）");
                record.setCreatedAt(commission.getSettleTime());
                allRecords.add(record);
            }

            // 处理commission_application数据
            for (CommissionApplication application : applications) {
                FinanceRecordVO record = new FinanceRecordVO();
                record.setId(application.getId());
                record.setUserId(application.getAgentId());
                UserInfo agent = agentMap.get(application.getAgentId());
                record.setUsername(agent != null ? agent.getNickname() : "未知代理");
                record.setType(2); // 代理返现
                record.setAmount(application.getCommissionAmount());
                record.setBalance(agent != null ? agent.getTotalCommissionAmount() : BigDecimal.ZERO);
                record.setOrderNo(null); // 返现申请没有订单号
                String statusText = application.getStatus() == 3 ? "已返现" : "已通过";
                record.setRemark("返现申请（" + statusText + "）");
                record.setCreatedAt(application.getReviewTime() != null ? application.getReviewTime() : application.getCreateTime());
                allRecords.add(record);
            }
        }

        // 如果没有指定类型或类型为3（会员收入），查询会员订单数据
        if (type == null || type == 3) {
            LambdaQueryWrapper<VipOrder> vipOrderWrapper = new LambdaQueryWrapper<>();
            vipOrderWrapper.eq(VipOrder::getPayStatus, 1);
            if (startDateTime != null && endDateTime != null) {
                vipOrderWrapper.between(VipOrder::getPayTime, startDateTime, endDateTime);
            } else if (startDateTime != null) {
                vipOrderWrapper.ge(VipOrder::getPayTime, startDateTime);
            } else if (endDateTime != null) {
                vipOrderWrapper.le(VipOrder::getPayTime, endDateTime);
            }

            List<VipOrder> vipOrders = vipOrderMapper.selectList(vipOrderWrapper);

            // 批量获取用户信息
            List<Long> userIds = vipOrders.stream().map(VipOrder::getUserId).distinct().collect(Collectors.toList());
            Map<Long, UserInfo> userMap = userIds.isEmpty() ? Map.of() :
                    userInfoMapper.selectBatchIds(userIds).stream()
                            .collect(Collectors.toMap(UserInfo::getId, u -> u));

            for (VipOrder vipOrder : vipOrders) {
                FinanceRecordVO record = new FinanceRecordVO();
                record.setId(vipOrder.getId());
                record.setUserId(vipOrder.getUserId());
                UserInfo user = userMap.get(vipOrder.getUserId());
                record.setUsername(user != null ? user.getNickname() : "未知用户");
                record.setType(3); // 会员收入
                record.setAmount(vipOrder.getActualAmount());
                record.setBalance(null); // 会员收入不涉及余额
                record.setOrderNo(vipOrder.getOrderNo());
                record.setRemark("星享会员开通");
                record.setCreatedAt(vipOrder.getPayTime());
                allRecords.add(record);
            }
        }

        // 按创建时间倒序排序
        allRecords.sort(Comparator.comparing(FinanceRecordVO::getCreatedAt).reversed());

        // 分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());
        List<FinanceRecordVO> pageRecords = start < allRecords.size() ?
                allRecords.subList(start, end) : new ArrayList<>();

        return new PageResult<>(pageRecords, (long) allRecords.size(), (long) page, (long) pageSize);
    }
}
