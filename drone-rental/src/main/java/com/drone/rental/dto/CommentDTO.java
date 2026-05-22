package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论DTO
 */
@Data
@ApiModel(description = "评论参数")
public class CommentDTO {

    @NotNull(message = "无人机ID不能为空")
    @ApiModelProperty(value = "无人机ID", required = true)
    private Long droneId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @NotBlank(message = "评论内容不能为空")
    @ApiModelProperty(value = "评论内容", required = true)
    private String content;

    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    @ApiModelProperty(value = "评分: 1-5星")
    private Integer rating;

    @ApiModelProperty(value = "评论图片(JSON数组)")
    private String images;
}
