package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
@ApiModel(description = "用户信息")
public class UserVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("住址")
    private String address;

    @ApiModelProperty("角色: 0-普通用户, 1-管理员")
    private Integer role;

    @ApiModelProperty("状态: 0-禁用, 1-正常")
    private Integer status;

    @ApiModelProperty("诚信状态: 0-不良, 1-正常")
    private Integer creditStatus;

    @ApiModelProperty("实名认证状态: 0-未认证, 1-待审核, 2-已认证, 3-未通过")
    private Integer verificationStatus;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("订单数量")
    private Integer orderCount;

    @ApiModelProperty("消费金额")
    private java.math.BigDecimal totalSpent;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;
}
