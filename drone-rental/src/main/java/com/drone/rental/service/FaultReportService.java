package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.dto.FaultReportDTO;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.vo.FaultReportVO;

import java.util.List;

/**
 * 故障上报服务接口
 */
public interface FaultReportService extends IService<FaultReport> {

    /**
     * 上报故障
     */
    void reportFault(FaultReportDTO dto);

    /**
     * 获取当前用户的故障上报列表（带关联信息）
     */
    IPage<FaultReportVO> getCurrentUserFaultReportsVO(Integer pageNum, Integer pageSize);

    /**
     * 根据订单ID获取故障报修列表
     */
    List<FaultReportVO> getFaultReportsByOrderId(Long orderId);

    /**
     * 获取当前用户的故障上报列表
     */
    IPage<FaultReport> getCurrentUserFaultReports(Integer pageNum, Integer pageSize);

    /**
     * 分页查询故障上报（管理员）
     */
    IPage<FaultReport> pageFaultReports(Integer pageNum, Integer pageSize, Integer auditStatus);

    /**
     * 分页查询故障上报VO（管理员，带关联信息）
     */
    IPage<FaultReportVO> pageFaultReportsVO(Integer pageNum, Integer pageSize, Integer auditStatus);

    /**
     * 审核故障上报（管理员）
     */
    void auditFaultReport(Long id, AuditDTO dto);

    /**
     * 获取故障上报详情
     */
    FaultReport getFaultReportDetail(Long id);

    /**
     * 获取故障上报详情VO（带关联信息）
     */
    FaultReportVO getFaultReportDetailVO(Long id);
}
