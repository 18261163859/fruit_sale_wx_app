-- ========================================
-- 水果商城数据库设计
-- ========================================

-- 用户收货地址表
CREATE TABLE IF NOT EXISTS user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    address_tag VARCHAR(20) NULL COMMENT '地址标签（如：家、公司等）',
    is_default TINYINT DEFAULT 0 COMMENT '是否为默认地址：0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 商品规格表
CREATE TABLE IF NOT EXISTS product_spec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    spec_name VARCHAR(100) NOT NULL COMMENT '规格名称（如1斤装、5斤装）',
    price DECIMAL(10,2) NOT NULL COMMENT '规格价格',
    vip_price DECIMAL(10,2) NULL COMMENT 'VIP价格',
    stock INT DEFAULT 0 COMMENT '库存',
    sort_order INT DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 修改购物车表,添加规格ID字段
-- 注意：如果购物车表已存在且没有spec_id字段，请执行以下语句
-- ALTER TABLE shopping_cart ADD COLUMN spec_id BIGINT NULL COMMENT '规格ID' AFTER product_id;
-- ALTER TABLE shopping_cart ADD INDEX idx_spec_id (spec_id);

-- 修改订单明细表,添加规格信息字段
-- 注意：如果订单明细表已存在且没有spec_id字段，请执行以下语句
-- ALTER TABLE order_item ADD COLUMN spec_id BIGINT NULL COMMENT '规格ID' AFTER product_id;
-- ALTER TABLE order_item ADD COLUMN spec_name VARCHAR(100) NULL COMMENT '规格名称' AFTER spec_id;

-- 订单信息表
CREATE TABLE IF NOT EXISTS order_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type INT DEFAULT 1 COMMENT '用户类型：1-普通会员 2-星享会员 3-一级代理 4-二级代理',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣金额（星享会员95折）',
    integral_deduct_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额',
    integral_used INT DEFAULT 0 COMMENT '使用积分数量',
    freight_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    actual_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    order_status INT DEFAULT 0 COMMENT '订单状态：0-待付款 1-待发货 2-已发货 3-已完成 4-已取消',
    pay_status INT DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付',
    pay_type INT NULL COMMENT '支付方式：1-微信支付 2-支付宝',
    pay_time DATETIME NULL COMMENT '支付时间',
    transaction_id VARCHAR(100) NULL COMMENT '第三方支付交易号',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    receiver_province VARCHAR(50) NOT NULL COMMENT '收货省份',
    receiver_city VARCHAR(50) NOT NULL COMMENT '收货城市',
    receiver_district VARCHAR(50) NOT NULL COMMENT '收货区/县',
    receiver_address VARCHAR(200) NOT NULL COMMENT '收货详细地址',
    delivery_type INT DEFAULT 1 COMMENT '配送方式',
    buyer_remark VARCHAR(500) NULL COMMENT '买家备注',
    is_commission_settled INT DEFAULT 0 COMMENT '佣金是否已结算：0-未结算 1-已结算',
    commission_settle_time DATETIME NULL COMMENT '佣金结算时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_order_status (order_status),
    INDEX idx_pay_status (pay_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_no VARCHAR(32) NOT NULL COMMENT '商品编号',
    spec_id BIGINT NULL COMMENT '规格ID',
    spec_name VARCHAR(100) NULL COMMENT '规格名称',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) NULL COMMENT '商品图片',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品原价',
    actual_price DECIMAL(10,2) NOT NULL COMMENT '实际成交价（VIP折扣后）',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    subtotal_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 订单物流表
CREATE TABLE IF NOT EXISTS order_logistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    express_company VARCHAR(50) NULL COMMENT '快递公司',
    express_no VARCHAR(50) NULL COMMENT '快递单号',
    shipper_id BIGINT NULL COMMENT '发货人员ID',
    ship_time DATETIME NULL COMMENT '发货时间',
    ship_remark VARCHAR(200) NULL COMMENT '发货备注',
    package_before_image VARCHAR(500) NULL COMMENT '包装前照片URL',
    package_after_image VARCHAR(500) NULL COMMENT '包装后照片URL',
    confirm_admin_id BIGINT NULL COMMENT '确认完成的管理员ID',
    confirm_time DATETIME NULL COMMENT '确认完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY uk_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_shipper_id (shipper_id),
    INDEX idx_express_no (express_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- 返现记录表
CREATE TABLE IF NOT EXISTS commission_record (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '返现ID',
    record_no VARCHAR(32) NOT NULL UNIQUE COMMENT '返现记录编号',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    order_amount DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '订单金额',
    agent_user_id BIGINT UNSIGNED NOT NULL COMMENT '代理用户ID',
    agent_level TINYINT UNSIGNED NOT NULL COMMENT '代理层级：3-一级代理 4-二级代理',
    commission_rate DECIMAL(5,2) UNSIGNED NOT NULL COMMENT '返现比例（百分比，如10.00表示10%）',
    commission_amount DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '返现金额',
    settle_status TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '结算状态：0-待结算 1-已结算',
    settle_time DATETIME NULL COMMENT '结算时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_agent_user_id (agent_user_id),
    INDEX idx_settle_status (settle_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='返现记录表';
