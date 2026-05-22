package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.entity.AirspaceRecord;
import com.drone.rental.service.AirspaceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 空域备案管理控制器（管理员端）
 */
@Api(tags = "空域备案-管理员端")
@RestController
@RequestMapping("/admin/airspace")
public class AdminAirspaceController {

    @Autowired
    private AirspaceRecordService airspaceRecordService;

    @ApiOperation("分页查询空域备案列表")
    @GetMapping("/list")
    public Result<IPage<AirspaceRecord>> pageAirspaceRecords(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("审核状态") @RequestParam(required = false) Integer auditStatus,
            @ApiParam("区域名称") @RequestParam(required = false) String regionName) {
        IPage<AirspaceRecord> page = airspaceRecordService.pageAirspaceRecords(pageNum, pageSize, auditStatus, regionName);
        return Result.success(page);
    }

    @ApiOperation("审核空域备案")
    @PutMapping("/{id}/audit")
    public Result<Void> auditAirspaceRecord(
            @PathVariable Long id,
            @Validated @RequestBody AuditDTO dto) {
        airspaceRecordService.auditAirspaceRecord(id, dto);
        return Result.success();
    }

    @ApiOperation("获取空域备案详情")
    @GetMapping("/{id}")
    public Result<AirspaceRecord> getAirspaceRecordDetail(@PathVariable Long id) {
        AirspaceRecord record = airspaceRecordService.getById(id);
        return Result.success(record);
    }
}
