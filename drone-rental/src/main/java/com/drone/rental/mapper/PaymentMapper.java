package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

}
