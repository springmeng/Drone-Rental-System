-- =====================================================
-- 翱翔无人机租赁系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0
-- 项目: 毕业设计
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS drone_rental DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE drone_rental;

-- =====================================================
-- 1. 用户表 (user)
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '住址',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `credit_status` TINYINT NOT NULL DEFAULT 1 COMMENT '诚信状态: 0-不良, 1-正常',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 用户飞行资质表 (user_qualification)
-- =====================================================
DROP TABLE IF EXISTS `user_qualification`;
CREATE TABLE `user_qualification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资质ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `certificate_no` VARCHAR(50) NOT NULL COMMENT '证件号码',
    `certificate_type` VARCHAR(50) DEFAULT NULL COMMENT '证件类型',
    `certificate_image` VARCHAR(255) DEFAULT NULL COMMENT '证件图片URL',
    `valid_start_date` DATE DEFAULT NULL COMMENT '有效期开始日期',
    `valid_end_date` DATE DEFAULT NULL COMMENT '有效期结束日期',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-审核通过, 2-审核拒绝',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_certificate_no` (`certificate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户飞行资质表';

-- =====================================================
-- 3. 无人机表 (drone)
-- =====================================================
DROP TABLE IF EXISTS `drone`;
CREATE TABLE `drone` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '无人机ID',
    `model` VARCHAR(100) NOT NULL COMMENT '型号名称',
    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    `type` VARCHAR(20) DEFAULT NULL COMMENT '类型: 航拍/测绘/农业/巡检',
    `description` TEXT DEFAULT NULL COMMENT '描述信息',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
    `price_per_day` DECIMAL(10,2) NOT NULL COMMENT '每日租赁价格(元)',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `flight_time` INT DEFAULT NULL COMMENT '续航时间(分钟)',
    `max_payload` DECIMAL(10,2) DEFAULT NULL COMMENT '最大载重(kg)',
    `max_speed` DECIMAL(10,2) DEFAULT NULL COMMENT '最大速度(km/h)',
    `max_range` DECIMAL(10,2) DEFAULT NULL COMMENT '最大航程(km)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-缺货, 1-在售, 2-维护中',
    `on_shelf` TINYINT NOT NULL DEFAULT 1 COMMENT '上架状态: 0-下架, 1-上架',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_model` (`model`),
    KEY `idx_status` (`status`),
    KEY `idx_on_shelf` (`on_shelf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机表';

-- =====================================================
-- 4. 无人机库存日志表 (drone_stock_log)
-- =====================================================
DROP TABLE IF EXISTS `drone_stock_log`;
CREATE TABLE `drone_stock_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `drone_id` BIGINT NOT NULL COMMENT '无人机ID',
    `change_type` TINYINT NOT NULL COMMENT '变更类型: 1-入库, 2-出租, 3-归还, 4-维修占用, 5-维修归还',
    `change_amount` INT NOT NULL COMMENT '变更数量(正数增加,负数减少)',
    `before_stock` INT NOT NULL COMMENT '变更前库存',
    `after_stock` INT NOT NULL COMMENT '变更后库存',
    `related_order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_drone_id` (`drone_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机库存日志表';

-- =====================================================
-- 5. 空域备案表 (airspace_record)
-- =====================================================
DROP TABLE IF EXISTS `airspace_record`;
CREATE TABLE `airspace_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '备案ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `region_name` VARCHAR(100) NOT NULL COMMENT '飞行区域名称',
    `region_address` VARCHAR(255) DEFAULT NULL COMMENT '飞行区域详细地址',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `radius` INT DEFAULT NULL COMMENT '飞行半径(米)',
    `max_altitude` INT DEFAULT NULL COMMENT '最大飞行高度(米)',
    `planned_start_time` DATETIME DEFAULT NULL COMMENT '计划开始时间',
    `planned_end_time` DATETIME DEFAULT NULL COMMENT '计划结束时间',
    `purpose` VARCHAR(255) DEFAULT NULL COMMENT '飞行用途',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-审核通过, 2-审核拒绝',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_region_name` (`region_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='空域备案表';

-- =====================================================
-- 6. 租赁订单表 (rental_order)
-- =====================================================
DROP TABLE IF EXISTS `rental_order`;
CREATE TABLE `rental_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `drone_id` BIGINT NOT NULL COMMENT '无人机ID',
    `airspace_record_id` BIGINT NULL COMMENT '空域备案ID',
    `rental_start_time` DATETIME NOT NULL COMMENT '租赁开始时间',
    `rental_end_time` DATETIME NOT NULL COMMENT '租赁结束时间',
    `rental_days` INT NOT NULL COMMENT '租赁天数',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价(元/天)',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `deposit_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '押金金额',
    `delivery_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    `order_status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待支付, 1-已支付, 2-租赁中, 3-已归还, 4-已取消, 5-已退款',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '订单备注',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_drone_id` (`drone_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租赁订单表';
ALTER TABLE rental_order MODIFY airspace_record_id BIGINT NULL COMMENT '空域备案ID';
-- =====================================================
-- 7. 支付记录表 (payment)
-- =====================================================
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_type` TINYINT NOT NULL DEFAULT 1 COMMENT '支付类型: 1-订单支付, 2-押金支付, 3-维修费支付',
    `payment_method` VARCHAR(20) DEFAULT 'SIMULATED' COMMENT '支付方式: SIMULATED-模拟支付, ALIPAY-支付宝, WECHAT-微信',
    `payment_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态: 0-未支付, 1-已支付, 2-已退款',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
    `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额',
    `refund_reason` VARCHAR(255) DEFAULT NULL COMMENT '退款原因',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- =====================================================
-- 8. 评论表 (comment)
-- =====================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `drone_id` BIGINT NOT NULL COMMENT '无人机ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `rating` TINYINT DEFAULT 5 COMMENT '评分: 1-5星',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '评论图片(JSON数组)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-已屏蔽, 1-正常',
    `reply_content` TEXT DEFAULT NULL COMMENT '管理员回复内容',
    `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_drone_id` (`drone_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 9. 故障上报表 (fault_report)
-- =====================================================
DROP TABLE IF EXISTS `fault_report`;
CREATE TABLE `fault_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '故障ID',
    `report_no` VARCHAR(32) NOT NULL COMMENT '故障单号',
    `user_id` BIGINT NOT NULL COMMENT '上报用户ID',
    `drone_id` BIGINT NOT NULL COMMENT '无人机ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `fault_type` VARCHAR(50) DEFAULT NULL COMMENT '故障类型',
    `fault_description` TEXT NOT NULL COMMENT '故障描述',
    `fault_images` VARCHAR(1000) DEFAULT NULL COMMENT '故障图片(JSON数组)',
    `fault_time` DATETIME DEFAULT NULL COMMENT '故障发生时间',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-确认故障, 2-非故障',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_report_no` (`report_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_drone_id` (`drone_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障上报表';

-- =====================================================
-- 10. 维修工单表 (maintenance_ticket)
-- =====================================================
DROP TABLE IF EXISTS `maintenance_ticket`;
CREATE TABLE `maintenance_ticket` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `ticket_no` VARCHAR(32) NOT NULL COMMENT '工单编号',
    `fault_report_id` BIGINT NOT NULL COMMENT '故障上报ID',
    `drone_id` BIGINT NOT NULL COMMENT '无人机ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `maintenance_type` VARCHAR(50) DEFAULT NULL COMMENT '维修类型',
    `maintenance_description` TEXT DEFAULT NULL COMMENT '维修描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待维修, 1-维修中, 2-已完成, 3-已取消',
    `estimated_cost` DECIMAL(10,2) DEFAULT NULL COMMENT '预估费用',
    `actual_cost` DECIMAL(10,2) DEFAULT NULL COMMENT '实际费用',
    `estimated_days` INT DEFAULT NULL COMMENT '预估维修天数',
    `actual_days` INT DEFAULT NULL COMMENT '实际维修天数',
    `start_time` DATETIME DEFAULT NULL COMMENT '维修开始时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '维修完成时间',
    `progress_notes` TEXT DEFAULT NULL COMMENT '进度备注(JSON数组)',
    `assignee_name` VARCHAR(50) DEFAULT NULL COMMENT '维修负责人',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_no` (`ticket_no`),
    KEY `idx_fault_report_id` (`fault_report_id`),
    KEY `idx_drone_id` (`drone_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修工单表';

-- =====================================================
-- 11. 诚信记录表 (credit_record)
-- =====================================================
DROP TABLE IF EXISTS `credit_record`;
CREATE TABLE `credit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `change_type` TINYINT NOT NULL COMMENT '变更类型: 1-标记不良, 2-恢复正常, 3-系统扣分, 4-系统加分',
    `before_status` TINYINT NOT NULL COMMENT '变更前状态: 0-不良, 1-正常',
    `after_status` TINYINT NOT NULL COMMENT '变更后状态: 0-不良, 1-正常',
    `reason` VARCHAR(255) DEFAULT NULL COMMENT '变更原因',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诚信记录表';

-- =====================================================
-- 初始数据
-- =====================================================

-- 插入默认管理员账号 (密码: admin123, 使用MD5加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `role`, `status`, `credit_status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000000', 1, 1, 1);

-- 插入测试用户 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `address`, `role`, `status`, `credit_status`) VALUES
('testuser', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', '13900000001', 'test@example.com', '北京市朝阳区', 0, 1, 1);

-- 插入示例无人机数据
INSERT INTO `drone` (`model`, `brand`, `type`, `description`, `image`, `price_per_day`, `stock`, `flight_time`, `max_payload`, `max_speed`, `max_range`, `status`, `on_shelf`) VALUES
('DJI Mavic 3', 'DJI大疆', '航拍', '专业航拍无人机，4/3 CMOS哈苏相机，46分钟续航', '/uploads/mavic3_drone.png', 299.00, 10, 46, 0.9, 75.0, 30.0, 1, 1),
('DJI Mini 3 Pro', 'DJI大疆', '航拍', '轻便型航拍无人机，249g起飞重量，适合新手', '/uploads/mini3pro_drone.png', 149.00, 15, 34, 0.25, 58.0, 18.0, 1, 1),
('DJI Air 2S', 'DJI大疆', '航拍', '一英寸传感器，5.4K视频，智能跟随', '/uploads/air2s_drone.png', 199.00, 8, 31, 0.6, 68.0, 18.5, 1, 1),
('DJI Inspire 2', 'DJI大疆', '航拍', '专业影视航拍平台，可换镜头，双操控', '/uploads/inspire2_drone.png', 599.00, 5, 27, 1.4, 94.0, 15.0, 1, 1),
('DJI Phantom 4 Pro V2.0', 'DJI大疆', '测绘', '经典航拍无人机，一英寸传感器', '/uploads/phantom4pro_drone.png', 249.00, 6, 30, 0.5, 72.0, 15.0, 1, 1),
('DJI Agras T40', 'DJI大疆', '农业', '农业植保无人机，40kg喷洒载荷', '/uploads/agrast40_drone.png', 899.00, 3, 25, 50.0, 45.0, 10.0, 1, 1);

-- 插入示例空域备案数据
INSERT INTO `airspace_record` (`user_id`, `region_name`, `region_address`, `longitude`, `latitude`, `radius`, `max_altitude`, `purpose`, `audit_status`) VALUES
(2, '北京奥林匹克公园', '北京市朝阳区奥林匹克公园', 116.391244, 39.992552, 500, 120, '航拍摄影', 1),
(2, '上海外滩', '上海市黄浦区外滩', 121.490317, 31.240018, 300, 100, '城市风光拍摄', 0);
