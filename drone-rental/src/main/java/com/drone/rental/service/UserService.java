package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.LoginDTO;
import com.drone.rental.dto.RegisterDTO;
import com.drone.rental.dto.UserUpdateDTO;
import com.drone.rental.entity.User;
import com.drone.rental.vo.LoginVO;
import com.drone.rental.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 管理员登录
     */
    LoginVO adminLogin(LoginDTO dto);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 更新用户信息
     */
    void updateUserInfo(UserUpdateDTO dto);

    /**
     * 修改密码
     */
    void updatePassword(String oldPassword, String newPassword);

    /**
     * 分页查询用户（管理员）
     * @param keyword 关键词（昵称/手机号）
     */
    IPage<UserVO> pageUsers(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 获取用户详情（管理员）
     */
    UserVO getUserDetail(Long userId);

    /**
     * 启用/禁用用户（管理员）
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码（管理员）
     */
    void resetPassword(Long userId);

    /**
     * 更新用户诚信状态（管理员）
     */
    void updateCreditStatus(Long userId, Integer creditStatus);

    /**
     * 检查用户是否可操作（未被禁用、诚信正常）
     */
    void checkUserOperable(Long userId);
}
