package com.fruit.sale.controller;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.Result;
import com.fruit.sale.service.IFinanceService;
import com.fruit.sale.vo.FinanceRecordVO;
import com.fruit.sale.vo.FinanceStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 财务管理控制器
 *
 * @author fruit-sale
 * @since 2025-10-05
 */
@Slf4j
@RestController
@RequestMapping("/admin/finance")
@Tag(name = "财务管理", description = "财务数据统计和记录查询")
public class FinanceController {

    @Autowired
    private IFinanceService financeService;

    /**
     * 获取财务统计数据
     */
    @Operation(summary = "获取财务统计", description = "获取总收入、总返现、总提现等统计数据")
    @GetMapping("/statistics")
    public Result<FinanceStatisticsVO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        FinanceStatisticsVO statistics = financeService.getStatistics(startDate, endDate);
        return Result.success(statistics);
    }

    /**
     * 获取财务记录列表
     */
    @Operation(summary = "获取财务记录", description = "分页查询财务记录，支持按类型和日期筛选")
    @GetMapping("/records")
    public Result<PageResult<FinanceRecordVO>> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        PageResult<FinanceRecordVO> result = financeService.getRecords(page, pageSize, type, startDate, endDate);
        return Result.success(result);
    }

    /**
     * 导出财务报表
     */
    @Operation(summary = "导出财务报表", description = "导出财务记录Excel文件")
    @GetMapping("/export")
    public void exportRecords(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        // TODO: 实现导出功能
        log.info("导出财务报表: type={}, startDate={}, endDate={}", type, startDate, endDate);
    }
}
