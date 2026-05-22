package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.dto.MaintenanceTicketDTO;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.entity.MaintenanceTicket;
import com.drone.rental.service.FaultReportService;
import com.drone.rental.service.MaintenanceTicketService;
import com.drone.rental.vo.FaultReportVO;
import com.drone.rental.vo.MaintenanceTicketVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备维护管理控制器（管理员端）
 */
@Api(tags = "设备维护-管理员端")
@RestController
@RequestMapping("/admin/maintenance")
public class AdminMaintenanceController {

    @Autowired
    private FaultReportService faultReportService;

    @Autowired
    private MaintenanceTicketService maintenanceTicketService;

    // ================ 故障上报管理 ================

    @ApiOperation("分页查询故障上报列表")
    @GetMapping("/fault/list")
    public Result<IPage<FaultReportVO>> pageFaultReports(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("审核状态") @RequestParam(required = false) Integer auditStatus) {
        IPage<FaultReportVO> page = faultReportService.pageFaultReportsVO(pageNum, pageSize, auditStatus);
        return Result.success(page);
    }

    @ApiOperation("获取故障上报详情")
    @GetMapping("/fault/{id}")
    public Result<FaultReportVO> getFaultReportDetail(@PathVariable Long id) {
        FaultReportVO report = faultReportService.getFaultReportDetailVO(id);
        return Result.success(report);
    }

    @ApiOperation("审核故障上报（确认故障将自动生成维修工单）")
    @PutMapping("/fault/{id}/audit")
    public Result<Void> auditFaultReport(
            @PathVariable Long id,
            @Validated @RequestBody AuditDTO dto) {
        faultReportService.auditFaultReport(id, dto);
        return Result.success();
    }

    // ================ 维修工单管理 ================

    @ApiOperation("分页查询维修工单列表")
    @GetMapping("/ticket/list")
    public Result<IPage<MaintenanceTicketVO>> pageMaintenanceTickets(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("无人机ID") @RequestParam(required = false) Long droneId,
            @ApiParam("设备型号") @RequestParam(required = false) String droneModel,
            @ApiParam("维修类型") @RequestParam(required = false) String type) {
        IPage<MaintenanceTicketVO> result = maintenanceTicketService.pageMaintenanceTicketsVO(page, pageSize, status, droneId, droneModel, type);
        return Result.success(result);
    }

    @ApiOperation("获取维修工单详情")
    @GetMapping("/ticket/{id}")
    public Result<MaintenanceTicketVO> getTicketDetail(@PathVariable Long id) {
        MaintenanceTicketVO ticket = maintenanceTicketService.getTicketDetailVO(id);
        return Result.success(ticket);
    }

    @ApiOperation("更新维修工单")
    @PutMapping("/ticket/{id}")
    public Result<Void> updateMaintenanceTicket(
            @PathVariable Long id,
            @RequestBody MaintenanceTicketDTO dto) {
        maintenanceTicketService.updateMaintenanceTicket(id, dto);
        return Result.success();
    }

    @ApiOperation("开始维修")
    @PostMapping("/ticket/{id}/start")
    public Result<Void> startMaintenance(@PathVariable Long id) {
        maintenanceTicketService.startMaintenance(id);
        return Result.success();
    }

    @ApiOperation("完成维修")
    @PostMapping("/ticket/{id}/complete")
    public Result<Void> completeMaintenance(
            @PathVariable Long id,
            @RequestBody(required = false) MaintenanceTicketDTO dto) {
        maintenanceTicketService.completeMaintenance(id, dto);
        return Result.success();
    }
}
