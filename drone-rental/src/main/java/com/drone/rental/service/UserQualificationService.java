package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.dto.QualificationDTO;
import com.drone.rental.entity.UserQualification;
import com.drone.rental.vo.UserQualificationVO;

/**
 * 用户飞行资质服务接口
 */
public interface UserQualificationService extends IService<UserQualification> {

    /**
     * 提交资质
     */
    void submitQualification(QualificationDTO dto);

    /**
     * 获取当前用户的资质
     */
    UserQualification getCurrentUserQualification();

    /**
     * 分页查询资质列表（管理员）
     */
    IPage<UserQualification> pageQualifications(Integer pageNum, Integer pageSize, Integer auditStatus);

    /**
     * 分页查询资质列表VO（管理员，包含用户名）
     */
    IPage<UserQualificationVO> pageQualificationsVO(Integer pageNum, Integer pageSize, Integer auditStatus);

    /**
     * 审核资质（管理员）
     */
    void auditQualification(Long id, AuditDTO dto);

    /**
     * 检查用户资质是否有效
     */
    void checkQualificationValid(Long userId);

    /**
     * 获取审核统计数据
     */
    Object getAuditStats();
}
