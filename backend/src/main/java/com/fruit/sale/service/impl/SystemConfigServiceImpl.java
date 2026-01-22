package com.fruit.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.entity.SystemConfig;
import com.fruit.sale.mapper.SystemConfigMapper;
import com.fruit.sale.service.ISystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class SystemConfigServiceImpl implements ISystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    // 配置键常量
    private static final String NEWYEAR_NORMAL_USER_ENABLED = "newyear_theme_normal_user_enabled";
    private static final String NEWYEAR_VIP_USER_ENABLED = "newyear_theme_vip_user_enabled";
    private static final String INTEGRAL_EXCHANGE_RATE = "integral_exchange_rate";
    private static final String MAX_POINTS_DEDUCTION_RATE = "maxPointsDeductionRate";
    private static final String DEFAULT_FREIGHT = "default_freight";
    private static final String FREE_FREIGHT_THRESHOLD = "free_freight_threshold";
    private static final String SERVICE_PHONE = "servicePhone";
    private static final String SERVICE_WECHAT = "serviceWechat";
    private static final String VIP_PRICE = "vipPrice";
    private static final String VIP_DURATION_DAYS = "vip_duration_days";
    private static final String VIP_DISCOUNT = "vipDiscount";

    @Override
    public Map<String, Object> getThemeConfig() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取普通用户新年主题配置
        String normalUserEnabled = getConfigValue(NEWYEAR_NORMAL_USER_ENABLED);
        result.put("normalUserNewYearEnabled", "true".equals(normalUserEnabled));
        
        // 获取VIP用户新年主题配置
        String vipUserEnabled = getConfigValue(NEWYEAR_VIP_USER_ENABLED);
        result.put("vipUserNewYearEnabled", "true".equals(vipUserEnabled));
        
        return result;
    }

    @Override
    public void setNewYearThemeConfig(Boolean normalUserEnabled, Boolean vipUserEnabled) {
        // 设置普通用户新年主题
        if (normalUserEnabled != null) {
            setConfigValue(NEWYEAR_NORMAL_USER_ENABLED, 
                normalUserEnabled.toString(), 
                "普通用户是否启用新年主题");
        }
        
        // 设置VIP用户新年主题
        if (vipUserEnabled != null) {
            setConfigValue(NEWYEAR_VIP_USER_ENABLED, 
                vipUserEnabled.toString(), 
                "VIP用户是否启用新年主题");
        }
    }

    @Override
    public String getConfigValue(String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public void setConfigValue(String key, String value, String desc) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);

        if (config != null) {
            // 更新已有配置
            config.setConfigValue(value);
            config.setConfigDesc(desc);
            config.setUpdateTime(LocalDateTime.now());
            systemConfigMapper.updateById(config);
        } else {
            // 新增配置
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigDesc(desc);
            config.setConfigType("string");
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            systemConfigMapper.insert(config);
        }
    }

    @Override
    public Map<String, Object> getIntegralConfig() {
        Map<String, Object> result = new HashMap<>();

        // 获取积分兑换比例（100积分=1元）
        String exchangeRate = getConfigValue(INTEGRAL_EXCHANGE_RATE);
        result.put("integralExchangeRate", exchangeRate != null ? Integer.parseInt(exchangeRate) : 100);

        // 获取最大积分抵扣比例（30%）
        String maxDeductionRate = getConfigValue(MAX_POINTS_DEDUCTION_RATE);
        result.put("maxPointsDeductionRate", maxDeductionRate != null ? Integer.parseInt(maxDeductionRate) : 30);

        return result;
    }

    @Override
    public Map<String, Object> getShippingConfig() {
        Map<String, Object> result = new HashMap<>();

        // 获取基础运费（默认15元）
        String defaultFreight = getConfigValue(DEFAULT_FREIGHT);
        // 只有当配置不存在时才使用默认值，0是有效值
        if (defaultFreight != null && !defaultFreight.trim().isEmpty()) {
            result.put("defaultFreight", new BigDecimal(defaultFreight));
        } else if (defaultFreight != null && defaultFreight.trim().isEmpty()) {
            // 空字符串视为未配置
            result.put("defaultFreight", new BigDecimal("15"));
        } else {
            // null视为未配置
            result.put("defaultFreight", new BigDecimal("15"));
        }

        // 获取包邮门槛（默认199元）
        String freeFreightThreshold = getConfigValue(FREE_FREIGHT_THRESHOLD);
        result.put("freeFreightThreshold", freeFreightThreshold != null && !freeFreightThreshold.trim().isEmpty()
            ? new BigDecimal(freeFreightThreshold) : new BigDecimal("199"));

        return result;
    }

    @Override
    public Map<String, Object> getCustomerServiceConfig() {
        Map<String, Object> result = new HashMap<>();

        // 获取客服电话
        String servicePhone = getConfigValue(SERVICE_PHONE);
        result.put("servicePhone", servicePhone != null ? servicePhone : "");

        // 获取客服微信
        String serviceWechat = getConfigValue(SERVICE_WECHAT);
        result.put("serviceWechat", serviceWechat != null ? serviceWechat : "");

        return result;
    }

    @Override
    public Map<String, Object> getVipConfig() {
        Map<String, Object> result = new HashMap<>();

        // 获取VIP价格（默认199元）
        String vipPrice = getConfigValue(VIP_PRICE);
        result.put("vipPrice", vipPrice != null ? new BigDecimal(vipPrice) : new BigDecimal("199.00"));

        // 获取VIP时长（默认365天）
        String vipDuration = getConfigValue(VIP_DURATION_DAYS);
        result.put("vipDurationDays", vipDuration != null ? Integer.parseInt(vipDuration) : 365);

        // 获取VIP折扣（默认9.5，即95折）
        String vipDiscount = getConfigValue(VIP_DISCOUNT);
        result.put("vipDiscount", vipDiscount != null ? new BigDecimal(vipDiscount) : new BigDecimal("9.5"));

        return result;
    }
}
