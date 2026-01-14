package com.fruit.sale.constant;

/**
 * Redis Key 常量
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public class RedisKeyConstants {

    /**
     * 用户信息缓存 key 前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * 商品信息缓存 key 前缀
     */
    public static final String PRODUCT_INFO_PREFIX = "product:info:";

    /**
     * 商品分类缓存 key
     */
    public static final String PRODUCT_CATEGORY_LIST = "product:category:list";

    /**
     * 推荐商品列表缓存 key
     */
    public static final String PRODUCT_RECOMMEND_LIST = "product:recommend:list";

    /**
     * 系统配置缓存 key 前缀
     */
    public static final String SYSTEM_CONFIG_PREFIX = "system:config:";

    /**
     * 订单锁 key 前缀
     */
    public static final String ORDER_LOCK_PREFIX = "order:lock:";

    /**
     * 库存锁 key 前缀
     */
    public static final String STOCK_LOCK_PREFIX = "stock:lock:";

    /**
     * 验证码 key 前缀
     */
    public static final String VERIFY_CODE_PREFIX = "verify:code:";
}
