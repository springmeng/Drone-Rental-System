-- 更新资质记录的证书图片
-- 为测试数据添加资质证书图片

-- 用户1的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_1.png' WHERE `user_id` = 1;

-- 用户3的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_2.png' WHERE `user_id` = 3;

-- 用户4的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_3.png' WHERE `user_id` = 4;

-- 用户5的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_1.png' WHERE `user_id` = 5;

-- 用户6的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_2.png' WHERE `user_id` = 6;

-- 用户7的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_3.png' WHERE `user_id` = 7;

-- 用户8的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_1.png' WHERE `user_id` = 8;

-- 用户9的资质证书
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_2.png' WHERE `user_id` = 9;

-- 用户10的资质证书 (如果存在)
UPDATE `user_qualification` SET `certificate_image` = '/uploads/cert_3.png' WHERE `user_id` = 10;
