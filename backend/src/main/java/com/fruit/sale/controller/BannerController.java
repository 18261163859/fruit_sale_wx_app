package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.entity.BannerConfig;
import com.fruit.sale.service.IBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "轮播图", description = "轮播图相关接口")
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private IBannerService bannerService;

    @Operation(summary = "获取首页轮播图", description = "获取首页展示的轮播图列表")
    @GetMapping("/list")
    public Result<List<BannerConfig>> getHomeBanners() {
        List<BannerConfig> banners = bannerService.getHomeBanners();
        return Result.success(banners);
    }
}