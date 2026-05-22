package com.drone.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.rental.entity.Drone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 无人机Mapper接口
 */
@Mapper
public interface DroneMapper extends BaseMapper<Drone> {

    /**
     * 减少库存
     * @param droneId 无人机ID
     * @param amount 减少数量
     * @return 影响行数
     */
    @Update("UPDATE drone SET stock = stock - #{amount} WHERE id = #{droneId} AND stock >= #{amount}")
    int decreaseStock(@Param("droneId") Long droneId, @Param("amount") int amount);

    /**
     * 增加库存
     * @param droneId 无人机ID
     * @param amount 增加数量
     * @return 影响行数
     */
    @Update("UPDATE drone SET stock = stock + #{amount} WHERE id = #{droneId}")
    int increaseStock(@Param("droneId") Long droneId, @Param("amount") int amount);
}
