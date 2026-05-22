-- 租赁订单表新增收货地址字段
ALTER TABLE `rental_order` ADD COLUMN `delivery_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址' AFTER `deposit_amount`;
