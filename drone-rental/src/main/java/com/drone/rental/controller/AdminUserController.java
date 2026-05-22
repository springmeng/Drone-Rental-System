package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.entity.CreditRecord;
import com.drone.rental.entity.User;
import com.drone.rental.entity.UserQualification;
import com.drone.rental.security.UserContext;
import com.drone.rental.service.CreditRecordService;
import com.drone.rental.service.UserQualificationService;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.UserQualificationVO;
import com.drone.rental.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器（管理员端）
 */
@Api(tags = "用户管理-管理员端")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserQualificationService qualificationService;

    @Autowired
    private CreditRecordService creditRecordService;

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public Result<IPage<UserVO>> pageUsers(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关键词(昵称/手机号)") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        IPage<UserVO> result = userService.pageUsers(page, pageSize, keyword, status);
        return Result.success(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserDetail(@PathVariable Long userId) {
        UserVO user = userService.getUserDetail(userId);
        return Result.success(user);
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @ApiParam("状态: 0-禁用, 1-正常") @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success();
    }

    @ApiOperation("更新用户诚信状态")
    @PutMapping("/{userId}/credit")
    public Result<Void> updateCreditStatus(
            @PathVariable Long userId,
            @ApiParam("诚信状态: 0-不良, 1-正常") @RequestParam Integer creditStatus,
            @ApiParam("变更原因") @RequestParam(required = false) String reason) {
        // 获取当前用户状态
        UserVO user = userService.getUserDetail(userId);
        Integer beforeStatus = user.getCreditStatus();

        // 更新诚信状态
        userService.updateCreditStatus(userId, creditStatus);

        // 记录诚信变更
        Long operatorId = UserContext.getCurrentUserId();
        User operator = userService.getById(operatorId);
        String operatorName = operator != null ? operator.getNickname() : "系统管理员";
        Integer changeType = creditStatus == 0 ? 1 : 2; // 1-标记不良, 2-恢复正常

        creditRecordService.addRecord(userId, changeType, beforeStatus, creditStatus,
                reason != null ? reason : (creditStatus == 0 ? "管理员标记为诚信不良" : "管理员恢复诚信状态"),
                operatorId, operatorName, null);

        return Result.success();
    }

    @ApiOperation("获取用户诚信记录")
    @GetMapping("/{userId}/credit-records")
    public Result<List<CreditRecord>> getCreditRecords(@PathVariable Long userId) {
        List<CreditRecord> records = creditRecordService.getUserRecords(userId);
        return Result.success(records);
    }

    @ApiOperation("重置用户密码")
    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        userService.resetPassword(userId);
        return Result.success();
    }

    @ApiOperation("分页查询飞行资质列表")
    @GetMapping("/qualification/list")
    public Result<IPage<UserQualificationVO>> pageQualifications(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("审核状态") @RequestParam(required = false) Integer auditStatus) {
        IPage<UserQualificationVO> page = qualificationService.pageQualificationsVO(pageNum, pageSize, auditStatus);
        return Result.success(page);
    }

    @ApiOperation("获取飞行资质详情")
    @GetMapping("/qualification/{id}")
    public Result<UserQualification> getQualificationDetail(@PathVariable Long id) {
        UserQualification qualification = qualificationService.getById(id);
        return Result.success(qualification);
    }

    @ApiOperation("审核飞行资质")
    @PutMapping("/qualification/{id}/audit")
    public Result<Void> auditQualification(
            @PathVariable Long id,
            @Validated @RequestBody AuditDTO dto) {
        qualificationService.auditQualification(id, dto);
        return Result.success();
    }

    @ApiOperation("获取资质审核统计")
    @GetMapping("/qualification/stats")
    public Result<Object> getQualificationStats() {
        return Result.success(qualificationService.getAuditStats());
    }
}
