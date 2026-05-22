package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.entity.Payment;
import com.drone.rental.entity.RentalOrder;

/**
 * 支付服务接口
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 创建支付记录
     */
    void createPayment(RentalOrder order);

    /**
     * 更新支付状态
     */
    void updatePaymentStatus(Long orderId, Integer paymentStatus);

    /**
     * 退款
     */
    void refundPayment(Long orderId, String reason);

    /**
     * 根据订单ID获取支付记录
     */
    Payment getByOrderId(Long orderId);

    /**
     * 分页查询支付记录
     */
    IPage<Payment> pagePayments(Integer pageNum, Integer pageSize, Integer paymentStatus, Long userId);
}
