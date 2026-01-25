package com.fruit.sale.controller;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.Result;
import com.fruit.sale.dto.CreateOrderDTO;
import com.fruit.sale.dto.ShipOrderDTO;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IOrderService;
import com.fruit.sale.service.WeChatPayService;
import com.fruit.sale.vo.OrderVO;
import com.fruit.sale.vo.OrderStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Operation(summary = "创建订单", description = "用户下单购买商品")
    @PostMapping("/create")
    public Result<OrderVO> createOrder(HttpServletRequest request,
                                    @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO order = orderService.createOrder(userId, createOrderDTO);

        // 返回完整的订单信息
        return Result.success(order);
    }

    @Operation(summary = "发起支付", description = "创建支付订单并返回微信支付参数")
    @PostMapping("/pay/{orderId}")
    public Result<Map<String, Object>> payOrder(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");

        // 获取订单信息
        OrderVO order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 验证订单状态
        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT) { // 0-待付款
            return Result.error("订单状态异常，无法支付");
        }

        // 获取用户OpenID
        com.fruit.sale.entity.UserInfo user = userInfoMapper.selectById(userId);
        if (user == null || user.getOpenid() == null || user.getOpenid().isEmpty()) {
            return Result.error("用户信息异常，请重新登录");
        }

        // 调用微信支付创建订单
        String description = "订单号：" + order.getOrderNo();
        Map<String, Object> payParams = weChatPayService.createPaymentOrder(
                orderId,
                order.getOrderNo(),
                order.getPayAmount(),
                description,
                user.getOpenid()
        );

        return Result.success("获取支付参数成功", payParams);
    }

    @Operation(summary = "取消订单", description = "取消待付款订单")
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success("订单已取消");
    }

    @Operation(summary = "确认收货", description = "用户确认收货")
    @PostMapping("/confirm/{orderId}")
    public Result<String> confirmReceipt(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.confirmReceipt(userId, orderId);
        return Result.success("确认收货成功");
    }

    @Operation(summary = "获取订单详情", description = "根据订单ID获取详细信息")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    @Operation(summary = "根据订单号获取订单详情", description = "根据订单号获取详细信息")
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getOrderDetailByOrderNo(@PathVariable String orderNo) {
        OrderVO order = orderService.getOrderDetailByOrderNo(orderNo);
        return Result.success(order);
    }

    @Operation(summary = "获取我的订单列表", description = "分页获取当前用户的订单")
    @GetMapping("/my/list")
    public Result<PageResult<OrderVO>> getMyOrders(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<OrderVO> page = orderService.getUserOrders(userId, status, current, size);
        return Result.success(page);
    }

    @Operation(summary = "获取订单统计", description = "获取各状态订单数量统计")
    @GetMapping("/my/statistics")
    public Result<OrderStatisticsVO> getOrderStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderStatisticsVO statistics = orderService.getUserOrderStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "发货", description = "发货端填写物流信息并发货")
    @PostMapping("/ship")
    public Result<String> shipOrder(@Valid @RequestBody ShipOrderDTO shipOrderDTO) {
        orderService.shipOrder(shipOrderDTO);
        return Result.success("发货成功");
    }

    @Operation(summary = "获取待发货订单", description = "发货端获取待发货订单列表")
    @GetMapping("/pending-ship/list")
    public Result<PageResult<OrderVO>> getPendingShipOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<OrderVO> page = orderService.getPendingShipOrders(current, size);
        return Result.success(page);
    }

    @Operation(summary = "获取已发货订单", description = "发货端获取已发货订单列表")
    @GetMapping("/shipped/list")
    public Result<PageResult<OrderVO>> getShippedOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<OrderVO> page = orderService.getShippedOrders(current, size);
        return Result.success(page);
    }

    @Operation(summary = "确认订单完成", description = "后台管理员确认订单完成")
    @PostMapping("/complete/{orderId}")
    public Result<String> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return Result.success("订单已完成");
    }
}
