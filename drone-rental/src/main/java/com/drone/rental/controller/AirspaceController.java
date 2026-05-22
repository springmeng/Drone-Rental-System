package com.drone.rental.controller;

import com.drone.rental.common.Result;
import com.drone.rental.dto.AirspaceRecordDTO;
import com.drone.rental.entity.AirspaceRecord;
import com.drone.rental.service.AirspaceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 空域备案控制器（用户端）
 */
@Api(tags = "空域备案-用户端")
@RestController
@RequestMapping("/airspace")
public class AirspaceController {

    @Autowired
    private AirspaceRecordService airspaceRecordService;

    @ApiOperation("提交空域备案")
    @PostMapping("/submit")
    public Result<Void> submitAirspaceRecord(@Validated @RequestBody AirspaceRecordDTO dto) {
        airspaceRecordService.submitAirspaceRecord(dto);
        return Result.success();
    }

    @ApiOperation("获取当前用户的空域备案列表")
    @GetMapping("/list")
    public Result<List<AirspaceRecord>> getCurrentUserAirspaceRecords() {
        List<AirspaceRecord> list = airspaceRecordService.getCurrentUserAirspaceRecords();
        return Result.success(list);
    }

    @ApiOperation("获取当前用户已通过审核的空域备案")
    @GetMapping("/approved")
    public Result<List<AirspaceRecord>> getCurrentUserApprovedRecords() {
        List<AirspaceRecord> list = airspaceRecordService.getCurrentUserApprovedRecords();
        return Result.success(list);
    }
}
