-- 修复管理员密码
-- 密码: admin123
-- 使用hutool BCrypt生成的hash

USE fruit_sale;

-- 更新admin密码为: admin123
-- Hash使用hutool BCrypt.hashpw("admin123", BCrypt.gensalt())生成
UPDATE admin_user
SET password = '$2a$10$Aq2WZF6Htp6/cZ0jb7F5yOX.Mx9K3qVYPqQXmE1WdYL8R3wK4gNd6'
WHERE username = 'admin';

-- 验证更新
SELECT username, LEFT(password, 30) as password_hash, real_name
FROM admin_user
WHERE username = 'admin';
