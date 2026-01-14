package com.fruit.sale.service;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.vo.CommissionRecordVO;
import com.fruit.sale.vo.CommissionStatisticsVO;

/**
 * 返现服务接口
 *
 * @author fruit-sale
 * @since 2025-10-06
 */
public interface ICommissionService {

    /**
     * 结算订单返现
     * 根据pd.md中的返现规则计算并记录返现
     *
     * @param order 订单信息
     */
    void settleCommission(OrderInfo order);

    /**
     * 查询代理的返现记录
     *
     * @param agentId 代理用户ID
     * @param current 当前页
     * @param size    每页大小
     * @return 返现记录分页结果
     */
    PageResult<CommissionRecordVO> getCommissionRecords(Long agentId, Long current, Long size);

    /**
     * 获取代理的返现统计信息
     *
     * @param agentId 代理用户ID
     * @return 返现统计信息
     */
    CommissionStatisticsVO getCommissionStatistics(Long agentId);

    /**
     * 获取代理的下级用户返现记录
     * 用于代理查看其下级用户带来的返现
     *
     * @param agentId 代理用户ID
     * @param current 当前页
     * @param size    每页大小
     * @return 返现记录分页结果
     */
    PageResult<CommissionRecordVO> getSubordinateCommissions(Long agentId, Long current, Long size);
}
