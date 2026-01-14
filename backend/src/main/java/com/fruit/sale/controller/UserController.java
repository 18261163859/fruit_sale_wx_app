package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.service.IUserService;
import com.fruit.sale.vo.IntegralRecordVO;
import com.fruit.sale.vo.OrderStatisticsVO;
import com.fruit.sale.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "用户管理", description = "用户信息相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @Operation(summary = "开通星享会员", description = "支付199元开通星享会员")
    @PostMapping("/star-member/open")
    public Result<String> openStarMember(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.openStarMember(userId);
        return Result.success("开通成功");
    }

    @Operation(summary = "兑换积分", description = "使用兑换码充值积分")
    @PostMapping("/integral/recharge")
    public Result<String> rechargeIntegral(HttpServletRequest request,
                                         @RequestParam String cardNo) {
        Long userId = (Long) request.getAttribute("userId");
        userService.rechargeIntegral(userId, cardNo);
        return Result.success("兑换成功");
    }

    @Operation(summary = "获取积分记录", description = "获取用户的积分变动记录列表")
    @GetMapping("/integral/records")
    public Result<List<IntegralRecordVO>> getIntegralRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<IntegralRecordVO> records = userService.getIntegralRecords(userId);
        return Result.success(records);
    }

    @Operation(summary = "获取订单统计", description = "获取用户的订单统计数据")
    @GetMapping("/order/statistics")
    public Result<OrderStatisticsVO> getOrderStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderStatisticsVO statistics = userService.getOrderStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "绑定邀请码", description = "用户绑定邀请人的邀请码，绑定后不可修改")
    @PostMapping("/bind-invite-code")
    public Result<String> bindInviteCode(HttpServletRequest request,
                                        @RequestParam String inviteCode) {
        Long userId = (Long) request.getAttribute("userId");
        userService.bindInviteCode(userId, inviteCode);
        return Result.success("绑定成功");
    }
}
