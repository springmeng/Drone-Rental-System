package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息VO
 */
@Data
@ApiModel(description = "订单信息")
public class OrderVO {

    @ApiModelProperty("订单ID")
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("无人机型号")
    private String droneModel;

    @ApiModelProperty("无人机图片")
    private String droneImage;

    @ApiModelProperty("空域备案ID")
    private Long airspaceRecordId;

    @ApiModelProperty("飞行区域名称")
    private String regionName;

    @ApiModelProperty("租赁开始时间")
    private LocalDateTime rentalStartTime;

    @ApiModelProperty("租赁结束时间")
    private LocalDateTime rentalEndTime;

    @ApiModelProperty("租赁天数")
    private Integer rentalDays;

    @ApiModelProperty("单价(元/天)")
    private BigDecimal unitPrice;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("押金金额")
    private BigDecimal depositAmount;

    @ApiModelProperty("订单状态: 0-待支付, 1-已支付, 2-租赁中, 3-已归还, 4-已取消, 5-已退款")
    private Integer orderStatus;

    @ApiModelProperty("订单状态描述")
    private String orderStatusDesc;

    @ApiModelProperty("收货地址")
    private String deliveryAddress;

    @ApiModelProperty("订单备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    /**
     * 获取订单状态描述
     */
    public String getOrderStatusDesc() {
        if (orderStatus == null) return "";
        switch (orderStatus) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "租赁中";
            case 3: return "已归还";
            case 4: return "已取消";
            case 5: return "已退款";
            default: return "未知";
        }
    }
}
