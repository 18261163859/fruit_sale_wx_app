package com.fruit.sale.service;

import com.fruit.sale.entity.BannerConfig;

import java.util.List;

/**
 * 轮播图服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IBannerService {

    /**
     * 获取首页轮播图列表（仅返回启用状态的）
     */
    List<BannerConfig> getHomeBanners();
}