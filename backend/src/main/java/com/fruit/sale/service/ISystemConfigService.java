package com.fruit.sale.service;

import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface ISystemConfigService {

    /**
     * 获取主题配置
     * @return 主题配置信息
     */
    Map<String, Object> getThemeConfig();

    /**
     * 设置新年主题配置
     * @param normalUserEnabled 普通用户是否启用新年主题
     * @param vipUserEnabled VIP用户是否启用新年主题
     */
    void setNewYearThemeConfig(Boolean normalUserEnabled, Boolean vipUserEnabled);

    /**
     * 根据key获取配置值
     * @param key 配置键
     * @return 配置值
     */
    String getConfigValue(String key);

    /**
     * 获取积分配置
     * @return 积分配置信息
     */
    Map<String, Object> getIntegralConfig();

    /**
     * 获取运费配置
     * @return 运费配置信息
     */
    Map<String, Object> getShippingConfig();

    /**
     * 设置配置值
     * @param key 配置键
     * @param value 配置值
     * @param desc 配置说明
     */
    void setConfigValue(String key, String value, String desc);

    /**
     * 获取客服配置
     * @return 客服配置信息
     */
    Map<String, Object> getCustomerServiceConfig();
}
