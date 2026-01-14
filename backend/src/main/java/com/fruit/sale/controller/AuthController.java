package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.dto.AdminLoginDTO;
import com.fruit.sale.dto.LoginDTO;
import com.fruit.sale.dto.PhoneNumberDTO;
import com.fruit.sale.service.IAdminUserService;
import com.fruit.sale.service.IUserService;
import com.fruit.sale.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "认证管理", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAdminUserService adminUserService;

    @Operation(summary = "微信小程序登录", description = "微信小程序用户登录/注册")
    @PostMapping({"/login", "/user/login"})
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @Operation(summary = "后台管理员登录", description = "后台管理系统登录")
    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody AdminLoginDTO loginDTO) {
        LoginVO loginVO = adminUserService.login(loginDTO);
        return Result.success(loginVO);
    }

    @Operation(summary = "获取手机号", description = "微信小程序获取用户手机号")
    @PostMapping("/phone")
    public Result<String> getPhoneNumber(@Valid @RequestBody PhoneNumberDTO phoneNumberDTO,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // TODO: 调用微信API解密手机号
        // 暂时返回成功，实际需要解密encryptedData获取手机号
        String phone = "138****" + System.currentTimeMillis() % 10000;
        userService.updatePhone(userId, phone);
        return Result.success("绑定成功");
    }
}
