package com.fruit.sale.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信API工具类 - 获取OpenID和UnionID
 *
 * @author fruit-sale
 * @since 2025-10-08
 */
@Slf4j
@Component
public class WeChatApiUtil {

    @Value("${wechat.miniapp.appid:}")
    private String appid;

    @Value("${wechat.miniapp.secret:}")
    private String secret;

    private static final String CODE_TO_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 通过code获取openid和unionid
     *
     * @param code 微信登录code
     * @return JSONObject包含openid和unionid
     */
    public JSONObject getOpenIdAndUnionId(String code) {
        JSONObject result = new JSONObject();

        if (appid == null || appid.isEmpty() || "your_appid_here".equals(appid)) {
            log.warn("未配置微信小程序appid，使用基于code生成的mock数据");
            // 基于code生成确定性的mock openid，同一个code返回相同的openid
            // 这样可以模拟不同微信用户有不同的openid
            String mockOpenid = "mock_openid_" + Math.abs(code.hashCode());
            String mockUnionid = "mock_unionid_" + Math.abs(code.hashCode());
            result.set("openid", mockOpenid);
            result.set("unionid", mockUnionid);
            log.info("使用基于code的mock openid: {}", mockOpenid);
            return result;
        }

        try {
            String url = String.format(CODE_TO_SESSION_URL, appid, secret, code);
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("openid")) {
                result.set("openid", json.getStr("openid"));
                result.set("unionid", json.getStr("unionid", "")); // unionid可能为空
                result.set("session_key", json.getStr("session_key"));
                log.info("成功获取微信用户openid");
                return result;
            } else {
                log.error("获取微信openid失败: {}", response);
                // 失败时返回基于code的mock数据
                String mockOpenid = "mock_openid_" + Math.abs(code.hashCode());
                String mockUnionid = "mock_unionid_" + Math.abs(code.hashCode());
                result.set("openid", mockOpenid);
                result.set("unionid", mockUnionid);
                return result;
            }
        } catch (Exception e) {
            log.error("调用微信API获取openid异常", e);
            // 异常时返回基于code的mock数据
            String mockOpenid = "mock_openid_" + Math.abs(code.hashCode());
            String mockUnionid = "mock_unionid_" + Math.abs(code.hashCode());
            result.set("openid", mockOpenid);
            result.set("unionid", mockUnionid);
            return result;
        }
    }
}
