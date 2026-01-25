package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.dto.VipOrderDTO;
import com.fruit.sale.service.ISystemConfigService;
import com.fruit.sale.service.IVipService;
import com.fruit.sale.vo.VipOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 星享会员控制器
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Tag(name = "星享会员管理", description = "星享会员相关接口")
@RestController
@RequestMapping("/vip")
public class VipController {

    @Autowired
    private IVipService vipService;

    @Operation(summary = "创建会员开通订单", description = "创建星享会员开通订单")
    @PostMapping("/order/create")
    public Result<VipOrderVO> createVipOrder(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        VipOrderVO order = vipService.createVipOrder(userId);
        return Result.success("订单创建成功", order);
    }

    @Operation(summary = "支付会员订单", description = "支付并开通星享会员")
    @PostMapping("/order/pay")
    public Result<VipOrderVO> payVipOrder(@Valid @RequestBody VipOrderDTO dto) {
        VipOrderVO order = vipService.payVipOrder(dto.getOrderNo(), dto.getPayType());
        return Result.success("支付成功，星享会员已开通", order);
    }

    @Operation(summary = "查询会员订单详情", description = "根据订单号查询会员订单详情")
    @GetMapping("/order/{orderNo}")
    public Result<VipOrderVO> getVipOrder(@PathVariable String orderNo) {
        VipOrderVO order = vipService.getVipOrderByNo(orderNo);
        return Result.success(order);
    }

    @Operation(summary = "检查会员状态", description = "检查当前用户是否是星享会员")
    @GetMapping("/status")
    public Result<Boolean> checkVipStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isVip = vipService.isVipUser(userId);
        return Result.success(isVip);
    }
}
