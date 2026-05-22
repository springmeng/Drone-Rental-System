package com.drone.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.dto.OrderCreateDTO;
import com.drone.rental.entity.Drone;
import com.drone.rental.entity.Payment;
import com.drone.rental.entity.RentalOrder;
import com.drone.rental.entity.AirspaceRecord;
import com.drone.rental.entity.User;
import com.drone.rental.mapper.RentalOrderMapper;
import com.drone.rental.security.UserContext;
import com.drone.rental.service.*;
import com.drone.rental.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<RentalOrderMapper, RentalOrder> implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private DroneService droneService;

    @Autowired
    private AirspaceRecordService airspaceRecordService;

    @Autowired
    @Lazy
    private PaymentService paymentService;

    @Autowired
    private UserQualificationService qualificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO dto) {
        Long userId = UserContext.getCurrentUserId();

        // 1. 检查用户状态
        userService.checkUserOperable(userId);

        // 2. 检查用户资质
        qualificationService.checkQualificationValid(userId);

        // 3. 检查无人机是否可租赁
        droneService.checkDroneRentable(dto.getDroneId());

        // 4. 检查空域备案（如果提供了空域备案ID）
        if (dto.getAirspaceRecordId() != null) {
            airspaceRecordService.checkAirspaceRecordValid(dto.getAirspaceRecordId(), userId);
        }

        // 5. 计算租赁天数和金额
        Drone drone = droneService.getById(dto.getDroneId());
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (days <= 0) {
            throw new BusinessException("租赁结束日期必须大于等于开始日期");
        }

        BigDecimal totalAmount = drone.getPricePerDay().multiply(BigDecimal.valueOf(days));

        // 6. 创建订单
        RentalOrder order = new RentalOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setDroneId(dto.getDroneId());
        order.setAirspaceRecordId(dto.getAirspaceRecordId());
        order.setRentalStartTime(dto.getStartDate().atStartOfDay());
        order.setRentalEndTime(dto.getEndDate().atTime(23, 59, 59));
        order.setRentalDays((int) days);
        order.setUnitPrice(drone.getPricePerDay());
        order.setTotalAmount(totalAmount);
        order.setDepositAmount(BigDecimal.ZERO); // 暂不收押金
        order.setOrderStatus(Constants.ORDER_STATUS_UNPAID);
        order.setRemark(dto.getRemark());

        this.save(order);

        // 7. 减少库存
        droneService.decreaseStock(dto.getDroneId(), 1, order.getId());

        // 8. 创建支付记录
        paymentService.createPayment(order);

        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 普通用户只能查看自己的订单
        Long currentUserId = UserContext.getCurrentUserId();
        if (!UserContext.isAdmin() && !order.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return convertToVO(order);
    }

    @Override
    public IPage<OrderVO> getCurrentUserOrders(Integer pageNum, Integer pageSize, Integer orderStatus) {
        Long userId = UserContext.getCurrentUserId();
        Page<RentalOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RentalOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(RentalOrder::getUserId, userId);
        if (orderStatus != null) {
            wrapper.eq(RentalOrder::getOrderStatus, orderStatus);
        }
        wrapper.orderByDesc(RentalOrder::getCreatedTime);

        IPage<RentalOrder> orderPage = this.page(page, wrapper);
        return orderPage.convert(this::convertToVO);
    }

    @Override
    public IPage<OrderVO> pageOrders(Integer pageNum, Integer pageSize, String orderNo,
                                      String userPhone, Integer orderStatus, String startDate, String endDate) {
        Page<RentalOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RentalOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(RentalOrder::getOrderNo, orderNo);
        }
        if (orderStatus != null) {
            wrapper.eq(RentalOrder::getOrderStatus, orderStatus);
        }
        // 如果有用户手机号筛选，先查询用户ID
        if (StringUtils.hasText(userPhone)) {
            List<Long> userIds = userService.list(
                new LambdaQueryWrapper<User>().like(User::getPhone, userPhone)
            ).stream().map(User::getId).collect(java.util.stream.Collectors.toList());
            if (userIds.isEmpty()) {
                return new Page<>(pageNum, pageSize);
            }
            wrapper.in(RentalOrder::getUserId, userIds);
        }
        // 日期范围筛选
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(RentalOrder::getCreatedTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(RentalOrder::getCreatedTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(RentalOrder::getCreatedTime);

        IPage<RentalOrder> orderPage = this.page(page, wrapper);
        return orderPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void simulatePay(Long orderId, String deliveryAddress) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 检查是否是自己的订单
        Long currentUserId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (order.getOrderStatus() != Constants.ORDER_STATUS_UNPAID) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }

        // 保存收货地址
        if (deliveryAddress != null && !deliveryAddress.trim().isEmpty()) {
            order.setDeliveryAddress(deliveryAddress.trim());
        }

        // 更新订单状态
        order.setOrderStatus(Constants.ORDER_STATUS_PAID);
        this.updateById(order);

        // 检查支付记录是否存在，不存在则创建
        Payment existingPayment = paymentService.getByOrderId(orderId);
        if (existingPayment == null) {
            // 创建支付记录
            paymentService.createPayment(order);
        }

        // 更新支付状态
        paymentService.updatePaymentStatus(orderId, Constants.PAYMENT_STATUS_PAID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String reason) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 检查是否是自己的订单
        Long currentUserId = UserContext.getCurrentUserId();
        if (!UserContext.isAdmin() && !order.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 只能取消待支付的订单
        if (order.getOrderStatus() != Constants.ORDER_STATUS_UNPAID) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }

        order.setOrderStatus(Constants.ORDER_STATUS_CANCELLED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        this.updateById(order);

        // 恢复库存
        droneService.increaseStock(order.getDroneId(), 1, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturn(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 只有租赁中的订单可以确认归还
        if (order.getOrderStatus() != Constants.ORDER_STATUS_RENTING) {
            throw new BusinessException("订单状态不允许确认归还，只有租赁中的订单才能确认归还");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_RETURNED);
        this.updateById(order);

        // 恢复库存
        droneService.increaseStock(order.getDroneId(), 1, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Long orderId, String reason) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 只有已支付的订单可以退款
        if (order.getOrderStatus() != Constants.ORDER_STATUS_PAID) {
            throw new BusinessException("订单状态不允许退款");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_REFUNDED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        this.updateById(order);

        // 更新支付状态为已退款
        paymentService.refundPayment(orderId, reason);

        // 恢复库存
        droneService.increaseStock(order.getDroneId(), 1, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId, String expressCompany, String expressNo) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 只有已支付的订单可以发货
        if (order.getOrderStatus() != Constants.ORDER_STATUS_PAID) {
            throw new BusinessException("订单状态不允许发货，只有已支付的订单才能发货");
        }

        // 更新订单状态为待收货
        order.setOrderStatus(Constants.ORDER_STATUS_SHIPPED);
        order.setRemark("快递公司: " + expressCompany + ", 快递单号: " + expressNo);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 检查是否是自己的订单
        Long currentUserId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 只有待收货的订单可以确认收货
        if (order.getOrderStatus() != Constants.ORDER_STATUS_SHIPPED) {
            throw new BusinessException("订单状态不允许确认收货");
        }

        // 更新订单状态为租赁中
        order.setOrderStatus(Constants.ORDER_STATUS_RENTING);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyReturn(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 检查是否是自己的订单
        Long currentUserId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 只有租赁中的订单可以申请退租
        if (order.getOrderStatus() != Constants.ORDER_STATUS_RENTING) {
            throw new BusinessException("订单状态不允许申请退租");
        }

        // 这里可以发送通知给管理员，暂时只记录日志
        // 用户申请退租后，管理员在后台确认归还
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(RentalOrder order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 获取用户信息
        User user = userService.getById(order.getUserId());
        if (user != null) {
            vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        // 获取无人机信息
        Drone drone = droneService.getById(order.getDroneId());
        if (drone != null) {
            vo.setDroneModel(drone.getModel());
            vo.setDroneImage(drone.getImage());
        }

        // 获取空域备案信息
        AirspaceRecord record = airspaceRecordService.getById(order.getAirspaceRecordId());
        if (record != null) {
            vo.setRegionName(record.getRegionName());
        }

        return vo;
    }
}
