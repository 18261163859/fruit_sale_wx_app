package com.fruit.sale.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(501, "业务异常"),

    /**
     * Token 过期
     */
    TOKEN_EXPIRED(1001, "Token已过期"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1002, "Token无效"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1003, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(1004, "用户不存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1005, "用户已被禁用"),

    /**
     * 手机号已存在
     */
    PHONE_EXIST(1006, "手机号已存在"),

    /**
     * 验证码错误
     */
    CODE_ERROR(1007, "验证码错误"),

    /**
     * 库存不足
     */
    STOCK_NOT_ENOUGH(2001, "库存不足"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(2002, "订单不存在"),

    /**
     * 订单状态异常
     */
    ORDER_STATUS_ERROR(2003, "订单状态异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
