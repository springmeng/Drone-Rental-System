package com.drone.rental.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.dto.LoginDTO;
import com.drone.rental.dto.RegisterDTO;
import com.drone.rental.dto.UserUpdateDTO;
import com.drone.rental.entity.User;
import com.drone.rental.entity.RentalOrder;
import com.drone.rental.entity.UserQualification;
import com.drone.rental.mapper.UserMapper;
import com.drone.rental.mapper.RentalOrderMapper;
import com.drone.rental.mapper.UserQualificationMapper;
import com.drone.rental.security.JwtUtil;
import com.drone.rental.security.UserContext;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.LoginVO;
import com.drone.rental.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RentalOrderMapper rentalOrderMapper;

    @Autowired
    private UserQualificationMapper userQualificationMapper;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtil.md5Hex(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setCreditStatus(Constants.CREDIT_STATUS_NORMAL);

        this.save(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        String encryptedPassword = DigestUtil.md5Hex(dto.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        vo.setToken(token);

        return vo;
    }

    @Override
    public LoginVO adminLogin(LoginDTO dto) {
        // 查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        String encryptedPassword = DigestUtil.md5Hex(dto.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查是否是管理员
        if (user.getRole() != Constants.ROLE_ADMIN) {
            throw new BusinessException("无管理员权限");
        }

        // 检查用户状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        vo.setToken(token);

        return vo;
    }

    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void updateUserInfo(UserUpdateDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }

        this.updateById(user);
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        String encryptedOldPassword = DigestUtil.md5Hex(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(DigestUtil.md5Hex(newPassword));
        this.updateById(user);
    }

    @Override
    public IPage<UserVO> pageUsers(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            // 按用户名、昵称或手机号模糊查询
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedTime);

        IPage<User> userPage = this.page(page, wrapper);

        return userPage.convert(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 填充额外信息
            fillUserExtraInfo(vo, user.getId());
            return vo;
        });
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // 填充额外信息
        fillUserExtraInfo(vo, userId);
        return vo;
    }

    /**
     * 填充用户额外信息（实名状态、订单数、消费金额）
     */
    private void fillUserExtraInfo(UserVO vo, Long userId) {
        // 查询实名认证信息
        UserQualification qualification = userQualificationMapper.selectOne(
                new LambdaQueryWrapper<UserQualification>()
                        .eq(UserQualification::getUserId, userId)
                        .orderByDesc(UserQualification::getCreatedTime)
                        .last("LIMIT 1"));

        if (qualification != null) {
            // 将审核状态映射为实名状态：0-待审核->1, 1-通过->2, 2-拒绝->3
            if (qualification.getAuditStatus() == 0) {
                vo.setVerificationStatus(1); // 待审核
            } else if (qualification.getAuditStatus() == 1) {
                vo.setVerificationStatus(2); // 已认证
            } else {
                vo.setVerificationStatus(3); // 未通过
            }
            // 使用证件号码作为身份信息
            vo.setIdCard(qualification.getCertificateNo());
        } else {
            vo.setVerificationStatus(0); // 未认证
        }

        // 查询订单数量
        Long orderCount = rentalOrderMapper.selectCount(
                new LambdaQueryWrapper<RentalOrder>()
                        .eq(RentalOrder::getUserId, userId));
        vo.setOrderCount(orderCount != null ? orderCount.intValue() : 0);

        // 查询消费金额（已支付订单的总金额）
        LambdaQueryWrapper<RentalOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(RentalOrder::getUserId, userId)
                .ge(RentalOrder::getOrderStatus, 1); // 状态>=1表示已支付

        java.util.List<RentalOrder> orders = rentalOrderMapper.selectList(orderWrapper);
        BigDecimal totalSpent = orders.stream()
                .map(RentalOrder::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSpent(totalSpent);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 不能禁用管理员
        if (user.getRole() == Constants.ROLE_ADMIN && status == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException("不能禁用管理员账号");
        }

        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        // 重置密码为 123456
        user.setPassword(DigestUtil.md5Hex("123456"));
        this.updateById(user);
    }

    @Override
    public void updateCreditStatus(Long userId, Integer creditStatus) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        user.setCreditStatus(creditStatus);
        this.updateById(user);
    }

    @Override
    public void checkUserOperable(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        if (user.getCreditStatus() == Constants.CREDIT_STATUS_BAD) {
            throw new BusinessException(ResultCode.USER_CREDIT_BAD);
        }
    }
}
