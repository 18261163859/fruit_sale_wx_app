package com.fruit.sale.service;

import com.fruit.sale.vo.DashboardStatisticsVO;
import com.fruit.sale.vo.OrderVO;
import com.fruit.sale.vo.SalesTrendVO;

import java.util.List;

/**
 * 后台首页服务接口
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
public interface IDashboardService {

    /**
     * 获取统计数据
     */
    DashboardStatisticsVO getStatistics();

    /**
     * 获取销售趋势
     * @param days 天数
     */
    List<SalesTrendVO> getSalesTrend(Integer days);

    /**
     * 获取最近订单
     * @param limit 数量限制
     */
    List<OrderVO> getRecentOrders(Integer limit);
}
