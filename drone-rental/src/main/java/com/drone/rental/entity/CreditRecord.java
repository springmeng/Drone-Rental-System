package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 诚信记录实体
 */
@Data
@TableName("credit_record")
@ApiModel(description = "诚信记录")
public class CreditRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("变更类型: 1-标记不良, 2-恢复正常, 3-系统扣分, 4-系统加分")
    private Integer changeType;

    @ApiModelProperty("变更前状态: 0-不良, 1-正常")
    private Integer beforeStatus;

    @ApiModelProperty("变更后状态: 0-不良, 1-正常")
    private Integer afterStatus;

    @ApiModelProperty("变更原因")
    private String reason;

    @ApiModelProperty("关联订单ID")
    private Long orderId;

    @ApiModelProperty("操作人ID(管理员)")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @ApiModelProperty("备注")
    private String remark;
}
