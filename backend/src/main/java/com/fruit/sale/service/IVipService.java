package com.fruit.sale.service;

import com.fruit.sale.dto.VipOrderDTO;
import com.fruit.sale.vo.VipOrderVO;

/**
 * 星享会员服务接口
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
public interface IVipService {

    /**
     * 创建会员开通订单
     *
     * @param userId 用户ID
     * @return 订单信息
     */
    VipOrderVO createVipOrder(Long userId);

    /**
     * 模拟支付并开通会员
     *
     * @param orderNo 订单号
     * @param payType 支付方式
     * @return 支付结果
     */
    VipOrderVO payVipOrder(String orderNo, String payType);

    /**
     * 查询会员订单详情
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    VipOrderVO getVipOrderByNo(String orderNo);

    /**
     * 检查用户是否是会员
     *
     * @param userId 用户ID
     * @return 是否是会员
     */
    boolean isVipUser(Long userId);
}
