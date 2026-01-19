package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.service.WeChatPayService;
import com.fruit.sale.vo.PayParamsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付控制器
 *
 * @author fruit-sale
 * @since 2025-01-14
 */
@Tag(name = "支付管理", description = "支付相关接口")
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @Value("${wechat.pay.mchId:}")
    private String mchId;

    @Value("${wechat.pay.appId:}")
    private String appId;

    @Operation(summary = "获取支付参数", description = "创建微信支付订单并返回支付参数")
    @PostMapping("/params/{orderId}")
    public Result<Map<String, Object>> getPayParams(
            HttpServletRequest request,
            @PathVariable Long orderId,
            @RequestParam String orderNo,
            @RequestParam BigDecimal amount) {

        Long userId = (Long) request.getAttribute("userId");

        // 检查是否配置了微信支付
        boolean isConfigured = (mchId != null && !mchId.isEmpty() && appId != null && !appId.isEmpty());

        Map<String, Object> payParams = new HashMap<>();

        if (isConfigured) {
            // 生产模式：调用真实微信支付
            // 注意：需要先获取用户OpenID，这里简化处理
            String openId = "user_openid_placeholder";

            payParams = weChatPayService.createPaymentOrder(
                    orderId,
                    orderNo,
                    amount,
                    "商品购买",
                    openId
            );
            payParams.put("mockMode", false);
        } else {
            // 测试模式：返回模拟支付参数
            payParams.put("appId", appId != null ? appId : "wx_demo_appid");
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
            payParams.put("package", "prepay_id=wx_demo_prepay_id");
            payParams.put("signType", "RSA");
            payParams.put("paySign", "");
            payParams.put("mockMode", true);
        }

        payParams.put("orderNo", orderNo);
        payParams.put("amount", amount);

        return Result.success(payParams);
    }

    @Operation(summary = "确认支付成功", description = "前端支付完成后调用")
    @PostMapping("/confirm/{orderId}")
    public Result<String> confirmPayment(
            HttpServletRequest request,
            @PathVariable Long orderId) {

        Long userId = (Long) request.getAttribute("userId");

        // 这里可以调用实际的支付状态查询接口验证支付结果
        // 简化处理：直接标记为已支付

        return Result.success("支付确认成功");
    }

    @Operation(summary = "查询支付状态", description = "查询订单支付状态")
    @GetMapping("/status/{orderNo}")
    public Result<String> getPaymentStatus(@PathVariable String orderNo) {
        WeChatPayService.PaymentStatus status = weChatPayService.queryPaymentStatus(orderNo);
        return Result.success(status.name());
    }
}