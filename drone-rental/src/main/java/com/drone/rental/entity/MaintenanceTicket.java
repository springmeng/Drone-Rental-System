package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单实体类
 */
@Data
@TableName("maintenance_ticket")
@ApiModel(value = "MaintenanceTicket", description = "维修工单实体")
public class MaintenanceTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("工单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("工单编号")
    private String ticketNo;

    @ApiModelProperty("故障上报ID")
    private Long faultReportId;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("关联用户ID")
    private Long userId;

    @ApiModelProperty("维修类型")
    private String maintenanceType;

    @ApiModelProperty("维修描述")
    private String maintenanceDescription;

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

    @ApiModelProperty("进度备注(JSON数组)")
    private String progressNotes;

    @ApiModelProperty("维修负责人")
    private String assigneeName;

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
