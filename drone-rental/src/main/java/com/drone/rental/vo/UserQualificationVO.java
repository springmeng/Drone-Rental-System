package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户飞行资质VO
 */
@Data
@ApiModel(description = "用户飞行资质信息")
public class UserQualificationVO {

    @ApiModelProperty("资质ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("证件号码")
    private String certificateNo;

    @ApiModelProperty("证件类型")
    private String certificateType;

    @ApiModelProperty("证件图片URL")
    private String certificateImage;

    @ApiModelProperty("有效期开始日期")
    private LocalDate validStartDate;

    @ApiModelProperty("有效期结束日期")
    private LocalDate validEndDate;

    @ApiModelProperty("审核状态: 0-待审核, 1-审核通过, 2-审核拒绝")
    private Integer auditStatus;

    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("审核时间")
    private LocalDateTime auditTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;
}
