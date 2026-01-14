package com.fruit.sale.dto;

import lombok.Data;

/**
 * 新年主题配置DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class NewYearThemeConfigDTO {
    
    /**
     * 普通用户是否启用新年主题
     */
    private Boolean normalUserEnabled;
    
    /**
     * VIP用户是否启用新年主题
     */
    private Boolean vipUserEnabled;
}
