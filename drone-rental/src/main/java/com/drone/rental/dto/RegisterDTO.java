package com.drone.rental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册请求DTO
 */
@Data
@ApiModel(description = "注册请求参数")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度为4-20个字符")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;
}
