package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置更新DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class ConfigUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;
}