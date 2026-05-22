package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户飞行资质实体类
 */
@Data
@TableName("user_qualification")
@ApiModel(value = "UserQualification", description = "用户飞行资质实体")
public class UserQualification implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("资质ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

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

    @ApiModelProperty("审核人ID")
    private Long auditorId;

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
