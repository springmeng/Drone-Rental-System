package com.drone.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.entity.CreditRecord;

import java.util.List;

/**
 * 诚信记录 Service
 */
public interface CreditRecordService extends IService<CreditRecord> {

    /**
     * 添加诚信记录
     * @param userId 用户ID
     * @param changeType 变更类型: 1-标记不良, 2-恢复正常
     * @param beforeStatus 变更前状态
     * @param afterStatus 变更后状态
     * @param reason 变更原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param orderId 关联订单ID（可选）
     */
    void addRecord(Long userId, Integer changeType, Integer beforeStatus, Integer afterStatus,
                   String reason, Long operatorId, String operatorName, Long orderId);

    /**
     * 获取用户的诚信记录
     * @param userId 用户ID
     * @return 诚信记录列表
     */
    List<CreditRecord> getUserRecords(Long userId);
}
