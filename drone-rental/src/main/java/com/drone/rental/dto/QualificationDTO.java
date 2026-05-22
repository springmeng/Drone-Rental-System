package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 飞行资质提交DTO
 */
@Data
@ApiModel(description = "飞行资质提交参数")
public class QualificationDTO {

    @NotBlank(message = "证件号码不能为空")
    @ApiModelProperty(value = "证件号码", required = true)
    private String certificateNo;

    @ApiModelProperty(value = "证件类型")
    private String certificateType;

    @ApiModelProperty(value = "证件图片URL")
    private String certificateImage;

    @NotNull(message = "有效期开始日期不能为空")
    @ApiModelProperty(value = "有效期开始日期", required = true)
    private LocalDate validStartDate;

    @NotNull(message = "有效期结束日期不能为空")
    @ApiModelProperty(value = "有效期结束日期", required = true)
    private LocalDate validEndDate;
}
