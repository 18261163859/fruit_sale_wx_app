package com.fruit.sale.controller;

import cn.hutool.json.JSONObject;
import com.fruit.sale.common.Result;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IOrderService;
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

    @Autowired
    private com.fruit.sale.utils.WeChatApiUtil weChatApiUtil;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IOrderService orderService;

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

        // 更新用户的openid
        UserInfo user = userInfoMapper.selectById(userId);
        if (user != null) {
            user.setOpenid(openid);
            userInfoMapper.updateById(user);
            return Result.success("OpenID更新成功");
        }

        return Result.error("用户不存在");
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

//        Map<String, Object> testPayParams = new HashMap<>();
//        testPayParams.put("timeStamp", "1769013834");
//        testPayParams.put("nonceStr", "9c612917c5ff4cf49417f528ccb5dc31");
//        testPayParams.put("package", "prepay_id=wx22004355108488d406c57973914b8b0001");
//        testPayParams.put("orderNo", "ORD2014008265755553792");
//        testPayParams.put("paySign", "VH5S9ZVrRgLbRRnBfm89el5p2FGOQEVtcfsIfj2oMlL6pxntd3EYSNICSlEVbSlrbRrIQ5MqVZdR8vwt6AlHKEzTKc8I3TNzb+P7mVls8SwtpwRjzOi9AL3IDhROSlD44E8/Rg/umUnvoYxDen8QbLuNUhMQ4CIXmzbU8BzXKxO/IhHCzrU64OfyUh1/AosnSRGF3nx4W1jGaHr/KmbiNDWQ2t2CK/uVF25SsqJTwBSXck6FFdwbi2sU1YKjlwgx00LoUTp72ysb45iyDlgsYvoCEKQo01kuVFaOlSALPExpqIW3JA0XYM37xVcb47PkQJXwsYnwI9CM4bRt7XI71w==");
//        testPayParams.put("appId", "wxf7c1e0a5c304e714");
//        testPayParams.put("signType", "RSA");
//        testPayParams.put("amount", 0.02);
//        testPayParams.put("mockMode", false);
//        return Result.success(testPayParams);

        // 获取用户的openid
        UserInfo user = userInfoMapper.selectById(userId);
        String openId = user != null && user.getOpenid() != null ? user.getOpenid() : "user_openid_placeholder";

        // 检查是否配置了微信支付
        boolean isConfigured = (mchId != null && !mchId.isEmpty() && appId != null && !appId.isEmpty());

        Map<String, Object> payParams = new HashMap<>();

        if (isConfigured) {
            // 生产模式：调用真实微信支付
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
}