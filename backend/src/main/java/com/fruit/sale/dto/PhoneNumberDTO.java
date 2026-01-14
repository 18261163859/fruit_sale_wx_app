package com.fruit.sale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 获取手机号DTO
 *
 * @author fruit-sale
 * @since 2024-10-02
 */
@Data
public class PhoneNumberDTO {

    /**
     * 微信code
     */
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 加密数据
     */
    private String encryptedData;

    /**
     * 加密算法的初始向量
     */
    private String iv;
}
