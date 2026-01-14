-- ========================================
-- 修复订单表字段问题
-- 执行日期: 2025-10-04
-- ========================================

-- 1. 修改 order_info 表的 user_type 字段，添加默认值
ALTER TABLE order_info
MODIFY COLUMN user_type INT DEFAULT 1 COMMENT '用户类型：1-普通会员 2-星享会员 3-一级代理 4-二级代理';

-- 2. 更新已存在的订单数据的 user_type（如果为NULL）
UPDATE order_info
SET user_type = 1
WHERE user_type IS NULL;

-- 3. 检查 order_item 表是否有 total_amount 字段需要重命名为 subtotal_amount
-- 如果字段名为 total_amount，则重命名为 subtotal_amount
ALTER TABLE order_item
CHANGE COLUMN total_amount subtotal_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '小计金额';
