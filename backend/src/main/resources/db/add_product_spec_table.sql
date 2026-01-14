-- 创建商品规格表
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
ALTER TABLE shopping_cart ADD COLUMN spec_id BIGINT NULL COMMENT '规格ID' AFTER product_id;
ALTER TABLE shopping_cart ADD INDEX idx_spec_id (spec_id);

-- 修改订单明细表,添加规格信息字段
ALTER TABLE order_item ADD COLUMN spec_id BIGINT NULL COMMENT '规格ID' AFTER product_id;
ALTER TABLE order_item ADD COLUMN spec_name VARCHAR(100) NULL COMMENT '规格名称' AFTER spec_id;
