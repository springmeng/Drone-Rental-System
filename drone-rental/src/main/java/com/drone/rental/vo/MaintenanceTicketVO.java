package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单VO（带关联信息）
 */
@Data
@ApiModel(value = "MaintenanceTicketVO", description = "维修工单视图对象")
public class MaintenanceTicketVO {

    @ApiModelProperty("工单ID")
    private Long id;

    @ApiModelProperty("工单编号")
    private String ticketNo;

    @ApiModelProperty("故障上报ID")
    private Long faultReportId;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("无人机型号")
    private String droneModel;

    @ApiModelProperty("无人机品牌")
    private String droneBrand;

    @ApiModelProperty("关联用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("维修类型")
    private String maintenanceType;

    @ApiModelProperty("维修描述")
    private String maintenanceDescription;

    @ApiModelProperty("故障类型")
    private String faultType;

    @ApiModelProperty("状态: 0-待维修, 1-维修中, 2-已完成, 3-已取消")
    private Integer status;

    @ApiModelProperty("预估费用")
    private BigDecimal estimatedCost;

    @ApiModelProperty("实际费用")
    private BigDecimal actualCost;

    @ApiModelProperty("预估维修天数")
    private Integer estimatedDays;

    @ApiModelProperty("实际维修天数")
    private Integer actualDays;

    @ApiModelProperty("维修开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("维修完成时间")
    private LocalDateTime completeTime;

    @ApiModelProperty("进度备注")
    private String progressNotes;

    @ApiModelProperty("维修负责人")
    private String assigneeName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;
}
