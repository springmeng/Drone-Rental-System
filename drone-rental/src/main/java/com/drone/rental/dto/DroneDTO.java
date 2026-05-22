package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 无人机创建/更新DTO
 */
@Data
@ApiModel(description = "无人机参数")
public class DroneDTO {

    @NotBlank(message = "型号名称不能为空")
    @ApiModelProperty(value = "型号名称", required = true)
    private String model;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "类型: 航拍/测绘/农业/巡检")
    private String type;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "图片URL")
    private String image;

    @NotNull(message = "每日租赁价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @ApiModelProperty(value = "每日租赁价格(元)", required = true)
    private BigDecimal pricePerDay;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    @ApiModelProperty(value = "库存数量", required = true)
    private Integer stock;

    @ApiModelProperty(value = "续航时间(分钟)")
    private Integer flightTime;

    @ApiModelProperty(value = "最大载重(kg)")
    private BigDecimal maxPayload;

    @ApiModelProperty(value = "最大速度(km/h)")
    private BigDecimal maxSpeed;

    @ApiModelProperty(value = "最大航程(km)")
    private BigDecimal maxRange;

    @ApiModelProperty(value = "状态: 0-缺货, 1-在售, 2-维护中")
    private Integer status;

    @ApiModelProperty(value = "上架状态: 0-下架, 1-上架")
    private Integer onShelf;
}
