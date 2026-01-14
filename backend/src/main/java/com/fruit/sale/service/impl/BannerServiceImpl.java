package com.fruit.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.entity.BannerConfig;
import com.fruit.sale.mapper.BannerConfigMapper;
import com.fruit.sale.service.IBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class BannerServiceImpl implements IBannerService {

    @Autowired
    private BannerConfigMapper bannerConfigMapper;

    @Override
    public List<BannerConfig> getHomeBanners() {
        return bannerConfigMapper.selectList(
                new LambdaQueryWrapper<BannerConfig>()
                        .eq(BannerConfig::getStatus, 1) // 仅返回启用状态的
                        .orderByAsc(BannerConfig::getSort)
                        .orderByDesc(BannerConfig::getCreateTime)
        );
    }
}