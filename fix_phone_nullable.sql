-- 修改 phone 字段允许为 NULL
-- 这样新用户注册时可以不填手机号，后续再绑定

ALTER TABLE user_info MODIFY COLUMN phone VARCHAR(20) NULL DEFAULT NULL COMMENT '手机号';

-- 查看修改后的表结构
DESC user_info;
