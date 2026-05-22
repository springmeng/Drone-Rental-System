package com.drone.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.entity.CreditRecord;
import com.drone.rental.mapper.CreditRecordMapper;
import com.drone.rental.service.CreditRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 诚信记录 Service 实现类
 */
@Service
public class CreditRecordServiceImpl extends ServiceImpl<CreditRecordMapper, CreditRecord> implements CreditRecordService {

    @Override
    public void addRecord(Long userId, Integer changeType, Integer beforeStatus, Integer afterStatus,
                          String reason, Long operatorId, String operatorName, Long orderId) {
        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setChangeType(changeType);
        record.setBeforeStatus(beforeStatus);
        record.setAfterStatus(afterStatus);
        record.setReason(reason);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setOrderId(orderId);
        this.save(record);
    }

    @Override
    public List<CreditRecord> getUserRecords(Long userId) {
        LambdaQueryWrapper<CreditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditRecord::getUserId, userId)
               .orderByDesc(CreditRecord::getCreatedTime);
        return this.list(wrapper);
    }
}
