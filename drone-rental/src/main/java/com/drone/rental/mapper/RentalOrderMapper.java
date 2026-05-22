package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.RentalOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租赁订单Mapper接口
 */
@Mapper
public interface RentalOrderMapper extends BaseMapper<RentalOrder> {

}
