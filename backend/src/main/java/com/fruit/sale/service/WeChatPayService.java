package com.fruit.sale.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.utils.WeChatPaySignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付服务
 *
 * @author fruit-sale
 * @since 2025-01-14
 */
@Slf4j
@Service
public class WeChatPayService {

    @Value("${wechat.pay.mchId:}")
    private String mchId;

    @Value("${wechat.pay.appId:}")
    private String appId;

    @Value("${wechat.pay.certificateSerialNo:}")
    private String certificateSerialNo;

    @Value("${wechat.pay.privateKeyPath:}")
    private String privateKeyPath;

    @Value("${wechat.pay.notifyUrl:}")
    private String notifyUrl;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        // 加载商户私钥
        if (privateKeyPath != null && !privateKeyPath.isEmpty() && !privateKeyPath.equals("your_private_key_path")) {
            try {
                this.privateKey = WeChatPaySignUtil.loadPrivateKeyFromPath(privateKeyPath);
                log.info("微信支付商户私钥加载成功");
            } catch (Exception e) {
                log.error("加载微信支付商户私钥失败: {}", privateKeyPath, e);
            }
        } else {
            log.warn("未配置微信支付商户私钥路径，将使用测试模式");
        }
    }

    /**
     * 创建支付订单（统一下单）
     */
    public Map<String, Object> createPaymentOrder(Long orderId, String orderNo, BigDecimal amount, String description, String openId) {
        // 检查配置
        if (mchId == null || mchId.isEmpty() || appId == null || appId.isEmpty()) {
            throw new BusinessException("微信支付配置不完整，请检查配置文件");
        }

        if (privateKey == null) {
            throw new BusinessException("商户私钥未加载，无法创建支付订单");
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
        requestBody.put("notify_url", notifyUrl != null && !notifyUrl.isEmpty() ? notifyUrl : "https://your-domain.com/api/pay/notify");

        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("total", amountInt);
        amountMap.put("currency", "CNY");
        requestBody.put("amount", amountMap);

        Map<String, String> payer = new HashMap<>();
        payer.put("openid", openId);
        requestBody.put("payer", payer);

        String jsonBody = JSON.toJSONString(requestBody);
        String url = "/v3/pay/transactions/jsapi";

        // 构建Authorization头
        String authorization = WeChatPaySignUtil.buildAuthorization(
                mchId,
                certificateSerialNo,
                privateKey,
                "POST",
                url,
                jsonBody
        );

        // 调用微信统一下单API
        HttpResponse response = HttpRequest.post("https://api.mch.weixin.qq.com" + url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authorization)
                .body(jsonBody)
                .execute();

        String responseBody = response.body();
        log.info("微信支付下单响应: {}", responseBody);

        if (response.getStatus() != 200) {
            throw new BusinessException("创建支付订单失败: " + responseBody);
        }

        JSONObject result = JSON.parseObject(responseBody);
        String prepayId = result.getString("prepay_id");

        if (prepayId == null || prepayId.isEmpty()) {
            throw new BusinessException("创建支付订单失败: " + responseBody);
        }

        // 构建小程序支付参数
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStrForPay = UUID.randomUUID().toString().replace("-", "");
        String packageStr = "prepay_id=" + prepayId;

        // 生成小程序支付签名
        String paySignMessage = appId + "\n" + timeStamp + "\n" + nonceStrForPay + "\n" + packageStr + "\n";
        String paySign;
        try {
            paySign = sign(paySignMessage);
        } catch (Exception e) {
            log.error("生成支付签名失败", e);
            throw new BusinessException("生成支付签名失败");
        }

        // 返回前端需要的支付参数
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("appId", appId);
        payParams.put("timeStamp", timeStamp);
        payParams.put("nonceStr", nonceStrForPay);
        payParams.put("package", packageStr);
        payParams.put("signType", "RSA");
        payParams.put("paySign", paySign);

        return payParams;
    }

    /**
     * 生成支付签名
     */
    private String sign(String message) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 查询支付状态
     */
    public PaymentStatus queryPaymentStatus(String orderNo) {
        if (mchId == null || mchId.isEmpty() || privateKey == null) {
            return PaymentStatus.UNKNOWN;
        }

        String url = String.format("/v3/pay/transactions/out-trade-no/%s?mchid=%s", orderNo, mchId);

        // 构建Authorization头
        String authorization = WeChatPaySignUtil.buildAuthorization(
                mchId,
                certificateSerialNo,
                privateKey,
                "GET",
                url,
                ""
        );

        HttpResponse response = HttpRequest.get("https://api.mch.weixin.qq.com" + url)
                .header("Accept", "application/json")
                .header("Authorization", authorization)
                .execute();

        String responseBody = response.body();

        if (response.getStatus() != 200 || responseBody == null || responseBody.isEmpty()) {
            return PaymentStatus.NOT_FOUND;
        }

        JSONObject result = JSON.parseObject(responseBody);
        String tradeState = result.getString("trade_state");

        return switch (tradeState) {
            case "SUCCESS" -> PaymentStatus.SUCCESS;
            case "CLOSED" -> PaymentStatus.CLOSED;
            case "PAYERROR" -> PaymentStatus.FAILED;
            default -> PaymentStatus.UNKNOWN;
        };
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
