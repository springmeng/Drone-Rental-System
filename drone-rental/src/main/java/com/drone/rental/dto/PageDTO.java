package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页查询基础DTO
 */
@Data
@ApiModel(description = "分页参数")
public class PageDTO {

    @ApiModelProperty(value = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String orderBy;

    @ApiModelProperty(value = "排序方式: asc/desc")
    private String orderDirection = "desc";
}
