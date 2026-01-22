package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.ResultCode;
import com.fruit.sale.dto.CreateOrderDTO;
import com.fruit.sale.dto.ShipOrderDTO;
import com.fruit.sale.entity.*;
import com.fruit.sale.enums.CommissionStatusEnum;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.enums.UserTypeEnum;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.*;
import com.fruit.sale.service.IOrderService;
import com.fruit.sale.service.IUserService;
import com.fruit.sale.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private com.fruit.sale.mapper.ProductSpecMapper productSpecMapper;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private ShareRecordMapper shareRecordMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private com.fruit.sale.service.ICommissionService commissionService;

    @Autowired
    private com.fruit.sale.service.ISystemConfigService systemConfigService;

    /**
     * 获取VIP折扣率（例如：9.5折返回0.95）
     */
    private BigDecimal getVipDiscountRate() {
        try {
            String vipDiscount = systemConfigService.getConfigValue("vipDiscount");
            if (vipDiscount != null) {
                // 配置值是9.5表示95折，需要除以10得到0.95
                return new BigDecimal(vipDiscount).divide(new BigDecimal("10"));
            }
        } catch (Exception e) {
            log.error("获取VIP折扣配置失败", e);
        }
        // 默认95折
        return new BigDecimal("0.95");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        // 获取用户信息
        UserInfo user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 获取收货地址
        UserAddress address = userAddressMapper.selectById(createOrderDTO.getAddressId());
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 创建订单
        OrderInfo order = new OrderInfo();
        order.setOrderNo("ORD" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setUserType(user.getUserType() != null ? user.getUserType() : UserTypeEnum.NORMAL.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverAddress(address.getDetailAddress());
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT);
        order.setBuyerRemark(createOrderDTO.getRemark());

        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 获取VIP折扣率
        BigDecimal vipDiscountRate = getVipDiscountRate();

        for (CreateOrderDTO.OrderItemDTO itemDTO : createOrderDTO.getItems()) {
            ProductInfo product = productInfoMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }

            // 价格、库存根据规格确定
            BigDecimal productPrice = product.getPrice();
            Integer availableStock = product.getStock();
            String specName = null;
            Long specId = null;

            // 如果指定了规格,使用规格的价格和库存
            if (itemDTO.getSpecId() != null) {
                com.fruit.sale.entity.ProductSpec spec = productSpecMapper.selectById(itemDTO.getSpecId());
                if (spec == null) {
                    throw new BusinessException("商品规格不存在");
                }
                if (spec.getStatus() == 0) {
                    throw new BusinessException("该规格已下架");
                }
                specId = spec.getId();
                specName = spec.getSpecName();
                productPrice = spec.getPrice();
                availableStock = spec.getStock();

                // VIP价格处理：通过vip_expire_time判断是否为星享会员
                if (user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
                    productPrice = spec.getVipPrice() != null ? spec.getVipPrice() : spec.getPrice().multiply(vipDiscountRate);
                }
            } else if (user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
                // 没有规格且是VIP用户,使用配置的折扣
                productPrice = product.getPrice().multiply(vipDiscountRate);
            }

            if (availableStock < itemDTO.getQuantity()) {
                throw new BusinessException(product.getProductName() + " 库存不足");
            }

            BigDecimal subtotal = productPrice.multiply(new BigDecimal(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductNo(product.getProductNo());
            orderItem.setSpecId(specId);
            orderItem.setSpecName(specName);
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductPrice(product.getPrice()); // 原价
            orderItem.setActualPrice(productPrice); // 实际成交价(VIP折扣后)
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotalAmount(subtotal);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 折扣已在商品价格中体现(规格的VIP价格),不再额外计算
        BigDecimal discountAmount = BigDecimal.ZERO;
        order.setDiscountAmount(discountAmount);

        // 计算积分抵扣
        BigDecimal integralDeductAmount = BigDecimal.ZERO;
        Integer integralUsed = 0;
        if (createOrderDTO.getUseIntegral() != null && createOrderDTO.getUseIntegral() > 0) {
            // 检查用户积分余额
            Integer userIntegralBalance = user.getIntegralBalance() != null ? user.getIntegralBalance() : 0;
            if (createOrderDTO.getUseIntegral() > userIntegralBalance) {
                throw new BusinessException("积分余额不足");
            }

            // 从系统配置中获取积分抵扣比例（默认100积分=1元）
            String exchangeRateStr = systemConfigService.getConfigValue("integral_exchange_rate");
            BigDecimal exchangeRate = new BigDecimal(exchangeRateStr != null ? exchangeRateStr : "100");

            // 计算实际抵扣金额 = 使用积分数 / 兑换比例
            integralDeductAmount = new BigDecimal(createOrderDTO.getUseIntegral())
                    .divide(exchangeRate, 2, RoundingMode.HALF_UP);

            // 积分抵扣金额不能超过订单总金额
            if (integralDeductAmount.compareTo(totalAmount) > 0) {
                integralDeductAmount = totalAmount;
            }

            integralUsed = createOrderDTO.getUseIntegral();
            log.info("订单积分抵扣: 使用积分={}, 兑换比例={}, 抵扣金额={}",
                    integralUsed, exchangeRate, integralDeductAmount);
        }
        order.setIntegralDeductAmount(integralDeductAmount);
        order.setIntegralUsed(integralUsed);

        // 计算运费
        Map<String, Object> shippingConfig = systemConfigService.getShippingConfig();
        BigDecimal defaultFreight = (BigDecimal) shippingConfig.get("defaultFreight");
        BigDecimal freeFreightThreshold = (BigDecimal) shippingConfig.get("freeFreightThreshold");

        BigDecimal freightAmount;
        if (totalAmount.compareTo(freeFreightThreshold) >= 0) {
            // 满额包邮
            freightAmount = BigDecimal.ZERO;
            log.info("订单满额包邮: 商品总金额={}, 包邮门槛={}", totalAmount, freeFreightThreshold);
        } else {
            // 需要运费
            freightAmount = defaultFreight;
            log.info("订单需要运费: 商品总金额={}, 运费={}", totalAmount, freightAmount);
        }
        order.setFreightAmount(freightAmount);

        // 实付金额 = 商品总金额 - 折扣 - 积分抵扣 + 运费
        BigDecimal actualAmount = totalAmount.subtract(discountAmount).subtract(integralDeductAmount).add(freightAmount);
        if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
            actualAmount = BigDecimal.ZERO;
        }
        order.setActualAmount(actualAmount);

        // 保存订单
        orderInfoMapper.insert(order);

        // 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo()); // 设置订单编号
            orderItemMapper.insert(item);

            // 扣减库存
            ProductInfo product = productInfoMapper.selectById(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            productInfoMapper.updateById(product);
        }

        OrderVO res = new OrderVO();
        res.setId(order.getId());
        res.setOrderNo(order.getOrderNo());

        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT) {
            throw new BusinessException("订单状态异常");
        }

        // 更新订单状态
        order.setOrderStatus(OrderStatusEnum.PENDING_SHIPMENT);
        order.setPayTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        // 扣除用户积分
        if (order.getIntegralUsed() != null && order.getIntegralUsed() > 0) {
            UserInfo user = userService.getById(order.getUserId());
            Integer balanceBefore = user.getIntegralBalance();
            Integer balanceAfter = balanceBefore - order.getIntegralUsed();
            user.setIntegralBalance(balanceAfter);
            userInfoMapper.updateById(user);

            // 记录积分变动
            IntegralRecord record = new IntegralRecord();
            record.setRecordNo("IR" + System.currentTimeMillis());
            record.setUserId(user.getId());
            record.setChangeType(4); // 4-抵扣消费
            record.setChangeAmount(-order.getIntegralUsed());
            record.setBalanceBefore(balanceBefore);
            record.setBalanceAfter(balanceAfter);
            record.setRelatedOrderNo(order.getOrderNo());
            record.setRemark("订单消费抵扣");
            integralRecordMapper.insert(record);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(ShipOrderDTO shipOrderDTO) {
        OrderInfo order = orderInfoMapper.selectById(shipOrderDTO.getOrderId());
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING_SHIPMENT) {
            throw new BusinessException("订单状态异常，无法发货");
        }

        // 保存物流信息
        OrderLogistics logistics = new OrderLogistics();
        logistics.setOrderId(order.getId());
        logistics.setOrderNo(order.getOrderNo());
        logistics.setExpressCompany(shipOrderDTO.getExpressCompany());
        logistics.setExpressNo(shipOrderDTO.getExpressNo());
        logistics.setPackageBeforeImage(shipOrderDTO.getPackageBeforeImage());
        logistics.setPackageAfterImage(shipOrderDTO.getPackageAfterImage());
        logistics.setShipRemark(shipOrderDTO.getShipRemark());
        logistics.setShipTime(LocalDateTime.now());
        orderLogisticsMapper.insert(logistics);

        // 更新订单状态
        order.setOrderStatus(OrderStatusEnum.SHIPPED);
        orderInfoMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (order.getOrderStatus() != OrderStatusEnum.SHIPPED) {
            throw new BusinessException("订单状态异常，无法完成");
        }

        // 更新订单状态
        order.setOrderStatus(OrderStatusEnum.COMPLETED);
        orderInfoMapper.updateById(order);

        // 结算返现和积分（返现基于实付金额，已自动扣除积分抵扣）
        commissionService.settleCommission(order);
        settleIntegral(order);

        // 注意: OrderInfo不再有sharerId字段，分享奖励逻辑需要从其他地方获取
        // if (order.getSharerId() != null) {
        //     settleShareReward(order);
        // }
    }

    /**
     * 结算积分
     */
    private void settleIntegral(OrderInfo order) {
        UserInfo user = userService.getById(order.getUserId());
        if (user == null) {
            return;
        }

        // TODO: 从系统配置获取积分比例
        Integer integralReward = order.getActualAmount().intValue();
        Integer balanceBefore = user.getIntegralBalance();
        Integer balanceAfter = balanceBefore + integralReward;

        user.setIntegralBalance(balanceAfter);
        userInfoMapper.updateById(user);

        IntegralRecord record = new IntegralRecord();
        record.setRecordNo("IR" + System.currentTimeMillis());
        record.setUserId(user.getId());
        record.setChangeType(2); // 2-消费获得
        record.setChangeAmount(integralReward);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setRelatedOrderNo(order.getOrderNo());
        record.setRemark("购物获得积分");
        integralRecordMapper.insert(record);
    }

    /**
     * 结算分享奖励 - 已废弃，OrderInfo不再有sharerId字段
     */
    @Deprecated
    private void settleShareReward(OrderInfo order) {
        // OrderInfo不再有sharerId字段，此方法已废弃
        // 分享奖励逻辑需要从其他地方获取分享关系
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT) {
            throw new BusinessException("只能取消待付款订单");
        }

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId));

        for (OrderItem item : items) {
            ProductInfo product = productInfoMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productInfoMapper.updateById(product);
            }
        }

        // 更新订单状态
        order.setOrderStatus(OrderStatusEnum.CANCELLED);
        orderInfoMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long userId, Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getOrderStatus() != OrderStatusEnum.SHIPPED) {
            throw new BusinessException("订单状态异常，无法确认收货");
        }

        // 更新订单状态
        order.setOrderStatus(OrderStatusEnum.COMPLETED);
        orderInfoMapper.updateById(order);

        // 结算返现和积分（返现基于实付金额，已自动扣除积分抵扣）
        commissionService.settleCommission(order);
        settleIntegral(order);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        OrderVO vo = BeanUtil.copyProperties(order, OrderVO.class);

        // 获取订单明细
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId));

        List<OrderVO.OrderItemVO> itemVOs = items.stream()
                .map(item -> BeanUtil.copyProperties(item, OrderVO.OrderItemVO.class))
                .collect(Collectors.toList());
        vo.setItems(itemVOs);

        // 获取物流信息
        OrderLogistics logistics = orderLogisticsMapper.selectOne(
                new LambdaQueryWrapper<OrderLogistics>()
                        .eq(OrderLogistics::getOrderId, orderId));

        if (logistics != null) {
            vo.setExpressNo(logistics.getExpressNo());
            vo.setExpressCompany(logistics.getExpressCompany());
            vo.setShipRemark(logistics.getShipRemark());
            vo.setPackageBeforeImage(logistics.getPackageBeforeImage());
            vo.setPackageAfterImage(logistics.getPackageAfterImage());
        }

        // 手动映射字段名不一致的属性
        vo.setIntegralAmount(order.getIntegralDeductAmount());
        vo.setPayAmount(order.getActualAmount());
        vo.setUseIntegral(order.getIntegralUsed());

        return vo;
    }

    @Override
    public PageResult<OrderVO> getUserOrders(Long userId, Integer status, Long current, Long size) {
        Page<OrderInfo> page = new Page<>(current, size);

        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId)
                .eq(status != null, OrderInfo::getOrderStatus, status)
                .orderByDesc(OrderInfo::getCreateTime);

        Page<OrderInfo> orderPage = orderInfoMapper.selectPage(page, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> getOrderDetail(order.getId()))
                .collect(Collectors.toList());

        return PageResult.build(voList, orderPage.getTotal(), 
                orderPage.getCurrent(), orderPage.getSize());
    }

    @Override
    public PageResult<OrderVO> getPendingShipOrders(Long current, Long size) {
        Page<OrderInfo> page = new Page<>(current, size);

        Page<OrderInfo> orderPage = orderInfoMapper.selectPage(page,
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.PENDING_SHIPMENT)
                        .orderByAsc(OrderInfo::getCreateTime));

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> getOrderDetail(order.getId()))
                .collect(Collectors.toList());

        return PageResult.build(voList, orderPage.getTotal(),
                orderPage.getCurrent(), orderPage.getSize());
    }

    @Override
    public PageResult<OrderVO> getShippedOrders(Long current, Long size) {
        Page<OrderInfo> page = new Page<>(current, size);

        Page<OrderInfo> orderPage = orderInfoMapper.selectPage(page,
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.SHIPPED)
                        .orderByDesc(OrderInfo::getUpdateTime));

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> getOrderDetail(order.getId()))
                .collect(Collectors.toList());

        return PageResult.build(voList, orderPage.getTotal(),
                orderPage.getCurrent(), orderPage.getSize());
    }

    @Override
    public com.fruit.sale.vo.OrderStatisticsVO getUserOrderStatistics(Long userId) {
        com.fruit.sale.vo.OrderStatisticsVO vo = new com.fruit.sale.vo.OrderStatisticsVO();

        // 待付款
        Integer pendingPayment = Math.toIntExact(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.PENDING_PAYMENT)));
        vo.setPendingPaymentCount(pendingPayment);

        // 待发货
        Integer pendingShip = Math.toIntExact(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.PENDING_SHIPMENT)));
        vo.setPendingShipmentCount(pendingShip);

        // 待收货（已发货）
        Integer shipped = Math.toIntExact(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.SHIPPED)));
        vo.setShippedCount(shipped);

        // 已完成订单数
        Integer completed = Math.toIntExact(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderStatus, OrderStatusEnum.COMPLETED)));
        vo.setCompletedCount(completed);

        return vo;
    }
}
