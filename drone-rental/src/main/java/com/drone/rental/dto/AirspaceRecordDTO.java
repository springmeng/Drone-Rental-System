package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 空域备案DTO
 */
@Data
@ApiModel(description = "空域备案参数")
public class AirspaceRecordDTO {

    @NotBlank(message = "飞行区域名称不能为空")
    @ApiModelProperty(value = "飞行区域名称", required = true)
    private String regionName;

    @ApiModelProperty(value = "飞行区域详细地址")
    private String regionAddress;

    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "飞行半径(米)")
    private Integer radius;

    @ApiModelProperty(value = "最大飞行高度(米)")
    private Integer maxAltitude;

    @ApiModelProperty(value = "计划开始时间")
    private LocalDateTime plannedStartTime;

    @ApiModelProperty(value = "计划结束时间")
    private LocalDateTime plannedEndTime;

    @ApiModelProperty(value = "飞行用途")
    private String purpose;
}
