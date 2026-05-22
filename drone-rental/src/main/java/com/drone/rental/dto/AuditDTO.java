package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核DTO（通用）
 */
@Data
@ApiModel(description = "审核参数")
public class AuditDTO {

    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty(value = "审核状态: 1-通过, 2-拒绝", required = true)
    private Integer auditStatus;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
}
