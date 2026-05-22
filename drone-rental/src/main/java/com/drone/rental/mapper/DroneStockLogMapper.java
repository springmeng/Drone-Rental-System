package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.DroneStockLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机库存日志Mapper接口
 */
@Mapper
public interface DroneStockLogMapper extends BaseMapper<DroneStockLog> {

}
