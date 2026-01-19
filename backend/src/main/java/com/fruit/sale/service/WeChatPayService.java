package com.fruit.sale.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fruit.sale.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付服务
 *
 * @author fruit-sale
 * @since 2025-01-14
 */
@Service
public class WeChatPayService {

    @Value("${wechat.pay.mchId:}")
    private String mchId;

    @Value("${wechat.pay.mchKey:}")
    private String mchKey;

    @Value("${wechat.pay.notifyUrl:}")
    private String notifyUrl;

    @Value("${wechat.pay.appId:}")
    private String appId;

    /**
     * 创建支付订单（统一下单）
     */
    public Map<String, Object> createPaymentOrder(Long orderId, String orderNo, BigDecimal amount, String description, String openId) {
        // 检查配置
        if (mchId == null || mchId.isEmpty() || appId == null || appId.isEmpty()) {
            throw new BusinessException("微信支付配置不完整，请检查配置文件");
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        int amountInt = amount.multiply(new BigDecimal("100")).setScale(0).toBigInteger().intValue();

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appid", appId);
        requestBody.put("mchid", mchId);
        requestBody.put("description", description);
        requestBody.put("out_trade_no", orderNo);
        requestBody.put("notify_url", notifyUrl != null ? notifyUrl : "https://your-domain.com/api/pay/notify");

        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("total", amountInt);
        amountMap.put("currency", "CNY");
        requestBody.put("amount", amountMap);

        Map<String, String> payer = new HashMap<>();
        payer.put("openid", openId);
        requestBody.put("payer", payer);

        String jsonBody = JSON.toJSONString(requestBody);

        // 调用微信统一下单API
        HttpRequest request = HttpRequest.post("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonBody);

        String response = request.execute().body();

        JSONObject result = JSON.parseObject(response);
        String prepayId = result.getString("prepay_id");

        if (prepayId == null || prepayId.isEmpty()) {
            throw new BusinessException("创建支付订单失败: " + response);
        }

        // 返回前端需要的支付参数
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("appId", appId);
        payParams.put("timeStamp", timestamp);
        payParams.put("nonceStr", nonceStr);
        payParams.put("package", "prepay_id=" + prepayId);
        payParams.put("signType", "RSA");
        // 注意：生产环境需要使用商户私钥进行签名
        payParams.put("paySign", "");

        return payParams;
    }

    /**
     * 查询支付状态
     */
    public PaymentStatus queryPaymentStatus(String orderNo) {
        if (mchId == null || mchId.isEmpty()) {
            return PaymentStatus.UNKNOWN;
        }

        String url = String.format("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s",
                orderNo, mchId);

        HttpRequest request = HttpRequest.get(url)
                .header("Accept", "application/json");

        String response = request.execute().body();

        if (response == null || response.isEmpty()) {
            return PaymentStatus.NOT_FOUND;
        }

        JSONObject result = JSON.parseObject(response);
        String tradeState = result.getString("trade_state");

        return switch (tradeState) {
            case "SUCCESS" -> PaymentStatus.SUCCESS;
            case "CLOSED" -> PaymentStatus.CLOSED;
            case "PAY_ERROR" -> PaymentStatus.FAILED;
            default -> PaymentStatus.UNKNOWN;
        };
    }

    /**
     * 获取微信支付APIv3密钥
     */
    public String getApiV3Key() {
        return mchKey;
    }

    /**
     * 支付状态枚举
     */
    public enum PaymentStatus {
        SUCCESS,     // 支付成功
        FAILED,      // 支付失败
        CLOSED,      // 已关闭
        NOT_FOUND,   // 订单不存在
        UNKNOWN      // 未知状态
    }
}