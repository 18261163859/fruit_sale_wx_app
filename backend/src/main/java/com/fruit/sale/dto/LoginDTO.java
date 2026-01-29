package com.fruit.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录 DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
@Schema(description = "登录请求参数")
public class LoginDTO {

    @Schema(description = "微信code")
    @NotBlank(message = "微信code不能为空")
    private String code;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "手机号(测试用)")
    private String phone;

    @Schema(description = "手机号code(用于解密获取真实手机号)")
    private String phoneCode;

@Schema(description = "邀请人ID")
    private Long inviterId;

    @Schema(description = "Invite code (for regular member binding)")
    private String inviteCode;

    @Schema(description = "Agent invite code (for proxy invitation with commission rate)")
    private String agentInviteCode;

    @Schema(description = "Commission rate (for proxy invitation via share link)")
    private java.math.BigDecimal commissionRate;
}
