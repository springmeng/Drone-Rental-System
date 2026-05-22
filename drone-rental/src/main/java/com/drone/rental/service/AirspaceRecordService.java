package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.AirspaceRecordDTO;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.entity.AirspaceRecord;

import java.util.List;

/**
 * 空域备案服务接口
 */
public interface AirspaceRecordService extends IService<AirspaceRecord> {

    /**
     * 提交空域备案
     */
    void submitAirspaceRecord(AirspaceRecordDTO dto);

    /**
     * 获取当前用户的空域备案列表
     */
    List<AirspaceRecord> getCurrentUserAirspaceRecords();

    /**
     * 获取当前用户已通过审核的空域备案
     */
    List<AirspaceRecord> getCurrentUserApprovedRecords();

    /**
     * 分页查询空域备案（管理员）
     */
    IPage<AirspaceRecord> pageAirspaceRecords(Integer pageNum, Integer pageSize, Integer auditStatus, String regionName);

    /**
     * 审核空域备案（管理员）
     */
    void auditAirspaceRecord(Long id, AuditDTO dto);

    /**
     * 检查空域备案是否有效
     */
    void checkAirspaceRecordValid(Long airspaceRecordId, Long userId);
}
