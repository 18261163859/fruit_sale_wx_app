/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : fruit_sale

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 21/01/2026 16:56:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `role_type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '角色类型：1-超级管理员，2-普通管理员，3-发货员',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Table structure for agent_application
-- ----------------------------
DROP TABLE IF EXISTS `agent_application`;
CREATE TABLE `agent_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `inviter_id` bigint NOT NULL COMMENT '邀请人ID（一级代理）',
  `invitee_id` bigint NOT NULL COMMENT '被邀请人ID',
  `invitee_phone` varchar(20) NOT NULL COMMENT '被邀请人手机号',
  `commission_rate` decimal(5,2) DEFAULT '5.00' COMMENT '返现比例',
  `status` tinyint DEFAULT '0' COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_id` (`invitee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理申请表';

-- ----------------------------
-- Table structure for agent_apply
-- ----------------------------
DROP TABLE IF EXISTS `agent_apply`;
CREATE TABLE `agent_apply` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请编号',
  `user_id` bigint unsigned NOT NULL COMMENT '申请人用户ID',
  `apply_type` tinyint unsigned NOT NULL COMMENT '申请类型：1-一级代理，2-二级代理',
  `recommender_id` bigint unsigned DEFAULT NULL COMMENT '推荐人用户ID（一级代理推荐）',
  `recommender_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐人代理码',
  `apply_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请理由',
  `contact_info` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系方式',
  `apply_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '申请状态：0-待审批，1-已通过，2-已拒绝',
  `audit_admin_id` bigint unsigned DEFAULT NULL COMMENT '审批管理员ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审批时间',
  `audit_remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批备注',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_recommender_id` (`recommender_id`),
  KEY `idx_apply_status` (`apply_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理申请记录表';

-- ----------------------------
-- Table structure for banner_config
-- ----------------------------
DROP TABLE IF EXISTS `banner_config`;
CREATE TABLE `banner_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `banner_title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图标题',
  `banner_image` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图图片URL',
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频URL（轮播类型为视频时使用）',
  `banner_type` tinyint unsigned NOT NULL COMMENT '类型：1-图片，2-视频',
  `link_type` tinyint unsigned DEFAULT NULL COMMENT '链接类型：1-商品详情，2-分类页，3-外部链接',
  `link_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链接地址',
  `sort_order` int unsigned NOT NULL DEFAULT '0' COMMENT '排序序号',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图配置表';

-- ----------------------------
-- Table structure for commission_application
-- ----------------------------
DROP TABLE IF EXISTS `commission_application`;
CREATE TABLE `commission_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `agent_id` bigint NOT NULL COMMENT '代理ID（申请人）',
  `commission_amount` decimal(10,2) NOT NULL COMMENT '申请返现金额',
  `status` tinyint DEFAULT '0' COMMENT '审批状态：0-待审核，1-已通过，2-已拒绝，3-已返现',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `account_name` varchar(50) DEFAULT NULL COMMENT '账户名',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  `transfer_time` datetime DEFAULT NULL COMMENT '返现时间',
  `transfer_admin_id` bigint DEFAULT NULL COMMENT '返现操作人ID',
  PRIMARY KEY (`id`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='返现申请表';

-- ----------------------------
-- Table structure for commission_record
-- ----------------------------
DROP TABLE IF EXISTS `commission_record`;
CREATE TABLE `commission_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '返现ID',
  `record_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '返现记录编号',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `order_amount` decimal(10,2) unsigned NOT NULL COMMENT '订单金额',
  `agent_user_id` bigint unsigned NOT NULL COMMENT '代理用户ID',
  `agent_level` tinyint unsigned NOT NULL COMMENT '代理层级：1-一级，2-二级',
  `commission_rate` decimal(5,2) unsigned NOT NULL COMMENT '返现比例',
  `commission_amount` decimal(10,2) unsigned NOT NULL COMMENT '返现金额',
  `settle_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '结算状态：0-待结算，1-已结算',
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_agent_user_id` (`agent_user_id`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_commission_agent_settle` (`agent_user_id`,`settle_status`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='返现记录表';

-- ----------------------------
-- Table structure for finance_record
-- ----------------------------
DROP TABLE IF EXISTS `finance_record`;
CREATE TABLE `finance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_no` varchar(32) NOT NULL COMMENT '流水号',
  `record_type` varchar(20) NOT NULL COMMENT '记录类型：vip_income-会员收入，order_income-订单收入，commission_expense-佣金支出，integral_expense-积分支出，refund_expense-退款支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `income_type` tinyint NOT NULL COMMENT '收支类型：1-收入，2-支出',
  `related_order_no` varchar(32) DEFAULT NULL COMMENT '关联订单号',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `record_date` date NOT NULL COMMENT '记录日期',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_income_type` (`income_type`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_related_order_no` (`related_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='财务流水记录表';

-- ----------------------------
-- Table structure for integral_card
-- ----------------------------
DROP TABLE IF EXISTS `integral_card`;
CREATE TABLE `integral_card` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '卡ID',
  `card_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '卡号',
  `card_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '兑换码（16位）',
  `integral_amount` int unsigned NOT NULL COMMENT '积分额度',
  `card_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '卡状态：0-未使用，1-已使用，2-已过期',
  `use_user_id` bigint unsigned DEFAULT NULL COMMENT '使用用户ID',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `batch_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_card_no` (`card_no`),
  UNIQUE KEY `uk_card_code` (`card_code`),
  KEY `idx_card_status` (`card_status`),
  KEY `idx_use_user_id` (`use_user_id`),
  KEY `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分充值卡表';

-- ----------------------------
-- Table structure for integral_record
-- ----------------------------
DROP TABLE IF EXISTS `integral_record`;
CREATE TABLE `integral_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `record_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水编号',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `change_type` tinyint unsigned NOT NULL COMMENT '变动类型：1-充值，2-消费获得，3-分享奖励，4-抵扣消费，5-管理员调整',
  `change_amount` int NOT NULL COMMENT '变动积分（正数增加，负数减少）',
  `balance_before` int unsigned NOT NULL COMMENT '变动前余额',
  `balance_after` int unsigned NOT NULL COMMENT '变动后余额',
  `related_order_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联订单编号',
  `related_card_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联充值卡编号',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_integral_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分流水表';

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `user_type` int DEFAULT '1' COMMENT '用户类型：1-普通会员 2-星享会员 3-一级代理 4-二级代理',
  `total_amount` decimal(10,2) unsigned NOT NULL COMMENT '商品总金额',
  `discount_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'VIP折扣金额',
  `integral_deduct_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `integral_used` int unsigned NOT NULL DEFAULT '0' COMMENT '使用积分数量',
  `freight_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '运费',
  `actual_amount` decimal(10,2) unsigned NOT NULL COMMENT '实付金额',
  `order_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已取消',
  `pay_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付',
  `pay_type` tinyint unsigned DEFAULT NULL COMMENT '支付方式：1-微信支付，2-积分+微信支付',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付流水号',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `receiver_province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货省份',
  `receiver_city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货城市',
  `receiver_district` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货区县',
  `receiver_address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货详细地址',
  `delivery_type` tinyint unsigned DEFAULT NULL COMMENT '配送方式：1-标准配送，2-顺丰次日达',
  `buyer_remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '买家备注',
  `is_commission_settled` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否已结算返现：0-否，1-是',
  `commission_settle_time` datetime DEFAULT NULL COMMENT '返现结算时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_order_user_status` (`user_id`,`order_status`,`create_time`),
  KEY `idx_order_time_status` (`create_time`,`order_status`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `spec_id` bigint DEFAULT NULL COMMENT '规格ID',
  `spec_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格名称',
  `product_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品编号',
  `product_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品图片',
  `product_price` decimal(10,2) unsigned NOT NULL COMMENT '商品单价',
  `actual_price` decimal(10,2) unsigned NOT NULL COMMENT '实际成交价（VIP折扣后）',
  `quantity` int unsigned NOT NULL COMMENT '购买数量',
  `subtotal_amount` decimal(10,2) DEFAULT '0.00' COMMENT '小计金额',
  `spec_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格信息',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品明细表';

-- ----------------------------
-- Table structure for order_logistics
-- ----------------------------
DROP TABLE IF EXISTS `order_logistics`;
CREATE TABLE `order_logistics` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `express_company` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快递公司',
  `express_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快递单号',
  `shipper_id` bigint unsigned DEFAULT NULL COMMENT '发货人员ID',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `ship_remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发货备注',
  `package_before_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '包装前照片URL',
  `package_after_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '包装后照片URL',
  `confirm_admin_id` bigint unsigned DEFAULT NULL COMMENT '确认完成的管理员ID',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认完成时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_shipper_id` (`shipper_id`),
  KEY `idx_express_no` (`express_no`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单物流表';

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `category_icon` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标URL',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID，0表示一级分类',
  `sort_order` int unsigned NOT NULL DEFAULT '0' COMMENT '排序序号',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品编号',
  `product_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `category_id` bigint unsigned NOT NULL COMMENT '分类ID',
  `main_image` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主图URL',
  `image_list` text COLLATE utf8mb4_unicode_ci COMMENT '商品图片列表（JSON格式）',
  `price` decimal(10,2) unsigned NOT NULL COMMENT '商品价格',
  `vip_price` decimal(10,2) unsigned NOT NULL COMMENT '星享会员价格',
  `stock` int unsigned NOT NULL DEFAULT '0' COMMENT '库存数量',
  `sales_count` int unsigned NOT NULL DEFAULT '0' COMMENT '销量',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '商品描述',
  `product_detail` text COLLATE utf8mb4_unicode_ci COMMENT '商品详情（富文本）',
  `spec_info` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格信息（JSON格式）',
  `max_buy_count` int unsigned DEFAULT NULL COMMENT '最大购买数量',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `sort_order` int unsigned NOT NULL DEFAULT '0' COMMENT '排序序号',
  `is_recommend` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否推荐：0-否，1-是',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_no` (`product_no`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_recommend` (`is_recommend`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `star_rating` tinyint unsigned NOT NULL COMMENT '星级评分：1-5星',
  `review_content` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评价内容',
  `review_images` text COLLATE utf8mb4_unicode_ci COMMENT '评价图片（JSON数组）',
  `admin_reply` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商家回复',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `is_visible` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否显示：0-隐藏，1-显示',
  `is_top` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_star_rating` (`star_rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- ----------------------------
-- Table structure for product_spec
-- ----------------------------
DROP TABLE IF EXISTS `product_spec`;
CREATE TABLE `product_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `spec_name` varchar(100) NOT NULL COMMENT '规格名称（如1斤装、5斤装）',
  `price` decimal(10,2) NOT NULL COMMENT '规格价格',
  `vip_price` decimal(10,2) DEFAULT NULL COMMENT 'VIP价格',
  `stock` int DEFAULT '0' COMMENT '库存',
  `sort_order` int DEFAULT '0' COMMENT '排序（数字越小越靠前）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品规格表';

-- ----------------------------
-- Table structure for share_record
-- ----------------------------
DROP TABLE IF EXISTS `share_record`;
CREATE TABLE `share_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分享ID',
  `record_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分享记录编号',
  `sharer_user_id` bigint unsigned NOT NULL COMMENT '分享人用户ID',
  `product_id` bigint unsigned NOT NULL COMMENT '分享商品ID',
  `product_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `share_channel` tinyint unsigned DEFAULT NULL COMMENT '分享渠道：1-微信好友，2-微信群，3-复制链接',
  `share_link` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分享链接',
  `is_converted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否转化：0-否，1-是',
  `converted_user_id` bigint unsigned DEFAULT NULL COMMENT '转化用户ID',
  `converted_order_id` bigint unsigned DEFAULT NULL COMMENT '转化订单ID',
  `converted_order_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '转化订单编号',
  `reward_integral` int unsigned DEFAULT NULL COMMENT '奖励积分',
  `is_rewarded` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否已奖励：0-否，1-是',
  `reward_time` datetime DEFAULT NULL COMMENT '奖励时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_sharer_user_id` (`sharer_user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_is_converted` (`is_converted`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_share_user_convert` (`sharer_user_id`,`is_converted`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享记录表';

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `spec_id` bigint DEFAULT NULL COMMENT '规格ID',
  `quantity` int unsigned NOT NULL DEFAULT '1' COMMENT '商品数量',
  `spec_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格信息',
  `is_selected` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否选中：0-否，1-是',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_spec_id` (`spec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_desc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'string' COMMENT '配置类型：string、int、decimal、json',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `address_tag` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址标签：家、公司、学校等',
  `is_default` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否默认地址：0-否，1-是',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户编号',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信unionid',
  `user_type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '用户类型：1-普通会员，2-星享会员，3-一级代理，4-二级代理',
  `vip_expire_time` datetime DEFAULT NULL COMMENT '星享会员过期时间（NULL表示永久）',
  `integral_balance` int unsigned NOT NULL DEFAULT '0' COMMENT '积分余额',
  `total_consume_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '累计消费金额',
  `total_commission_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '累计返现金额',
  `invite_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邀请码（代理专用）',
  `inviter_user_id` bigint unsigned DEFAULT NULL COMMENT '邀请人用户ID',
  `agent_level` tinyint unsigned DEFAULT NULL COMMENT '代理层级：1-一级代理，2-二级代理',
  `parent_agent_id` bigint unsigned DEFAULT NULL COMMENT '上级代理用户ID',
  `commission_rate` decimal(5,2) unsigned DEFAULT NULL COMMENT '返现比例（百分比）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `is_shipper` tinyint(1) DEFAULT '0' COMMENT '是否为发货人员：0-否，1-是',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_inviter_user_id` (`inviter_user_id`),
  KEY `idx_parent_agent_id` (`parent_agent_id`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- ----------------------------
-- Table structure for vip_order
-- ----------------------------
DROP TABLE IF EXISTS `vip_order`;
CREATE TABLE `vip_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vip_price` decimal(10,2) NOT NULL DEFAULT '199.00' COMMENT '会员价格',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态：0-待支付，1-已支付，2-已取消',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式：wechat-微信，alipay-支付宝',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '交易流水号',
  `vip_duration` int NOT NULL DEFAULT '365' COMMENT '会员时长（天）',
  `vip_start_time` datetime DEFAULT NULL COMMENT '会员生效时间',
  `vip_end_time` datetime DEFAULT NULL COMMENT '会员结束时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='星享会员开通订单表';

SET FOREIGN_KEY_CHECKS = 1;
