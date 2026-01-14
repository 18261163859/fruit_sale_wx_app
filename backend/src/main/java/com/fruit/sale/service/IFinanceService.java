package com.fruit.sale.service;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.vo.FinanceRecordVO;
import com.fruit.sale.vo.FinanceStatisticsVO;

import java.time.LocalDate;

/**
 * 财务服务接口
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
public interface IFinanceService {

    /**
     * 获取财务统计数据
     */
    FinanceStatisticsVO getStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取财务记录列表
     */
    PageResult<FinanceRecordVO> getRecords(Integer page, Integer pageSize, Integer type,
                                           LocalDate startDate, LocalDate endDate);
}
