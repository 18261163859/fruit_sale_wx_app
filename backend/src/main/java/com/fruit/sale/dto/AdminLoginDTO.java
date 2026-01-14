package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 后台登录 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "后台登录请求参数")
public class AdminLoginDTO {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
