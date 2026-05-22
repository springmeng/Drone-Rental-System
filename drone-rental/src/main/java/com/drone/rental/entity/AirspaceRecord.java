package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 空域备案实体类
 */
@Data
@TableName("airspace_record")
@ApiModel(value = "AirspaceRecord", description = "空域备案实体")
public class AirspaceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("备案ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("飞行区域名称")
    private String regionName;

    @ApiModelProperty("飞行区域详细地址")
    private String regionAddress;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @ApiModelProperty("飞行半径(米)")
    private Integer radius;

    @ApiModelProperty("最大飞行高度(米)")
    private Integer maxAltitude;

    @ApiModelProperty("计划开始时间")
    private LocalDateTime plannedStartTime;

    @ApiModelProperty("计划结束时间")
    private LocalDateTime plannedEndTime;

    @ApiModelProperty("飞行用途")
    private String purpose;

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
