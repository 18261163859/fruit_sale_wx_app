package com.fruit.sale.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * 微信支付签名工具类
 *
 * @author fruit-sale
 * @since 2026-01-22
 */
@Slf4j
public class WeChatPaySignUtil {

    /**
     * 构建Authorization头
     */
    public static String buildAuthorization(String mchid, String serialNo, PrivateKey privateKey,
                                             String method, String url, String body) {
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            String nonceStr = UUID.randomUUID().toString().replace("-", "");

            // 构建签名串
            String message = buildMessage(method, url, timestamp, nonceStr, body);

            // 生成签名
            String signature = sign(message, privateKey);

            // 构建Authorization值
            return String.format("WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",timestamp=\"%d\",serial_no=\"%s\",signature=\"%s\"",
                    mchid, nonceStr, timestamp, serialNo, signature);
        } catch (Exception e) {
            log.error("构建Authorization失败", e);
            throw new RuntimeException("构建Authorization失败", e);
        }
    }

    /**
     * 构建待签名字符串
     */
    private static String buildMessage(String method, String url, long timestamp, String nonceStr, String body) {
        return method + "\n" +
                url + "\n" +
                timestamp + "\n" +
                nonceStr + "\n" +
                body + "\n";
    }

    /**
     * 使用私钥对内容进行签名
     */
    private static String sign(String message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 从文件路径加载私钥（支持classpath和文件系统路径）
     */
    public static PrivateKey loadPrivateKeyFromPath(String filePath) {
        try {
            String content;
            if (filePath.startsWith("classpath:")) {
                // 从classpath加载
                String resourcePath = filePath.substring("classpath:".length());
                java.io.InputStream inputStream = WeChatPaySignUtil.class.getClassLoader().getResourceAsStream(resourcePath);
                if (inputStream == null) {
                    throw new RuntimeException("找不到classpath资源: " + resourcePath);
                }
                content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                // 从文件系统加载
                content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            }
            return loadPrivateKeyFromString(content);
        } catch (IOException e) {
            log.error("读取私钥文件失败: {}", filePath, e);
            throw new RuntimeException("读取私钥文件失败", e);
        }
    }

    /**
     * 从字符串加载私钥
     */
    public static PrivateKey loadPrivateKeyFromString(String privateKeyPEM) {
        try {
            // 移除PEM头尾和换行符
            String privateKeyContent = privateKeyPEM
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("加载私钥失败", e);
            throw new RuntimeException("加载私钥失败", e);
        }
    }

    /**
     * 验证响应签名
     */
    public static boolean verifySignature(String timestamp, String nonce, String body, String signature, PublicKey publicKey) {
        try {
            String message = timestamp + "\n" + nonce + "\n" + body + "\n";
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes(StandardCharsets.UTF_8));
            return sign.verify(Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            log.error("验证签名失败", e);
            return false;
        }
    }
}
