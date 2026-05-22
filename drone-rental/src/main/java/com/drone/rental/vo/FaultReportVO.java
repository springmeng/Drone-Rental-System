package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 故障报修VO（带关联信息）
 */
@Data
@ApiModel(value = "FaultReportVO", description = "故障报修视图对象")
public class FaultReportVO {

    @ApiModelProperty("故障ID")
    private Long id;

    @ApiModelProperty("故障单号")
    private String reportNo;

    @ApiModelProperty("上报用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("无人机型号")
    private String droneModel;

    @ApiModelProperty("无人机品牌")
    private String droneBrand;

    @ApiModelProperty("关联订单ID")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("故障类型")
    private String faultType;

    @ApiModelProperty("故障描述")
    private String faultDescription;

    @ApiModelProperty("故障图片")
    private String faultImages;

    @ApiModelProperty("故障发生时间")
    private LocalDateTime faultTime;

    @ApiModelProperty("审核状态: 0-待审核, 1-确认故障, 2-非故障")
    private Integer auditStatus;

    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("审核时间")
    private LocalDateTime auditTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("联系电话")
    private String contactPhone;
}
