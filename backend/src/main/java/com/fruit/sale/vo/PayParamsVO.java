package com.fruit.sale.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 支付参数VO
 */
@Data
public class PayParamsVO {
    /**
     * 小程序AppID
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 订单详情扩展字符串
     */
    private String packageStr;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 签名
     */
    private String paySign;

    /**
     * 是否模拟支付模式
     */
    private Boolean mockMode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;
}