package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 用户信息更新DTO
 */
@Data
@ApiModel(description = "用户信息更新参数")
public class UserUpdateDTO {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "住址")
    private String address;
}
