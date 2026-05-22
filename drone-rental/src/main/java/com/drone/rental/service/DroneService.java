package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.DroneDTO;
import com.drone.rental.entity.Drone;

/**
 * 无人机服务接口
 */
public interface DroneService extends IService<Drone> {

    /**
     * 分页查询无人机列表（用户端，只查上架且可租赁的）
     */
    IPage<Drone> pageAvailableDrones(Integer pageNum, Integer pageSize, String keyword, String brand,
                                      String type, Integer status, java.math.BigDecimal minPrice,
                                      java.math.BigDecimal maxPrice, String sortBy, String sortOrder);

    /**
     * 分页查询无人机列表（管理端，查询所有）
     */
    IPage<Drone> pageAllDrones(Integer pageNum, Integer pageSize, String keyword, String type, Integer status, Integer onShelf);

    /**
     * 获取无人机详情
     */
    Drone getDroneDetail(Long id);

    /**
     * 添加无人机（管理员）
     */
    void addDrone(DroneDTO dto);

    /**
     * 更新无人机（管理员）
     */
    void updateDrone(Long id, DroneDTO dto);

    /**
     * 删除无人机（管理员）
     */
    void deleteDrone(Long id);

    /**
     * 上架/下架无人机（管理员）
     */
    void updateOnShelf(Long id, Integer onShelf);

    /**
     * 更新无人机状态（管理员）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 更新无人机库存（管理员）
     */
    void updateStock(Long id, Integer stock);

    /**
     * 检查无人机是否可租赁
     */
    void checkDroneRentable(Long droneId);

    /**
     * 减少库存（下单时调用）
     */
    void decreaseStock(Long droneId, int amount, Long orderId);

    /**
     * 增加库存（取消/退款时调用）
     */
    void increaseStock(Long droneId, int amount, Long orderId);

    /**
     * 获取所有品牌列表
     */
    java.util.List<String> getAllBrands();

    /**
     * 获取所有类型列表
     */
    java.util.List<String> getAllTypes();
}
