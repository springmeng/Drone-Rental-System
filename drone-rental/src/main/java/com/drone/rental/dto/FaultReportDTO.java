package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 故障上报DTO
 */
@Data
@ApiModel(description = "故障上报参数")
public class FaultReportDTO {

    @NotNull(message = "无人机ID不能为空")
    @ApiModelProperty(value = "无人机ID", required = true)
    private Long droneId;

    @ApiModelProperty(value = "关联订单ID")
    private Long orderId;

    @ApiModelProperty(value = "故障类型")
    private String faultType;

    @NotBlank(message = "故障描述不能为空")
    @ApiModelProperty(value = "故障描述", required = true)
    private String faultDescription;

    @ApiModelProperty(value = "故障图片(JSON数组)")
    private String faultImages;

    @ApiModelProperty(value = "故障发生时间")
    private LocalDateTime faultTime;
}
