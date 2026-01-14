-- =============================================
-- 高端云南水果销售小程序 - 初始化数据
-- =============================================

USE fruit_sale;

-- =============================================
-- 1. 管理员账号初始化
-- =============================================

-- 插入超级管理员 (用户名: admin, 密码: admin123)
INSERT INTO `admin_user` (`username`, `password`, `real_name`, `phone`, `email`, `role_type`, `status`, `create_time`, `update_time`)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIzYoAYFJgA8HGF/a', '系统管理员', '13800138000', 'admin@fruit.com', 1, 1, NOW(), NOW()),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIzYoAYFJgA8HGF/a', '普通管理员', '13800138001', 'manager@fruit.com', 2, 1, NOW(), NOW()),
('shipper', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIzYoAYFJgA8HGF/a', '发货员', '13800138002', 'shipper@fruit.com', 3, 1, NOW(), NOW());

-- =============================================
-- 2. 系统配置初始化（使用INSERT IGNORE避免重复）
-- =============================================

INSERT IGNORE INTO `system_config` (`config_key`, `config_value`, `config_desc`, `config_type`, `create_time`, `update_time`) VALUES
-- 会员配置
('vip_price', '199.00', '星享会员开通价格', 'decimal', NOW(), NOW()),
('vip_discount', '0.95', '星享会员折扣（95折）', 'decimal', NOW(), NOW()),
('vip_valid_days', '365', '星享会员有效期（天）', 'int', NOW(), NOW()),

-- 积分配置
('integral_exchange_rate', '100', '积分兑换比例（100积分=1元）', 'int', NOW(), NOW()),
('order_integral_rate', '0.01', '订单积分比例（1元=1积分）', 'decimal', NOW(), NOW()),
('share_reward_rate', '0.05', '分享奖励比例（5%转为积分）', 'decimal', NOW(), NOW()),

-- 返现配置
('min_commission_threshold', '100.00', '最低返现门槛金额', 'decimal', NOW(), NOW()),
('level1_commission_rate', '0.10', '一级代理默认返现比例（10%）', 'decimal', NOW(), NOW()),
('level2_commission_rate', '0.10', '二级代理默认返现比例（10%）', 'decimal', NOW(), NOW()),

-- 运费配置
('default_freight', '15.00', '默认运费', 'decimal', NOW(), NOW()),
('free_freight_threshold', '199.00', '包邮门槛金额', 'decimal', NOW(), NOW()),

-- 主题配置
('newyear_theme_start', '12-30', '新年主题开始日期（农历腊月三十）', 'string', NOW(), NOW()),
('newyear_theme_end', '01-15', '新年主题结束日期（农历正月十五）', 'string', NOW(), NOW()),

-- 网站信息
('site_name', '高端云南水果', '网站名称', 'string', NOW(), NOW()),
('site_logo', '', '网站Logo URL', 'string', NOW(), NOW()),
('company_name', '云南优果农业科技有限公司', '公司名称', 'string', NOW(), NOW()),
('service_phone', '400-888-8888', '客服电话', 'string', NOW(), NOW()),
('company_address', '云南省昆明市盘龙区果品批发市场', '公司地址', 'string', NOW(), NOW());

-- =============================================
-- 3. 商品分类初始化
-- =============================================

-- 一级分类
INSERT INTO `product_category` (`category_name`, `category_icon`, `parent_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
('热带水果', 'https://example.com/icons/tropical.png', 0, 1, 1, NOW(), NOW()),
('温带水果', 'https://example.com/icons/temperate.png', 0, 2, 1, NOW(), NOW()),
('高原水果', 'https://example.com/icons/plateau.png', 0, 3, 1, NOW(), NOW()),
('进口水果', 'https://example.com/icons/imported.png', 0, 4, 1, NOW(), NOW()),
('干果坚果', 'https://example.com/icons/nuts.png', 0, 5, 1, NOW(), NOW());

-- 获取一级分类ID
SET @tropical_id = (SELECT id FROM product_category WHERE category_name='热带水果' AND parent_id=0 LIMIT 1);
SET @temperate_id = (SELECT id FROM product_category WHERE category_name='温带水果' AND parent_id=0 LIMIT 1);

-- 二级分类（热带水果）
INSERT INTO `product_category` (`category_name`, `parent_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
('芒果', @tropical_id, 1, 1, NOW(), NOW()),
('火龙果', @tropical_id, 2, 1, NOW(), NOW()),
('香蕉', @tropical_id, 3, 1, NOW(), NOW()),
('菠萝', @tropical_id, 4, 1, NOW(), NOW());

-- 二级分类（温带水果）
INSERT INTO `product_category` (`category_name`, `parent_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
('苹果', @temperate_id, 1, 1, NOW(), NOW()),
('梨', @temperate_id, 2, 1, NOW(), NOW()),
('桃子', @temperate_id, 3, 1, NOW(), NOW()),
('葡萄', @temperate_id, 4, 1, NOW(), NOW());

-- =============================================
-- 4. 轮播图初始化
-- =============================================

INSERT INTO `banner_config` (`banner_title`, `banner_image`, `banner_type`, `link_type`, `link_url`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
('夏日芒果季', 'https://example.com/banners/mango.jpg', 1, 2, '/pages/category/category?id=6', 1, 1, NOW(), NOW()),
('新鲜火龙果上市', 'https://example.com/banners/pitaya.jpg', 1, 2, '/pages/category/category?id=7', 2, 1, NOW(), NOW()),
('品质水果 源自云南', 'https://example.com/banners/intro.jpg', 1, NULL, NULL, 3, 1, NOW(), NOW()),
('会员专享95折', 'https://example.com/banners/vip.jpg', 1, 1, '/pages/open-vip/open-vip', 4, 1, NOW(), NOW());

-- =============================================
-- 5. 示例商品数据
-- =============================================

-- 获取分类ID（需要根据实际插入的ID调整）
SET @category_mango = (SELECT id FROM product_category WHERE category_name='芒果' LIMIT 1);
SET @category_pitaya = (SELECT id FROM product_category WHERE category_name='火龙果' LIMIT 1);
SET @category_apple = (SELECT id FROM product_category WHERE category_name='苹果' LIMIT 1);
SET @category_grape = (SELECT id FROM product_category WHERE category_name='葡萄' LIMIT 1);

INSERT INTO `product_info` (`product_no`, `product_name`, `category_id`, `main_image`, `image_list`, `price`, `vip_price`, `stock`, `sales_count`, `description`, `product_detail`, `spec_info`, `max_buy_count`, `status`, `sort_order`, `is_recommend`, `create_time`, `update_time`) VALUES

-- 芒果系列
('P2024093001', '云南攀枝花凯特芒果', @category_mango, 'https://example.com/products/mango1.jpg',
'["https://example.com/products/mango1-1.jpg","https://example.com/products/mango1-2.jpg","https://example.com/products/mango1-3.jpg"]',
89.00, 84.55, 500, 128, '新鲜采摘，香甜多汁，肉质细腻',
'<p>攀枝花凯特芒果，产自云南攀枝花，果肉金黄，香甜可口。</p><p>产品规格：5斤装/箱</p><p>保存方式：常温保存3-5天，冷藏可保存7天</p>',
'{"weight":"5斤","origin":"云南攀枝花"}', 10, 1, 1, 1, NOW(), NOW()),

('P2024093002', '玉溪象牙芒果礼盒装', @category_mango, 'https://example.com/products/mango2.jpg',
'["https://example.com/products/mango2-1.jpg","https://example.com/products/mango2-2.jpg"]',
128.00, 121.60, 300, 86, '精品礼盒装，送礼佳品',
'<p>玉溪象牙芒果，果型修长，香气浓郁。</p><p>产品规格：3斤精品礼盒装</p>',
'{"weight":"3斤","origin":"云南玉溪","package":"礼盒装"}', 5, 1, 2, 1, NOW(), NOW()),

-- 火龙果系列
('P2024093003', '红心火龙果', @category_pitaya, 'https://example.com/products/pitaya1.jpg',
'["https://example.com/products/pitaya1-1.jpg","https://example.com/products/pitaya1-2.jpg"]',
68.00, 64.60, 800, 256, '红心火龙果，营养丰富，口感软糯',
'<p>云南红心火龙果，富含花青素和维生素。</p><p>产品规格：6个装/箱（约4斤）</p>',
'{"weight":"4斤","count":"6个","origin":"云南"}', 10, 1, 3, 1, NOW(), NOW()),

('P2024093004', '白心火龙果', @category_pitaya, 'https://example.com/products/pitaya2.jpg',
'["https://example.com/products/pitaya2-1.jpg","https://example.com/products/pitaya2-2.jpg"]',
48.00, 45.60, 600, 189, '白心火龙果，清甜爽口',
'<p>新鲜白心火龙果，果肉多汁。</p><p>产品规格：6个装/箱（约4斤）</p>',
'{"weight":"4斤","count":"6个","origin":"云南"}', 10, 1, 4, 1, NOW(), NOW()),

-- 苹果系列
('P2024093005', '昭通冰糖心苹果', @category_apple, 'https://example.com/products/apple1.jpg',
'["https://example.com/products/apple1-1.jpg","https://example.com/products/apple1-2.jpg"]',
98.00, 93.10, 1000, 412, '高海拔种植，天然糖心',
'<p>昭通冰糖心苹果，产自海拔2000米以上高原。</p><p>产品规格：10斤装/箱（约15-18个）</p>',
'{"weight":"10斤","origin":"云南昭通"}', 20, 1, 5, 1, NOW(), NOW()),

('P2024093006', '红富士苹果礼盒', @category_apple, 'https://example.com/products/apple2.jpg',
'["https://example.com/products/apple2-1.jpg","https://example.com/products/apple2-2.jpg"]',
158.00, 150.10, 500, 95, '精选大果，礼盒包装',
'<p>精选红富士苹果，果径80mm以上。</p><p>产品规格：12个精品礼盒装（约5斤）</p>',
'{"weight":"5斤","count":"12个","package":"礼盒装"}', 10, 1, 6, 0, NOW(), NOW()),

-- 葡萄系列
('P2024093007', '宾川阳光玫瑰葡萄', @category_grape, 'https://example.com/products/grape1.jpg',
'["https://example.com/products/grape1-1.jpg","https://example.com/products/grape1-2.jpg","https://example.com/products/grape1-3.jpg"]',
138.00, 131.10, 400, 167, '阳光玫瑰，果香浓郁，无籽品种',
'<p>宾川阳光玫瑰葡萄，玫瑰香气，脆甜无籽。</p><p>产品规格：3斤装/箱</p>',
'{"weight":"3斤","origin":"云南宾川","feature":"无籽"}', 5, 1, 7, 1, NOW(), NOW()),

('P2024093008', '夏黑葡萄', @category_grape, 'https://example.com/products/grape2.jpg',
'["https://example.com/products/grape2-1.jpg","https://example.com/products/grape2-2.jpg"]',
78.00, 74.10, 600, 234, '夏黑葡萄，果粒饱满，清甜多汁',
'<p>夏黑葡萄，果皮薄，含糖量高。</p><p>产品规格：5斤装/箱</p>',
'{"weight":"5斤","origin":"云南"}', 10, 1, 8, 1, NOW(), NOW());

-- =============================================
-- 6. 测试用户数据（用于测试）
-- =============================================

-- 插入测试普通会员
INSERT INTO `user_info` (`user_no`, `phone`, `nickname`, `avatar_url`, `openid`, `user_type`, `integral_balance`, `status`, `register_time`, `create_time`, `update_time`) VALUES
('U2024093001', '13900000001', '张三', 'https://example.com/avatar/user1.jpg', 'openid_test_001', 1, 500, 1, NOW(), NOW(), NOW()),
('U2024093002', '13900000002', '李四', 'https://example.com/avatar/user2.jpg', 'openid_test_002', 1, 300, 1, NOW(), NOW(), NOW());

-- 插入测试星享会员
INSERT INTO `user_info` (`user_no`, `phone`, `nickname`, `avatar_url`, `openid`, `user_type`, `vip_expire_time`, `integral_balance`, `status`, `register_time`, `create_time`, `update_time`) VALUES
('U2024093003', '13900000003', '王五', 'https://example.com/avatar/user3.jpg', 'openid_test_003', 2, DATE_ADD(NOW(), INTERVAL 365 DAY), 1000, 1, NOW(), NOW(), NOW());

-- 插入测试一级代理
INSERT INTO `user_info` (`user_no`, `phone`, `nickname`, `avatar_url`, `openid`, `user_type`, `vip_expire_time`, `integral_balance`, `invite_code`, `agent_level`, `commission_rate`, `status`, `register_time`, `create_time`, `update_time`) VALUES
('U2024093004', '13900000004', '赵六', 'https://example.com/avatar/user4.jpg', 'openid_test_004', 3, DATE_ADD(NOW(), INTERVAL 365 DAY), 2000, 'AGENT001', 1, 10.00, 1, NOW(), NOW(), NOW());

-- 插入测试二级代理（赵六的下级）
INSERT INTO `user_info` (`user_no`, `phone`, `nickname`, `avatar_url`, `openid`, `user_type`, `vip_expire_time`, `integral_balance`, `invite_code`, `inviter_user_id`, `agent_level`, `parent_agent_id`, `commission_rate`, `status`, `register_time`, `create_time`, `update_time`) VALUES
('U2024093005', '13900000005', '孙七', 'https://example.com/avatar/user5.jpg', 'openid_test_005', 4, DATE_ADD(NOW(), INTERVAL 365 DAY), 1500, 'AGENT002',
(SELECT id FROM (SELECT id FROM user_info WHERE user_no='U2024093004') AS tmp), 2,
(SELECT id FROM (SELECT id FROM user_info WHERE user_no='U2024093004') AS tmp), 10.00, 1, NOW(), NOW(), NOW());

-- =============================================
-- 7. 测试收货地址
-- =============================================

SET @user1_id = (SELECT id FROM user_info WHERE user_no='U2024093001' LIMIT 1);
SET @user2_id = (SELECT id FROM user_info WHERE user_no='U2024093002' LIMIT 1);

INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `address_tag`, `is_default`, `create_time`, `update_time`) VALUES
(@user1_id, '张三', '13900000001', '云南省', '昆明市', '盘龙区', '北京路123号', '家', 1, NOW(), NOW()),
(@user1_id, '张三', '13900000001', '云南省', '昆明市', '五华区', '人民路456号', '公司', 0, NOW(), NOW()),
(@user2_id, '李四', '13900000002', '云南省', '大理白族自治州', '大理市', '下关镇789号', '家', 1, NOW(), NOW());

-- =============================================
-- 8. 积分充值卡示例（用于测试）
-- =============================================

INSERT INTO `integral_card` (`card_no`, `card_code`, `integral_amount`, `card_status`, `expire_time`, `batch_no`, `create_time`, `update_time`) VALUES
('CARD202409300001', 'ABCD1234EFGH5678', 1000, 0, DATE_ADD(NOW(), INTERVAL 180 DAY), 'BATCH001', NOW(), NOW()),
('CARD202409300002', 'WXYZ9876IJKL4321', 2000, 0, DATE_ADD(NOW(), INTERVAL 180 DAY), 'BATCH001', NOW(), NOW()),
('CARD202409300003', 'MNOP5678QRST1234', 500, 0, DATE_ADD(NOW(), INTERVAL 180 DAY), 'BATCH001', NOW(), NOW()),
('CARD202409300004', 'UVWX3456ABCD7890', 1000, 1, DATE_ADD(NOW(), INTERVAL 180 DAY), 'BATCH001', NOW(), NOW()),
('CARD202409300005', 'EFGH7890IJKL2345', 3000, 0, DATE_ADD(NOW(), INTERVAL 180 DAY), 'BATCH002', NOW(), NOW());

-- 标记一张卡为已使用
UPDATE `integral_card` SET `card_status` = 1, `use_user_id` = @user1_id, `use_time` = NOW() WHERE `card_no` = 'CARD202409300004';

-- =============================================
-- 9. 示例订单数据（用于测试展示）
-- =============================================

-- 创建测试订单1（待付款）
INSERT INTO `order_info` (
    `order_no`, `user_id`, `user_type`, `total_amount`, `discount_amount`, `freight_amount`, `actual_amount`,
    `order_status`, `pay_status`,
    `receiver_name`, `receiver_phone`, `receiver_province`, `receiver_city`, `receiver_district`, `receiver_address`,
    `create_time`, `update_time`
) VALUES (
    'ORD202409300001', @user1_id, 1, 89.00, 0.00, 15.00, 104.00,
    0, 0,
    '张三', '13900000001', '云南省', '昆明市', '盘龙区', '北京路123号',
    NOW(), NOW()
);

-- 创建测试订单2（待发货）
INSERT INTO `order_info` (
    `order_no`, `user_id`, `user_type`, `total_amount`, `discount_amount`, `freight_amount`, `actual_amount`,
    `order_status`, `pay_status`, `pay_type`, `pay_time`, `transaction_id`,
    `receiver_name`, `receiver_phone`, `receiver_province`, `receiver_city`, `receiver_district`, `receiver_address`,
    `create_time`, `update_time`
) VALUES (
    'ORD202409300002', @user2_id, 2, 68.00, 3.40, 0.00, 64.60,
    1, 1, 1, NOW(), 'WX20240930001',
    '李四', '13900000002', '云南省', '大理白族自治州', '大理市', '下关镇789号',
    DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()
);

-- 创建测试订单3（已发货）
INSERT INTO `order_info` (
    `order_no`, `user_id`, `user_type`, `total_amount`, `discount_amount`, `freight_amount`, `actual_amount`,
    `order_status`, `pay_status`, `pay_type`, `pay_time`, `transaction_id`,
    `receiver_name`, `receiver_phone`, `receiver_province`, `receiver_city`, `receiver_district`, `receiver_address`,
    `create_time`, `update_time`
) VALUES (
    'ORD202409300003', @user1_id, 1, 138.00, 0.00, 0.00, 138.00,
    2, 1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 'WX20240928001',
    '张三', '13900000001', '云南省', '昆明市', '盘龙区', '北京路123号',
    DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()
);

-- 获取订单ID
SET @order1_id = (SELECT id FROM order_info WHERE order_no='ORD202409300001' LIMIT 1);
SET @order2_id = (SELECT id FROM order_info WHERE order_no='ORD202409300002' LIMIT 1);
SET @order3_id = (SELECT id FROM order_info WHERE order_no='ORD202409300003' LIMIT 1);

-- 创建订单商品明细
INSERT INTO `order_item` (`order_id`, `order_no`, `product_id`, `product_no`, `product_name`, `product_image`, `product_price`, `actual_price`, `quantity`, `total_amount`, `create_time`, `update_time`) VALUES
(@order1_id, 'ORD202409300001', 1, 'P2024093001', '云南攀枝花凯特芒果', 'https://example.com/products/mango1.jpg', 89.00, 89.00, 1, 89.00, NOW(), NOW()),
(@order2_id, 'ORD202409300002', 3, 'P2024093003', '红心火龙果', 'https://example.com/products/pitaya1.jpg', 68.00, 64.60, 1, 64.60, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(@order3_id, 'ORD202409300003', 7, 'P2024093007', '宾川阳光玫瑰葡萄', 'https://example.com/products/grape1.jpg', 138.00, 138.00, 1, 138.00, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- 创建物流信息（已发货订单）
INSERT INTO `order_logistics` (`order_id`, `order_no`, `express_company`, `express_no`, `ship_time`, `create_time`, `update_time`) VALUES
(@order3_id, 'ORD202409300003', '顺丰速运', 'SF1234567890', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW());

-- =============================================
-- 10. 完成数据统计
-- =============================================

SELECT '数据初始化完成！' AS message;

-- 统计信息
SELECT '管理员账号' AS category, COUNT(*) AS count FROM admin_user
UNION ALL
SELECT '系统配置' AS category, COUNT(*) AS count FROM system_config
UNION ALL
SELECT '商品分类' AS category, COUNT(*) AS count FROM product_category
UNION ALL
SELECT '轮播图' AS category, COUNT(*) AS count FROM banner_config
UNION ALL
SELECT '商品信息' AS category, COUNT(*) AS count FROM product_info
UNION ALL
SELECT '测试用户' AS category, COUNT(*) AS count FROM user_info
UNION ALL
SELECT '收货地址' AS category, COUNT(*) AS count FROM user_address
UNION ALL
SELECT '积分充值卡' AS category, COUNT(*) AS count FROM integral_card
UNION ALL
SELECT '测试订单' AS category, COUNT(*) AS count FROM order_info;

-- =============================================
-- 使用说明
-- =============================================

/*
初始化账号信息：

【后台管理员账号】
1. 超级管理员
   用户名：admin
   密码：admin123

2. 普通管理员
   用户名：manager
   密码：admin123

3. 发货员
   用户名：shipper
   密码：admin123

【测试用户】
- 普通会员：张三 (13900000001)、李四 (13900000002)
- 星享会员：王五 (13900000003)
- 一级代理：赵六 (13900000004) 邀请码：AGENT001
- 二级代理：孙七 (13900000005) 邀请码：AGENT002

【测试积分卡】
可用兑换码：
- ABCD1234EFGH5678 (1000积分)
- WXYZ9876IJKL4321 (2000积分)
- MNOP5678QRST1234 (500积分)
- EFGH7890IJKL2345 (3000积分)

【示例商品】
已创建8个示例商品，涵盖芒果、火龙果、苹果、葡萄等分类

【示例订单】
- 订单1：待付款状态
- 订单2：待发货状态
- 订单3：已发货状态（含物流信息）

导入命令：
mysql -u root -p fruit_sale < init_data.sql

*/


