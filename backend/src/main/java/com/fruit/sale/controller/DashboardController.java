package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.service.IDashboardService;
import com.fruit.sale.vo.DashboardStatisticsVO;
import com.fruit.sale.vo.OrderVO;
import com.fruit.sale.vo.SalesTrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台首页控制器
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "后台首页管理", description = "后台首页数据统计接口")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据", description = "获取总订单数、总销售额、总用户数等统计数据")
    @GetMapping("/statistics")
    public Result<DashboardStatisticsVO> getStatistics() {
        DashboardStatisticsVO statistics = dashboardService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取销售趋势
     */
    @Operation(summary = "获取销售趋势", description = "获取近N天的销售趋势数据")
    @GetMapping("/sales-trend")
    public Result<List<SalesTrendVO>> getSalesTrend(@RequestParam(defaultValue = "7") Integer days) {
        List<SalesTrendVO> trend = dashboardService.getSalesTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取最近订单
     */
    @Operation(summary = "获取最近订单", description = "获取最近N条订单记录")
    @GetMapping("/recent-orders")
    public Result<List<OrderVO>> getRecentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        List<OrderVO> orders = dashboardService.getRecentOrders(limit);
        return Result.success(orders);
    }
}
