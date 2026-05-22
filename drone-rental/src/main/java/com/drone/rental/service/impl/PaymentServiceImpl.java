package com.drone.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.entity.Payment;
import com.drone.rental.entity.RentalOrder;
import com.drone.rental.mapper.PaymentMapper;
import com.drone.rental.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Override
    public void createPayment(RentalOrder order) {
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentType(Constants.PAYMENT_TYPE_ORDER);
        payment.setPaymentMethod("SIMULATED");
        payment.setPaymentStatus(Constants.PAYMENT_STATUS_UNPAID);

        this.save(payment);
    }

    @Override
    public void updatePaymentStatus(Long orderId, Integer paymentStatus) {
        Payment payment = this.getByOrderId(orderId);
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }

        payment.setPaymentStatus(paymentStatus);
        if (paymentStatus == Constants.PAYMENT_STATUS_PAID) {
            payment.setPaymentTime(LocalDateTime.now());
        }

        this.updateById(payment);
    }

    @Override
    public void refundPayment(Long orderId, String reason) {
        Payment payment = this.getByOrderId(orderId);
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }

        payment.setPaymentStatus(Constants.PAYMENT_STATUS_REFUNDED);
        payment.setRefundTime(LocalDateTime.now());
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundReason(reason);

        this.updateById(payment);
    }

    @Override
    public Payment getByOrderId(Long orderId) {
        return this.getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, orderId)
                .last("LIMIT 1"));
    }

    @Override
    public IPage<Payment> pagePayments(Integer pageNum, Integer pageSize, Integer paymentStatus, Long userId) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();

        if (paymentStatus != null) {
            wrapper.eq(Payment::getPaymentStatus, paymentStatus);
        }
        if (userId != null) {
            wrapper.eq(Payment::getUserId, userId);
        }
        wrapper.orderByDesc(Payment::getCreatedTime);

        return this.page(page, wrapper);
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
