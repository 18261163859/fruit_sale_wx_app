package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.FinanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 财务流水记录 Mapper
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {

    /**
     * 统计指定日期范围内的收支汇总
     */
    @Select("SELECT record_type, SUM(amount) as total_amount, COUNT(*) as count " +
            "FROM finance_record " +
            "WHERE is_deleted = 0 AND record_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY record_type")
    List<Map<String, Object>> statisticsByDateRange(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 计算指定日期范围内的总收入
     */
    @Select("SELECT COALESCE(SUM(amount), 0) " +
            "FROM finance_record " +
            "WHERE is_deleted = 0 AND income_type = 1 " +
            "AND record_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal getTotalIncome(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    /**
     * 计算指定日期范围内的总支出
     */
    @Select("SELECT COALESCE(SUM(amount), 0) " +
            "FROM finance_record " +
            "WHERE is_deleted = 0 AND income_type = 2 " +
            "AND record_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal getTotalExpense(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
