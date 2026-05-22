package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.FaultReportDTO;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.entity.MaintenanceTicket;
import com.drone.rental.service.FaultReportService;
import com.drone.rental.service.MaintenanceTicketService;
import com.drone.rental.vo.FaultReportVO;

import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 故障上报控制器（用户端）
 */
@Api(tags = "故障上报-用户端")
@RestController
@RequestMapping("/fault")
public class FaultController {

    @Autowired
    private FaultReportService faultReportService;

    @Autowired
    private MaintenanceTicketService maintenanceTicketService;

    @ApiOperation("上报故障")
    @PostMapping("/report")
    public Result<Void> reportFault(@Validated @RequestBody FaultReportDTO dto) {
        faultReportService.reportFault(dto);
        return Result.success();
    }

    @ApiOperation("获取当前用户的故障上报列表")
    @GetMapping("/my")
    public Result<IPage<FaultReportVO>> getCurrentUserFaultReports(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<FaultReportVO> page = faultReportService.getCurrentUserFaultReportsVO(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("根据订单ID获取故障报修列表")
    @GetMapping("/order/{orderId}")
    public Result<List<FaultReportVO>> getFaultReportsByOrderId(@PathVariable Long orderId) {
        List<FaultReportVO> list = faultReportService.getFaultReportsByOrderId(orderId);
        return Result.success(list);
    }

    @ApiOperation("获取故障上报详情")
    @GetMapping("/{id}")
    public Result<FaultReport> getFaultReportDetail(@PathVariable Long id) {
        FaultReport report = faultReportService.getFaultReportDetail(id);
        return Result.success(report);
    }

    @ApiOperation("查看维修进度")
    @GetMapping("/{faultReportId}/maintenance")
    public Result<MaintenanceTicket> getMaintenanceProgress(@PathVariable Long faultReportId) {
        MaintenanceTicket ticket = maintenanceTicketService.getByFaultReportId(faultReportId);
        return Result.success(ticket);
    }
}
