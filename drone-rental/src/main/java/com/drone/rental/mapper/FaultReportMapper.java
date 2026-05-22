package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.FaultReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 故障上报Mapper接口
 */
@Mapper
public interface FaultReportMapper extends BaseMapper<FaultReport> {

}
