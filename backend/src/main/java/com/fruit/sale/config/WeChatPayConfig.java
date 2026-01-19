package com.fruit.sale.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 *
 * @author fruit-sale
 * @since 2025-01-14
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户 API v3 密钥
     */
    private String mchKey;

    /**
     * 商户证书序列号
     */
    private String mchSerialNo;

    /**
     * 微信支付回调地址
     */
    private String notifyUrl;

    /**
     * APPID（小程序）
     */
    private String appId;
}