package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 故障上报实体类
 */
@Data
@TableName("fault_report")
@ApiModel(value = "FaultReport", description = "故障上报实体")
public class FaultReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("故障ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("故障单号")
    private String reportNo;

    @ApiModelProperty("上报用户ID")
    private Long userId;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("关联订单ID")
    private Long orderId;

    @ApiModelProperty("故障类型")
    private String faultType;

    @ApiModelProperty("故障描述")
    private String faultDescription;

    @ApiModelProperty("故障图片(JSON数组)")
    private String faultImages;

    @ApiModelProperty("故障发生时间")
    private LocalDateTime faultTime;

    @ApiModelProperty("审核状态: 0-待审核, 1-确认故障, 2-非故障")
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
