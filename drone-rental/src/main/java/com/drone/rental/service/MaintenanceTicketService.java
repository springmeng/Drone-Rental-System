package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.MaintenanceTicketDTO;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.entity.MaintenanceTicket;
import com.drone.rental.vo.MaintenanceTicketVO;

/**
 * 维修工单服务接口
 */
public interface MaintenanceTicketService extends IService<MaintenanceTicket> {

    /**
     * 根据故障上报创建维修工单
     */
    void createFromFaultReport(FaultReport faultReport);

    /**
     * 分页查询维修工单（管理员）- 带关联信息
     */
    IPage<MaintenanceTicketVO> pageMaintenanceTicketsVO(Integer pageNum, Integer pageSize, Integer status, Long droneId, String droneModel, String type);

    /**
     * 分页查询维修工单（管理员）
     */
    IPage<MaintenanceTicket> pageMaintenanceTickets(Integer pageNum, Integer pageSize, Integer status, Long droneId, String droneModel, String type);

    /**
     * 更新维修工单
     */
    void updateMaintenanceTicket(Long id, MaintenanceTicketDTO dto);

    /**
     * 获取工单详情（带关联信息）
     */
    MaintenanceTicketVO getTicketDetailVO(Long id);

    /**
     * 获取工单详情
     */
    MaintenanceTicket getTicketDetail(Long id);

    /**
     * 根据故障上报ID获取工单
     */
    MaintenanceTicket getByFaultReportId(Long faultReportId);

    /**
     * 开始维修
     */
    void startMaintenance(Long id);

    /**
     * 完成维修
     */
    void completeMaintenance(Long id, MaintenanceTicketDTO dto);
}
