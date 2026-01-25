package com.fruit.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.entity.FinanceRecord;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.entity.VipOrder;
import com.fruit.sale.mapper.FinanceRecordMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.mapper.VipOrderMapper;
import com.fruit.sale.service.ISystemConfigService;
import com.fruit.sale.service.IVipService;
import com.fruit.sale.vo.VipOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 星享会员服务实现
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Slf4j
@Service
public class VipServiceImpl implements IVipService {

    @Autowired
    private VipOrderMapper vipOrderMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private FinanceRecordMapper financeRecordMapper;

    @Autowired
    private ISystemConfigService iSystemConfigService;

    private static final int VIP_DURATION_DAYS = 365;

    @Override
    @Transactional
    public VipOrderVO createVipOrder(Long userId) {
        log.info("用户 {} 创建星享会员订单", userId);

        // 检查用户是否存在
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否已经是会员
        if (isVipUser(userId)) {
            throw new BusinessException("您已经是星享会员了");
        }

        // 检查是否有未支付的订单
        LambdaQueryWrapper<VipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VipOrder::getUserId, userId)
                .eq(VipOrder::getPayStatus, 0)
                .eq(VipOrder::getOrderStatus, 0);
        VipOrder existingOrder = vipOrderMapper.selectOne(wrapper);
        if (existingOrder != null) {
            log.info("用户 {} 已有未支付订单 {}", userId, existingOrder.getOrderNo());
            return convertToVO(existingOrder);
        }

        String VIP_PRICE_STR = iSystemConfigService.getConfigValue("vipPrice");

        BigDecimal VIP_PRICE = new BigDecimal(VIP_PRICE_STR);

        // 创建新订单
        VipOrder vipOrder = new VipOrder();
        vipOrder.setOrderNo(generateOrderNo());
        vipOrder.setUserId(userId);
        vipOrder.setVipPrice(VIP_PRICE);
        vipOrder.setActualAmount(VIP_PRICE);
        vipOrder.setOrderStatus(0); // 待支付
        vipOrder.setPayStatus(0); // 未支付
        vipOrder.setVipDuration(VIP_DURATION_DAYS);

        vipOrderMapper.insert(vipOrder);
        log.info("创建会员订单成功：{}", vipOrder.getOrderNo());

        return convertToVO(vipOrder);
    }

    @Override
    @Transactional
    public VipOrderVO payVipOrder(String orderNo, String payType) {
        log.info("支付会员订单：{}，支付方式：{}", orderNo, payType);

        // 查询订单
        LambdaQueryWrapper<VipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VipOrder::getOrderNo, orderNo);
        VipOrder vipOrder = vipOrderMapper.selectOne(wrapper);
        if (vipOrder == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (vipOrder.getPayStatus() == 1) {
            throw new BusinessException("订单已支付");
        }
        if (vipOrder.getOrderStatus() == 2) {
            throw new BusinessException("订单已取消");
        }

        // 模拟支付成功
        LocalDateTime now = LocalDateTime.now();
        vipOrder.setPayStatus(1);
        vipOrder.setOrderStatus(1);
        vipOrder.setPayType(payType);
        vipOrder.setPayTime(now);
        vipOrder.setTransactionId(UUID.randomUUID().toString().replace("-", ""));
        vipOrder.setVipStartTime(now);
        vipOrder.setVipEndTime(now.plusDays(VIP_DURATION_DAYS));

        vipOrderMapper.updateById(vipOrder);

        // 更新用户会员状态
        UserInfo user = userInfoMapper.selectById(vipOrder.getUserId());
        user.setUserType(2); // 2-VIP会员
        user.setVipExpireTime(vipOrder.getVipEndTime());
        userInfoMapper.updateById(user);

        // 创建财务记录
        createFinanceRecord(vipOrder);

        log.info("会员订单支付成功，用户 {} 已开通星享会员至 {}", vipOrder.getUserId(), vipOrder.getVipEndTime());

        return convertToVO(vipOrder);
    }

    @Override
    public VipOrderVO getVipOrderByNo(String orderNo) {
        LambdaQueryWrapper<VipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VipOrder::getOrderNo, orderNo);
        VipOrder vipOrder = vipOrderMapper.selectOne(wrapper);
        if (vipOrder == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(vipOrder);
    }

    @Override
    public boolean isVipUser(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 检查是否是VIP会员（基于user_type）
        return user.getUserType() != null && user.getUserType() == 2;
    }

    /**
     * 创建财务记录
     */
    private void createFinanceRecord(VipOrder vipOrder) {
        FinanceRecord record = new FinanceRecord();
        record.setRecordNo(generateFinanceRecordNo());
        record.setRecordType("vip_income");
        record.setAmount(vipOrder.getActualAmount());
        record.setIncomeType(1); // 收入
        record.setRelatedOrderNo(vipOrder.getOrderNo());
        record.setUserId(vipOrder.getUserId());
        record.setDescription("星享会员开通");
        record.setRecordDate(LocalDate.now());

        financeRecordMapper.insert(record);
        log.info("创建财务记录：{}", record.getRecordNo());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "VIP" + System.currentTimeMillis();
    }

    /**
     * 生成财务流水号
     */
    private String generateFinanceRecordNo() {
        return "FIN" + System.currentTimeMillis();
    }

    /**
     * 转换为VO
     */
    private VipOrderVO convertToVO(VipOrder vipOrder) {
        VipOrderVO vo = new VipOrderVO();
        BeanUtils.copyProperties(vipOrder, vo);

        // 设置状态描述
        switch (vipOrder.getOrderStatus()) {
            case 0:
                vo.setOrderStatusDesc("待支付");
                break;
            case 1:
                vo.setOrderStatusDesc("已支付");
                break;
            case 2:
                vo.setOrderStatusDesc("已取消");
                break;
            default:
                vo.setOrderStatusDesc("未知");
        }

        switch (vipOrder.getPayStatus()) {
            case 0:
                vo.setPayStatusDesc("未支付");
                break;
            case 1:
                vo.setPayStatusDesc("已支付");
                break;
            default:
                vo.setPayStatusDesc("未知");
        }

        return vo;
    }
}
