package com.drone.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.dto.QualificationDTO;
import com.drone.rental.entity.User;
import com.drone.rental.entity.UserQualification;
import com.drone.rental.mapper.UserQualificationMapper;
import com.drone.rental.security.UserContext;
import com.drone.rental.service.UserQualificationService;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.UserQualificationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户飞行资质服务实现类
 */
@Service
public class UserQualificationServiceImpl extends ServiceImpl<UserQualificationMapper, UserQualification>
        implements UserQualificationService {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public void submitQualification(QualificationDTO dto) {
        Long userId = UserContext.getCurrentUserId();

        // 检查是否已有资质记录
        UserQualification existing = this.getOne(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getUserId, userId)
                .orderByDesc(UserQualification::getCreatedTime)
                .last("LIMIT 1"));

        // 如果有待审核的资质，不允许重复提交
        if (existing != null && existing.getAuditStatus() == Constants.AUDIT_STATUS_PENDING) {
            throw new BusinessException("您已有待审核的资质申请，请等待审核结果");
        }

        UserQualification qualification = new UserQualification();
        qualification.setUserId(userId);
        qualification.setCertificateNo(dto.getCertificateNo());
        qualification.setCertificateType(dto.getCertificateType());
        qualification.setCertificateImage(dto.getCertificateImage());
        qualification.setValidStartDate(dto.getValidStartDate());
        qualification.setValidEndDate(dto.getValidEndDate());
        qualification.setAuditStatus(Constants.AUDIT_STATUS_PENDING);

        this.save(qualification);
    }

    @Override
    public UserQualification getCurrentUserQualification() {
        Long userId = UserContext.getCurrentUserId();
        return this.getOne(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getUserId, userId)
                .orderByDesc(UserQualification::getCreatedTime)
                .last("LIMIT 1"));
    }

    @Override
    public IPage<UserQualification> pageQualifications(Integer pageNum, Integer pageSize, Integer auditStatus) {
        Page<UserQualification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserQualification> wrapper = new LambdaQueryWrapper<>();

        if (auditStatus != null) {
            wrapper.eq(UserQualification::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(UserQualification::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<UserQualificationVO> pageQualificationsVO(Integer pageNum, Integer pageSize, Integer auditStatus) {
        IPage<UserQualification> qualificationPage = pageQualifications(pageNum, pageSize, auditStatus);

        Page<UserQualificationVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(qualificationPage.getTotal());
        voPage.setRecords(qualificationPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    /**
     * 转换为VO
     */
    private UserQualificationVO convertToVO(UserQualification qualification) {
        UserQualificationVO vo = new UserQualificationVO();
        BeanUtils.copyProperties(qualification, vo);

        // 获取用户信息
        User user = userService.getById(qualification.getUserId());
        if (user != null) {
            vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        return vo;
    }

    @Override
    public void auditQualification(Long id, AuditDTO dto) {
        UserQualification qualification = this.getById(id);
        if (qualification == null) {
            throw new BusinessException(ResultCode.QUALIFICATION_NOT_EXIST);
        }

        if (qualification.getAuditStatus() != Constants.AUDIT_STATUS_PENDING) {
            throw new BusinessException("该资质已审核，不能重复审核");
        }

        qualification.setAuditStatus(dto.getAuditStatus());
        qualification.setAuditRemark(dto.getAuditRemark());
        qualification.setAuditTime(LocalDateTime.now());
        qualification.setAuditorId(UserContext.getCurrentUserId());

        this.updateById(qualification);
    }

    @Override
    public void checkQualificationValid(Long userId) {
        UserQualification qualification = this.getOne(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getUserId, userId)
                .eq(UserQualification::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
                .orderByDesc(UserQualification::getCreatedTime)
                .last("LIMIT 1"));

        if (qualification == null) {
            throw new BusinessException(ResultCode.QUALIFICATION_NOT_APPROVED);
        }

        // 检查是否过期
        if (qualification.getValidEndDate() != null && qualification.getValidEndDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ResultCode.QUALIFICATION_EXPIRED);
        }
    }

    @Override
    public Object getAuditStats() {
        // 待审核数量
        long pending = this.count(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getAuditStatus, Constants.AUDIT_STATUS_PENDING));

        // 今日通过数量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayApproved = this.count(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
                .ge(UserQualification::getAuditTime, todayStart));

        // 今日拒绝数量
        long todayRejected = this.count(new LambdaQueryWrapper<UserQualification>()
                .eq(UserQualification::getAuditStatus, Constants.AUDIT_STATUS_REJECTED)
                .ge(UserQualification::getAuditTime, todayStart));

        // 累计审核数量
        long total = this.count(new LambdaQueryWrapper<UserQualification>()
                .ne(UserQualification::getAuditStatus, Constants.AUDIT_STATUS_PENDING));

        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", pending);
        stats.put("todayApproved", todayApproved);
        stats.put("todayRejected", todayRejected);
        stats.put("total", total);
        return stats;
    }
}
