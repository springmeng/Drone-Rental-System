package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("payment")
@ApiModel(value = "Payment", description = "支付记录实体")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("支付ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("支付单号")
    private String paymentNo;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("支付金额")
    private BigDecimal amount;

    @ApiModelProperty("支付类型: 1-订单支付, 2-押金支付, 3-维修费支付")
    private Integer paymentType;

    @ApiModelProperty("支付方式")
    private String paymentMethod;

    @ApiModelProperty("支付状态: 0-未支付, 1-已支付, 2-已退款")
    private Integer paymentStatus;

    @ApiModelProperty("支付时间")
    private LocalDateTime paymentTime;

    @ApiModelProperty("退款时间")
    private LocalDateTime refundTime;

    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("退款原因")
    private String refundReason;

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
