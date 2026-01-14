package com.fruit.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.mapper.OrderInfoMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IDashboardService;
import com.fruit.sale.service.IOrderService;
import com.fruit.sale.vo.DashboardStatisticsVO;
import com.fruit.sale.vo.OrderVO;
import com.fruit.sale.vo.SalesTrendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台首页服务实现
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Slf4j
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IOrderService orderService;

    @Override
    public DashboardStatisticsVO getStatistics() {
        DashboardStatisticsVO vo = new DashboardStatisticsVO();

        // 总订单数
        Long totalOrders = orderInfoMapper.selectCount(null);
        vo.setTotalOrders(totalOrders.intValue());

        // 总销售额（已完成订单）
        List<OrderInfo> completedOrders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED)
        );
        BigDecimal totalSales = completedOrders.stream()
                .map(OrderInfo::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSales(totalSales);

        // 总用户数
        Long totalUsers = userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>()
                        .in(UserInfo::getUserType, 1, 2) // 1-普通会员 2-星享会员
        );
        vo.setTotalUsers(totalUsers.intValue());

        // 总代理数
        Long totalAgents = userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>()
                        .in(UserInfo::getUserType, 3, 4) // 3-一级代理 4-二级代理
        );
        vo.setTotalAgents(totalAgents.intValue());

        // 今日数据
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 今日订单数
        Long todayOrders = orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .between(OrderInfo::getCreateTime, todayStart, todayEnd)
        );
        vo.setTodayOrders(todayOrders.intValue());

        // 今日销售额
        List<OrderInfo> todayCompletedOrders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED)
                        .between(OrderInfo::getCreateTime, todayStart, todayEnd)
        );
        BigDecimal todaySales = todayCompletedOrders.stream()
                .map(OrderInfo::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodaySales(todaySales);

        // 今日新增用户数
        Long todayUsers = userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>()
                        .between(UserInfo::getCreateTime, todayStart, todayEnd)
        );
        vo.setTodayUsers(todayUsers.intValue());

        return vo;
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(Integer days) {
        List<SalesTrendVO> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            SalesTrendVO vo = new SalesTrendVO();
            vo.setDate(date.format(formatter));

            // 当天订单
            List<OrderInfo> dayOrders = orderInfoMapper.selectList(
                    new LambdaQueryWrapper<OrderInfo>()
                            .between(OrderInfo::getCreateTime, dayStart, dayEnd)
            );

            // 订单数
            vo.setCount(dayOrders.size());

            // 销售额（已完成订单）
            BigDecimal amount = dayOrders.stream()
                    .filter(order -> order.getOrderStatus() == OrderStatusEnum.COMPLETED)
                    .map(OrderInfo::getActualAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAmount(amount);

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<OrderVO> getRecentOrders(Integer limit) {
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .orderByDesc(OrderInfo::getCreateTime)
                        .last("LIMIT " + limit)
        );

        return orders.stream()
                .map(order -> orderService.getOrderDetail(order.getId()))
                .collect(Collectors.toList());
    }
}
