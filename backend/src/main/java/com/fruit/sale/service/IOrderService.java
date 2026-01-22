package com.fruit.sale.service;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.dto.CreateOrderDTO;
import com.fruit.sale.dto.ShipOrderDTO;
import com.fruit.sale.vo.OrderVO;
import com.fruit.sale.vo.OrderStatisticsVO;

/**
 * 订单服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IOrderService {

    /**
     * 创建订单
     */
    OrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO);

    /**
     * 支付订单
     */
    void payOrder(Long orderId);

    /**
     * 发货
     */
    void shipOrder(ShipOrderDTO shipOrderDTO);

    /**
     * 确认完成订单（后台操作）
     */
    void completeOrder(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmReceipt(Long userId, Long orderId);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 获取用户订单列表
     */
    PageResult<OrderVO> getUserOrders(Long userId, Integer status, Long current, Long size);

    /**
     * 获取待发货订单列表（发货端）
     */
    PageResult<OrderVO> getPendingShipOrders(Long current, Long size);

    /**
     * 获取已发货订单列表（发货端）
     */
    PageResult<OrderVO> getShippedOrders(Long current, Long size);

    /**
     * 获取用户订单统计
     */
    OrderStatisticsVO getUserOrderStatistics(Long userId);
}
