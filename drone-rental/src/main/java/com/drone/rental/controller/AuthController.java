package com.drone.rental.controller;

import com.drone.rental.common.Result;
import com.drone.rental.dto.LoginDTO;
import com.drone.rental.dto.RegisterDTO;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @ApiOperation("管理员登录")
    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Validated @RequestBody LoginDTO dto) {
        LoginVO vo = userService.adminLogin(dto);
        return Result.success(vo);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，客户端清除Token即可
        return Result.success();
    }
}
