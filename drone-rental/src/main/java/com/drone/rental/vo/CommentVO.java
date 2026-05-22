package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论VO（带关联信息）
 */
@Data
@ApiModel(value = "CommentVO", description = "评论视图对象")
public class CommentVO {

    @ApiModelProperty("评论ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String userNickname;

    @ApiModelProperty("用户头像")
    private String userAvatar;

    @ApiModelProperty("无人机ID")
    private Long droneId;

    @ApiModelProperty("无人机型号")
    private String droneModel;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评分")
    private Integer rating;

    @ApiModelProperty("图片")
    private String images;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("回复内容")
    private String reply;

    @ApiModelProperty("回复时间")
    private LocalDateTime replyTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
