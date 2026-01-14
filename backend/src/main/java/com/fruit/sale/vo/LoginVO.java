package com.fruit.sale.vo;

import com.fruit.sale.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应 VO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "登录响应结果")
public class LoginVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户类型")
    private UserTypeEnum userType;

    @Schema(description = "是否为星享会员")
    private Integer isStarMember;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "积分余额")
    private Integer integral;

    @Schema(description = "账户余额")
    private String balance;

    // 管理员专用字段
    @Schema(description = "用户名（管理员）")
    private String username;

    @Schema(description = "真实姓名（管理员）")
    private String realName;

    @Schema(description = "角色（管理员）")
    private Integer role;
}
