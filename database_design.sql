-- =============================================
-- 高端云南水果销售小程序 - 数据库设计
-- 符合阿里巴巴MySQL开发规范
-- =============================================

-- =============================================
-- 1. 用户相关表
-- =============================================

-- 用户基础信息表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_no` VARCHAR(32) NOT NULL COMMENT '用户编号',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `openid` VARCHAR(100) DEFAULT NULL COMMENT '微信openid',
    `unionid` VARCHAR(100) DEFAULT NULL COMMENT '微信unionid',
    `user_type` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '用户类型：1-普通会员，2-星享会员，3-一级代理，4-二级代理',
    `vip_expire_time` DATETIME DEFAULT NULL COMMENT '星享会员过期时间（NULL表示永久）',
    `integral_balance` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分余额',
    `total_consume_amount` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    `total_commission_amount` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '累计返现金额',
    `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '邀请码（代理专用）',
    `inviter_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '邀请人用户ID',
    `agent_level` TINYINT UNSIGNED DEFAULT NULL COMMENT '代理层级：1-一级代理，2-二级代理',
    `parent_agent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '上级代理用户ID',
    `commission_rate` DECIMAL(5,2) UNSIGNED DEFAULT NULL COMMENT '返现比例（百分比）',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_no` (`user_no`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_openid` (`openid`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_inviter_user_id` (`inviter_user_id`),
    KEY `idx_parent_agent_id` (`parent_agent_id`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- 用户收货地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `address_tag` VARCHAR(20) DEFAULT NULL COMMENT '地址标签：家、公司、学校等',
    `is_default` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

-- 代理申请记录表
DROP TABLE IF EXISTS `agent_apply`;
CREATE TABLE `agent_apply` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `apply_no` VARCHAR(32) NOT NULL COMMENT '申请编号',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '申请人用户ID',
    `apply_type` TINYINT UNSIGNED NOT NULL COMMENT '申请类型：1-一级代理，2-二级代理',
    `recommender_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '推荐人用户ID（一级代理推荐）',
    `recommender_code` VARCHAR(20) DEFAULT NULL COMMENT '推荐人代理码',
    `apply_reason` VARCHAR(500) DEFAULT NULL COMMENT '申请理由',
    `contact_info` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    `apply_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '申请状态：0-待审批，1-已通过，2-已拒绝',
    `audit_admin_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '审批管理员ID',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `audit_remark` VARCHAR(200) DEFAULT NULL COMMENT '审批备注',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_apply_no` (`apply_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_recommender_id` (`recommender_id`),
    KEY `idx_apply_status` (`apply_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理申请记录表';

-- =============================================
-- 2. 商品相关表
-- =============================================

-- 商品分类表
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `category_icon` VARCHAR(500) DEFAULT NULL COMMENT '分类图标URL',
    `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    `sort_order` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品信息表
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `product_no` VARCHAR(32) NOT NULL COMMENT '商品编号',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `main_image` VARCHAR(500) NOT NULL COMMENT '主图URL',
    `image_list` TEXT COMMENT '商品图片列表（JSON格式）',
    `price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '商品价格',
    `vip_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '星享会员价格',
    `stock` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
    `sales_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '销量',
    `description` TEXT COMMENT '商品描述',
    `product_detail` TEXT COMMENT '商品详情（富文本）',
    `spec_info` VARCHAR(500) DEFAULT NULL COMMENT '规格信息（JSON格式）',
    `max_buy_count` INT UNSIGNED DEFAULT NULL COMMENT '最大购买数量',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `sort_order` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序序号',
    `is_recommend` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_no` (`product_no`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_recommend` (`is_recommend`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';

-- =============================================
-- 3. 订单相关表
-- =============================================

-- 订单主表
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `user_type` TINYINT UNSIGNED NOT NULL COMMENT '下单时用户类型',
    `total_amount` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '商品总金额',
    `discount_amount` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT 'VIP折扣金额',
    `integral_deduct_amount` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `integral_used` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用积分数量',
    `freight_amount` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '运费',
    `actual_amount` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '实付金额',
    `order_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已取消',
    `pay_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付',
    `pay_type` TINYINT UNSIGNED DEFAULT NULL COMMENT '支付方式：1-微信支付，2-积分+微信支付',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '支付流水号',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_province` VARCHAR(50) NOT NULL COMMENT '收货省份',
    `receiver_city` VARCHAR(50) NOT NULL COMMENT '收货城市',
    `receiver_district` VARCHAR(50) NOT NULL COMMENT '收货区县',
    `receiver_address` VARCHAR(200) NOT NULL COMMENT '收货详细地址',
    `delivery_type` TINYINT UNSIGNED DEFAULT NULL COMMENT '配送方式：1-标准配送，2-顺丰次日达',
    `buyer_remark` VARCHAR(200) DEFAULT NULL COMMENT '买家备注',
    `is_commission_settled` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已结算返现：0-否，1-是',
    `commission_settle_time` DATETIME DEFAULT NULL COMMENT '返现结算时间',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_pay_status` (`pay_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 订单商品明细表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `product_no` VARCHAR(32) NOT NULL COMMENT '商品编号',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(500) NOT NULL COMMENT '商品图片',
    `product_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '商品单价',
    `actual_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '实际成交价（VIP折扣后）',
    `quantity` INT UNSIGNED NOT NULL COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '小计金额',
    `spec_info` VARCHAR(200) DEFAULT NULL COMMENT '规格信息',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品明细表';

-- 订单物流表
DROP TABLE IF EXISTS `order_logistics`;
CREATE TABLE `order_logistics` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物流ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `express_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    `express_no` VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
    `shipper_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '发货人员ID',
    `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `ship_remark` VARCHAR(200) DEFAULT NULL COMMENT '发货备注',
    `package_before_image` VARCHAR(500) DEFAULT NULL COMMENT '包装前照片URL',
    `package_after_image` VARCHAR(500) DEFAULT NULL COMMENT '包装后照片URL',
    `confirm_admin_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '确认完成的管理员ID',
    `confirm_time` DATETIME DEFAULT NULL COMMENT '确认完成时间',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_shipper_id` (`shipper_id`),
    KEY `idx_express_no` (`express_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单物流表';

-- =============================================
-- 4. 返现相关表
-- =============================================

-- 返现记录表
DROP TABLE IF EXISTS `commission_record`;
CREATE TABLE `commission_record` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '返现ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '返现记录编号',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `order_amount` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '订单金额',
    `agent_user_id` BIGINT UNSIGNED NOT NULL COMMENT '代理用户ID',
    `agent_level` TINYINT UNSIGNED NOT NULL COMMENT '代理层级：1-一级，2-二级',
    `commission_rate` DECIMAL(5,2) UNSIGNED NOT NULL COMMENT '返现比例',
    `commission_amount` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '返现金额',
    `settle_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '结算状态：0-待结算，1-已结算',
    `settle_time` DATETIME DEFAULT NULL COMMENT '结算时间',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_agent_user_id` (`agent_user_id`),
    KEY `idx_settle_status` (`settle_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='返现记录表';

-- =============================================
-- 5. 积分相关表
-- =============================================

-- 积分流水表
DROP TABLE IF EXISTS `integral_record`;
CREATE TABLE `integral_record` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '流水编号',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `change_type` TINYINT UNSIGNED NOT NULL COMMENT '变动类型：1-充值，2-消费获得，3-分享奖励，4-抵扣消费，5-管理员调整',
    `change_amount` INT NOT NULL COMMENT '变动积分（正数增加，负数减少）',
    `balance_before` INT UNSIGNED NOT NULL COMMENT '变动前余额',
    `balance_after` INT UNSIGNED NOT NULL COMMENT '变动后余额',
    `related_order_no` VARCHAR(32) DEFAULT NULL COMMENT '关联订单编号',
    `related_card_no` VARCHAR(32) DEFAULT NULL COMMENT '关联充值卡编号',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分流水表';

-- 积分充值卡表
DROP TABLE IF EXISTS `integral_card`;
CREATE TABLE `integral_card` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '卡ID',
    `card_no` VARCHAR(32) NOT NULL COMMENT '卡号',
    `card_code` VARCHAR(20) NOT NULL COMMENT '兑换码（16位）',
    `integral_amount` INT UNSIGNED NOT NULL COMMENT '积分额度',
    `card_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '卡状态：0-未使用，1-已使用，2-已过期',
    `use_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '使用用户ID',
    `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `batch_no` VARCHAR(32) DEFAULT NULL COMMENT '批次号',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_no` (`card_no`),
    UNIQUE KEY `uk_card_code` (`card_code`),
    KEY `idx_card_status` (`card_status`),
    KEY `idx_use_user_id` (`use_user_id`),
    KEY `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分充值卡表';

-- =============================================
-- 6. 分享相关表
-- =============================================

-- 分享记录表
DROP TABLE IF EXISTS `share_record`;
CREATE TABLE `share_record` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分享ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '分享记录编号',
    `sharer_user_id` BIGINT UNSIGNED NOT NULL COMMENT '分享人用户ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '分享商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `share_channel` TINYINT UNSIGNED DEFAULT NULL COMMENT '分享渠道：1-微信好友，2-微信群，3-复制链接',
    `share_link` VARCHAR(500) NOT NULL COMMENT '分享链接',
    `is_converted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否转化：0-否，1-是',
    `converted_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '转化用户ID',
    `converted_order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '转化订单ID',
    `converted_order_no` VARCHAR(32) DEFAULT NULL COMMENT '转化订单编号',
    `reward_integral` INT UNSIGNED DEFAULT NULL COMMENT '奖励积分',
    `is_rewarded` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已奖励：0-否，1-是',
    `reward_time` DATETIME DEFAULT NULL COMMENT '奖励时间',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_sharer_user_id` (`sharer_user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_is_converted` (`is_converted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享记录表';

-- =============================================
-- 7. 购物车表
-- =============================================

-- 购物车表
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品数量',
    `spec_info` VARCHAR(200) DEFAULT NULL COMMENT '规格信息',
    `is_selected` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否选中：0-否，1-是',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- =============================================
-- 8. 系统配置表
-- =============================================

-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `config_desc` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '配置类型：string、int、decimal、json',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 轮播图配置表
DROP TABLE IF EXISTS `banner_config`;
CREATE TABLE `banner_config` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    `banner_title` VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    `banner_image` VARCHAR(500) NOT NULL COMMENT '轮播图图片URL（图片轮播时使用主图，视频轮播时使用封面图）',
    `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频URL（轮播类型为视频时使用）',
    `banner_type` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '轮播类型：1-图片，2-视频',
    `link_type` TINYINT UNSIGNED DEFAULT NULL COMMENT '链接类型：1-商品详情，2-分类页，3-外部链接，4-不跳转',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '链接地址',
    `sort_order` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_status` (`status`),
    KEY `idx_banner_type` (`banner_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图配置表';

-- =============================================
-- 9. 管理员表
-- =============================================

-- 管理员表
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role_type` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '角色类型：1-超级管理员，2-普通管理员，3-发货员',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- =============================================
-- 10. 评价表
-- =============================================

-- 商品评价表
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `star_rating` TINYINT UNSIGNED NOT NULL COMMENT '星级评分：1-5星',
    `review_content` VARCHAR(500) NOT NULL COMMENT '评价内容',
    `review_images` TEXT DEFAULT NULL COMMENT '评价图片（JSON数组）',
    `admin_reply` VARCHAR(500) DEFAULT NULL COMMENT '商家回复',
    `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
    `is_visible` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `is_top` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_star_rating` (`star_rating`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- =============================================
-- 初始化系统配置数据
-- =============================================

INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `config_type`) VALUES
('vip_price', '199', '星享会员开通价格', 'decimal'),
('vip_discount', '0.95', '星享会员折扣（95折）', 'decimal'),
('share_reward_rate', '0.05', '分享奖励比例（5%）', 'decimal'),
('integral_exchange_rate', '100', '积分兑换比例（100积分=1元）', 'int'),
('min_commission_threshold', '100', '最低返现门槛金额', 'decimal'),
('newyear_theme_start', '12-30', '新年主题开始日期（农历腊月三十）', 'string'),
('newyear_theme_end', '01-15', '新年主题结束日期（农历正月十五）', 'string'),
('default_freight', '15', '默认运费', 'decimal');

-- =============================================
-- 创建索引优化查询
-- =============================================

-- 订单相关复合索引
CREATE INDEX idx_order_user_status ON order_info(user_id, order_status, create_time);
CREATE INDEX idx_order_time_status ON order_info(create_time, order_status);

-- 返现记录复合索引
CREATE INDEX idx_commission_agent_settle ON commission_record(agent_user_id, settle_status, create_time);

-- 积分流水复合索引  
CREATE INDEX idx_integral_user_time ON integral_record(user_id, create_time);

-- 分享记录复合索引
CREATE INDEX idx_share_user_convert ON share_record(sharer_user_id, is_converted, create_time);
