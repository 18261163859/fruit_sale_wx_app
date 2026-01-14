-- 添加轮播图视频支持字段
-- 执行日期: 2025-10-03

-- 添加视频URL字段
ALTER TABLE banner_config
ADD COLUMN video_url VARCHAR(500) NULL COMMENT '视频URL（轮播类型为视频时使用）' AFTER banner_image;

-- 添加轮播类型字段
ALTER TABLE banner_config
ADD COLUMN banner_type TINYINT DEFAULT 1 COMMENT '轮播类型：1-图片 2-视频' AFTER video_url;

-- 更新已有数据，设置默认类型为图片
UPDATE banner_config SET banner_type = 1 WHERE banner_type IS NULL;
