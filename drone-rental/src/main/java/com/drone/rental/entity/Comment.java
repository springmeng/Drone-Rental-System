package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
@ApiModel(value = "Comment", description = "评论实体")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("评论ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评分: 1-5星")
    private Integer rating;

    @ApiModelProperty("评论图片(JSON数组)")
    private String images;

    @ApiModelProperty("状态: 0-已屏蔽, 1-正常")
    private Integer status;

    @ApiModelProperty("管理员回复内容")
    private String replyContent;

    @ApiModelProperty("回复时间")
    private LocalDateTime replyTime;

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
