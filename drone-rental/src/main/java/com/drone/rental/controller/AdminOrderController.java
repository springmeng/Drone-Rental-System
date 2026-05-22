package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.service.OrderService;
import com.drone.rental.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器（管理员端）
 */
@Api(tags = "订单管理-管理员端")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("分页查询订单列表")
    @GetMapping("/list")
    public Result<IPage<OrderVO>> pageOrders(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单编号") @RequestParam(required = false) String orderNo,
            @ApiParam("用户手机") @RequestParam(required = false) String userPhone,
            @ApiParam("订单状态") @RequestParam(required = false) Integer orderStatus,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        IPage<OrderVO> result = orderService.pageOrders(page, pageSize, orderNo, userPhone, orderStatus, startDate, endDate);
        return Result.success(result);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO vo = orderService.getOrderDetail(orderId);
        return Result.success(vo);
    }

    @ApiOperation("确认归还")
    @PostMapping("/{orderId}/return")
    public Result<Void> confirmReturn(@PathVariable Long orderId) {
        orderService.confirmReturn(orderId);
        return Result.success();
    }

    @ApiOperation("退款")
    @PostMapping("/{orderId}/refund")
    public Result<Void> refundOrder(
            @PathVariable Long orderId,
            @ApiParam("退款原因") @RequestParam(required = false) String reason) {
        orderService.refundOrder(orderId, reason);
        return Result.success();
    }

    @ApiOperation("发货")
    @PostMapping("/{orderId}/ship")
    public Result<Void> shipOrder(
            @PathVariable Long orderId,
            @ApiParam("快递公司") @RequestParam String expressCompany,
            @ApiParam("快递单号") @RequestParam String expressNo) {
        orderService.shipOrder(orderId, expressCompany, expressNo);
        return Result.success();
    }
}
