package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.service.ISystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "系统配置", description = "系统配置相关接口")
@RestController
@RequestMapping("/system-config")
public class SystemConfigController {

    @Autowired
    private ISystemConfigService systemConfigService;

    @Operation(summary = "获取主题配置", description = "获取新年主题等配置信息")
    @GetMapping("/theme")
    public Result<Map<String, Object>> getThemeConfig() {
        Map<String, Object> config = systemConfigService.getThemeConfig();
        return Result.success(config);
    }

    @Operation(summary = "获取积分配置", description = "获取积分兑换比例和最大抵扣比例")
    @GetMapping("/integral")
    public Result<Map<String, Object>> getIntegralConfig() {
        Map<String, Object> config = systemConfigService.getIntegralConfig();
        return Result.success(config);
    }

    @Operation(summary = "获取运费配置", description = "获取基础运费和包邮门槛")
    @GetMapping("/shipping")
    public Result<Map<String, Object>> getShippingConfig() {
        Map<String, Object> config = systemConfigService.getShippingConfig();
        return Result.success(config);
    }

    @Operation(summary = "获取客服配置", description = "获取客服电话和微信")
    @GetMapping("/customer-service")
    public Result<Map<String, Object>> getCustomerServiceConfig() {
        Map<String, Object> config = systemConfigService.getCustomerServiceConfig();
        return Result.success(config);
    }

    @Operation(summary = "获取VIP配置", description = "获取VIP价格和时长")
    @GetMapping("/vip")
    public Result<Map<String, Object>> getVipConfig() {
        Map<String, Object> config = systemConfigService.getVipConfig();
        return Result.success(config);
    }
}
