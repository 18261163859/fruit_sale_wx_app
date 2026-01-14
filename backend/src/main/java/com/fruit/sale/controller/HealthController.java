package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "健康检查", description = "系统健康检查相关接口")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "健康检查", description = "检查系统运行状态")
    @GetMapping("/check")
    public Result<Map<String, Object>> check() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("application", "fruit-sale-backend");
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "高端云南水果销售系统");
        data.put("version", "1.0.0");
        data.put("description", "高端云南水果线上销售平台后台管理系统");
        data.put("javaVersion", System.getProperty("java.version"));
        data.put("osName", System.getProperty("os.name"));
        data.put("osVersion", System.getProperty("os.version"));
        return Result.success(data);
    }
}
