package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 无人机库存日志实体类
 */
@Data
@TableName("drone_stock_log")
@ApiModel(value = "DroneStockLog", description = "无人机库存日志实体")
public class DroneStockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("变更类型: 1-入库, 2-出租, 3-归还, 4-维修占用, 5-维修归还")
    private Integer changeType;

    @ApiModelProperty("变更数量")
    private Integer changeAmount;

    @ApiModelProperty("变更前库存")
    private Integer beforeStock;

    @ApiModelProperty("变更后库存")
    private Integer afterStock;

    @ApiModelProperty("关联订单ID")
    private Long relatedOrderId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
