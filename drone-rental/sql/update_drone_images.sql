-- 更新无人机图片路径
-- 执行此SQL为所有无人机添加图片

UPDATE `drone` SET `image` = '/uploads/mavic3_drone.png' WHERE `model` = 'DJI Mavic 3';
UPDATE `drone` SET `image` = '/uploads/mini3pro_drone.png' WHERE `model` = 'DJI Mini 3 Pro';
UPDATE `drone` SET `image` = '/uploads/air2s_drone.png' WHERE `model` = 'DJI Air 2S';
UPDATE `drone` SET `image` = '/uploads/inspire2_drone.png' WHERE `model` = 'DJI Inspire 2';
UPDATE `drone` SET `image` = '/uploads/phantom4pro_drone.png' WHERE `model` = 'DJI Phantom 4 Pro V2.0';
UPDATE `drone` SET `image` = '/uploads/agrast40_drone.png' WHERE `model` = 'DJI Agras T40';
