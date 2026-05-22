package com.drone.rental.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录返回VO
 */
@Data
@ApiModel(description = "登录返回信息")
public class LoginVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("角色: 0-普通用户, 1-管理员")
    private Integer role;

    @ApiModelProperty("Token")
    private String token;
}
