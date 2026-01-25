package com.fruit.sale.controller;

import cn.hutool.json.JSONObject;
import com.fruit.sale.common.Result;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IOrderService;
import com.fruit.sale.service.IVipService;
import com.fruit.sale.service.WeChatPayService;
import com.fruit.sale.vo.VipOrderVO;
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

    @Autowired
    private com.fruit.sale.utils.WeChatApiUtil weChatApiUtil;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IVipService vipService;

    @Value("${wechat.pay.mchId:}")
    private String mchId;

    @Value("${wechat.pay.appId:}")
    private String appId;

    @Operation(summary = "更新用户OpenID", description = "通过微信登录code获取并更新用户OpenID")
    @PostMapping("/update-openid")
    public Result<String> updateOpenId(
            HttpServletRequest request,
            @RequestParam String code) {

        Long userId = (Long) request.getAttribute("userId");

        // 通过code获取openid
        JSONObject wechatInfo = weChatApiUtil.getOpenIdAndUnionId(code);
        String openid = wechatInfo.getStr("openid");

        if (openid == null || openid.isEmpty()) {
            return Result.error("获取OpenID失败");
        }

        return Result.success("OpenID更新成功");
    }

    @Operation(summary = "获取支付参数", description = "创建微信支付订单并返回支付参数")
    @PostMapping("/params/{orderId}")
    public Result<Map<String, Object>> getPayParams(
            HttpServletRequest request,
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> params) {

        Long userId = (Long) request.getAttribute("userId");

        // 从请求体获取参数
        String orderNo = (String) params.get("orderNo");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        Map<String, Object> payParams = new HashMap<>();

        // 检查是否配置了微信支付
        boolean isConfigured = (mchId != null && !mchId.isEmpty() && appId != null && !appId.isEmpty());

        if (isConfigured) {
            // 生产模式：调用真实微信支付
            // 获取用户的openid
            UserInfo user = userInfoMapper.selectById(userId);
            String openId = user != null ? user.getOpenid() : "user_openid_placeholder";

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

        orderService.payOrder(orderId);

        return Result.success("支付确认成功");
    }

    @Operation(summary = "查询支付状态", description = "查询订单支付状态")
    @GetMapping("/status/{orderNo}")
    public Result<String> getPaymentStatus(@PathVariable String orderNo) {
        WeChatPayService.PaymentStatus status = weChatPayService.queryPaymentStatus(orderNo);
        return Result.success(status.name());
    }

    @Operation(summary = "获取VIP订单支付参数", description = "创建VIP会员订单的微信支付参数")
    @PostMapping("/params/vip/{orderNo}")
    public Result<Map<String, Object>> getVipPayParams(
            HttpServletRequest request,
            @PathVariable String orderNo) {

        Long userId = (Long) request.getAttribute("userId");

        // 获取用户信息
        UserInfo user = userInfoMapper.selectById(userId);
        String openId = user != null ? user.getOpenid() : "user_openid_placeholder";

        // 获取VIP订单信息
        VipOrderVO vipOrder = vipService.getVipOrderByNo(orderNo);

        if (vipOrder == null) {
            return Result.error("VIP订单不存在");
        }

        BigDecimal amount = vipOrder.getActualAmount();

        Map<String, Object> payParams = new HashMap<>();

        // 检查是否配置了微信支付
        boolean isConfigured = (mchId != null && !mchId.isEmpty() && appId != null && !appId.isEmpty());

        if (isConfigured) {
            // 生产模式：调用真实微信支付
            payParams = weChatPayService.createPaymentOrder(
                    vipOrder.getId(),
                    orderNo,
                    amount,
                    "开通星享会员",
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
}