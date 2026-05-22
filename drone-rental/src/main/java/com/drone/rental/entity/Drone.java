package com.drone.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 无人机实体类
 */
@Data
@TableName("drone")
@ApiModel(value = "Drone", description = "无人机实体")
public class Drone implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("无人机ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("型号名称")
    private String model;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("类型: 航拍/测绘/农业/巡检")
    private String type;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("图片URL")
    private String image;

    @ApiModelProperty("每日租赁价格(元)")
    private BigDecimal pricePerDay;

    @ApiModelProperty("库存数量")
    private Integer stock;

    @ApiModelProperty("续航时间(分钟)")
    private Integer flightTime;

    @ApiModelProperty("最大载重(kg)")
    private BigDecimal maxPayload;

    @ApiModelProperty("最大速度(km/h)")
    private BigDecimal maxSpeed;

    @ApiModelProperty("最大航程(km)")
    private BigDecimal maxRange;

    @ApiModelProperty("状态: 0-缺货, 1-在售, 2-维护中")
    private Integer status;

    @ApiModelProperty("上架状态: 0-下架, 1-上架")
    private Integer onShelf;

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
