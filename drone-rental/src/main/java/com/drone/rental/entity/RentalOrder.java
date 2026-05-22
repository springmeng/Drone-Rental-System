package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租赁订单实体类
 */
@Data
@TableName("rental_order")
@ApiModel(value = "RentalOrder", description = "租赁订单实体")
public class RentalOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("空域备案ID")
    private Long airspaceRecordId;

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

    @ApiModelProperty("收货地址")
    private String deliveryAddress;

    @ApiModelProperty("订单备注")
    private String remark;

    @ApiModelProperty("取消原因")
    private String cancelReason;

    @ApiModelProperty("取消时间")
    private LocalDateTime cancelTime;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @ApiModelProperty("逻辑删除标志")
    @TableLogic
    private Integer deleted;
}
