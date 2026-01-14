-- 为返现申请表添加返现相关字段
ALTER TABLE commission_application
ADD COLUMN transfer_time DATETIME COMMENT '返现时间',
ADD COLUMN transfer_admin_id BIGINT COMMENT '返现操作人ID';

-- 更新status字段注释，添加状态3说明
ALTER TABLE commission_application
MODIFY COLUMN status INT NOT NULL DEFAULT 0 COMMENT '审批状态：0-待审核，1-已通过，2-已拒绝，3-已返现';
