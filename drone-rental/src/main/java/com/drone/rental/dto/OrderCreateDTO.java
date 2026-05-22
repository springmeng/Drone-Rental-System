package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 订单创建DTO
 */
@Data
@ApiModel(description = "订单创建参数")
public class OrderCreateDTO {

    @NotNull(message = "无人机ID不能为空")
    @ApiModelProperty(value = "无人机ID", required = true)
    private Long droneId;

    @ApiModelProperty(value = "空域备案ID")
    private Long airspaceRecordId;

    @NotNull(message = "租赁开始日期不能为空")
    @ApiModelProperty(value = "租赁开始日期", required = true)
    private LocalDate startDate;

    @NotNull(message = "租赁结束日期不能为空")
    @ApiModelProperty(value = "租赁结束日期", required = true)
    private LocalDate endDate;

    @ApiModelProperty(value = "收货地址")
    private String deliveryAddress;

    @ApiModelProperty(value = "订单备注")
    private String remark;
}
