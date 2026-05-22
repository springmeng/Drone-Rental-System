package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 维修工单更新DTO
 */
@Data
@ApiModel(description = "维修工单参数")
public class MaintenanceTicketDTO {

    @ApiModelProperty(value = "维修类型")
    private String maintenanceType;

    @ApiModelProperty(value = "维修描述")
    private String maintenanceDescription;

    @ApiModelProperty(value = "状态: 0-待维修, 1-维修中, 2-已完成, 3-已取消")
    private Integer status;

    @ApiModelProperty(value = "预估费用")
    private BigDecimal estimatedCost;

    @ApiModelProperty(value = "实际费用")
    private BigDecimal actualCost;

    @ApiModelProperty(value = "预估维修天数")
    private Integer estimatedDays;

    @ApiModelProperty(value = "实际维修天数")
    private Integer actualDays;

    @ApiModelProperty(value = "维修负责人")
    private String assigneeName;

    @ApiModelProperty(value = "进度备注")
    private String progressNote;
}
