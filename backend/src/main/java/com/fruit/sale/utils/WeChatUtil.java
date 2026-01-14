package com.fruit.sale.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 微信API工具类
 *
 * @author fruit-sale
 * @since 2025-10-08
 */
@Slf4j
@Component
public class WeChatUtil {

    @Value("${wechat.miniapp.appid:}")
    private String appid;

    @Value("${wechat.miniapp.secret:}")
    private String secret;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";
    private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String GET_PHONE_NUMBER_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    public WeChatUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取access_token（带缓存）
     */
    public String getAccessToken() {
        // 先从Redis获取
        String accessToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
        if (accessToken != null && !accessToken.isEmpty()) {
            return accessToken;
        }

        // Redis中没有，调用微信API获取
        String url = String.format(GET_ACCESS_TOKEN_URL, appid, secret);
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);

        if (json.containsKey("access_token")) {
            accessToken = json.getStr("access_token");
            Integer expiresIn = json.getInt("expires_in", 7200);

            // 存入Redis，提前5分钟过期
            redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, expiresIn - 300, TimeUnit.SECONDS);

            log.info("成功获取微信access_token");
            return accessToken;
        } else {
            log.error("获取微信access_token失败: {}", response);
            throw new RuntimeException("获取微信access_token失败: " + json.getStr("errmsg"));
        }
    }

    /**
     * 通过phoneCode获取用户手机号
     *
     * @param phoneCode 前端获取的手机号code
     * @return 手机号
     */
    public String getPhoneNumber(String phoneCode) {
        if (appid == null || appid.isEmpty() || "your_appid_here".equals(appid)) {
            log.warn("未配置微信小程序appid，无法获取真实手机号");
            // 返回临时手机号用于测试
            return "138****" + (System.currentTimeMillis() % 10000);
        }

        try {
            String accessToken = getAccessToken();
            String url = String.format(GET_PHONE_NUMBER_URL, accessToken);

            JSONObject requestBody = new JSONObject();
            requestBody.set("code", phoneCode);

            String response = HttpUtil.post(url, requestBody.toString());
            JSONObject json = JSONUtil.parseObj(response);

            if (json.getInt("errcode", -1) == 0) {
                JSONObject phoneInfo = json.getJSONObject("phone_info");
                String phoneNumber = phoneInfo.getStr("phoneNumber");
                log.info("成功获取用户手机号: {}", phoneNumber);
                return phoneNumber;
            } else {
                log.error("获取用户手机号失败: {}", response);
                // 失败时返回临时手机号
                return "138****" + (System.currentTimeMillis() % 10000);
            }
        } catch (Exception e) {
            log.error("调用微信API获取手机号异常", e);
            // 异常时返回临时手机号
            return "138****" + (System.currentTimeMillis() % 10000);
        }
    }
}
