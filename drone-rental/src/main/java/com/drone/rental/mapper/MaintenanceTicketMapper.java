package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.MaintenanceTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维修工单Mapper接口
 */
@Mapper
public interface MaintenanceTicketMapper extends BaseMapper<MaintenanceTicket> {

}
