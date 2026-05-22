package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.OrderCreateDTO;
import com.drone.rental.entity.RentalOrder;
import com.drone.rental.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<RentalOrder> {

    /**
     * 创建订单
     */
    OrderVO createOrder(OrderCreateDTO dto);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 获取当前用户订单列表
     */
    IPage<OrderVO> getCurrentUserOrders(Integer pageNum, Integer pageSize, Integer orderStatus);

    /**
     * 分页查询订单（管理员）
     */
    IPage<OrderVO> pageOrders(Integer pageNum, Integer pageSize, String orderNo, String userPhone, Integer orderStatus, String startDate, String endDate);

    /**
     * 模拟支付
     */
    void simulatePay(Long orderId, String deliveryAddress);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, String reason);

    /**
     * 确认归还（管理员）
     */
    void confirmReturn(Long orderId);

    /**
     * 退款（管理员）
     */
    void refundOrder(Long orderId, String reason);

    /**
     * 发货（管理员）
     */
    void shipOrder(Long orderId, String expressCompany, String expressNo);

    /**
     * 确认收货（用户）
     */
    void confirmReceive(Long orderId);

    /**
     * 申请退租（用户）
     */
    void applyReturn(Long orderId);
}
