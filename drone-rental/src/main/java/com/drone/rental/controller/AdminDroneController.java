package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.DroneDTO;
import com.drone.rental.entity.Drone;
import com.drone.rental.service.DroneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 无人机管理控制器（管理员端）
 */
@Api(tags = "无人机管理-管理员端")
@RestController
@RequestMapping("/admin/drone")
public class AdminDroneController {

    @Autowired
    private DroneService droneService;

    @ApiOperation("分页查询无人机列表")
    @GetMapping("/list")
    public Result<IPage<Drone>> pageAllDrones(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关键词(型号/品牌)") @RequestParam(required = false) String keyword,
            @ApiParam("类型") @RequestParam(required = false) String type,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("上架状态") @RequestParam(required = false) Integer onShelf) {
        IPage<Drone> result = droneService.pageAllDrones(page, pageSize, keyword, type, status, onShelf);
        return Result.success(result);
    }

    @ApiOperation("获取无人机详情")
    @GetMapping("/{id}")
    public Result<Drone> getDroneDetail(@PathVariable Long id) {
        Drone drone = droneService.getDroneDetail(id);
        return Result.success(drone);
    }

    @ApiOperation("添加无人机")
    @PostMapping("/add")
    public Result<Void> addDrone(@Validated @RequestBody DroneDTO dto) {
        droneService.addDrone(dto);
        return Result.success();
    }

    @ApiOperation("更新无人机")
    @PutMapping("/{id}")
    public Result<Void> updateDrone(@PathVariable Long id, @Validated @RequestBody DroneDTO dto) {
        droneService.updateDrone(id, dto);
        return Result.success();
    }

    @ApiOperation("删除无人机")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDrone(@PathVariable Long id) {
        droneService.deleteDrone(id);
        return Result.success();
    }

    @ApiOperation("上架/下架无人机")
    @PutMapping("/{id}/shelf")
    public Result<Void> updateOnShelf(
            @PathVariable Long id,
            @ApiParam("上架状态: 0-下架, 1-上架") @RequestParam Integer onShelf) {
        droneService.updateOnShelf(id, onShelf);
        return Result.success();
    }

    @ApiOperation("更新无人机状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态: 0-缺货, 1-在售, 2-维护中") @RequestParam Integer status) {
        droneService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("更新无人机库存")
    @PutMapping("/{id}/stock")
    public Result<Void> updateStock(
            @PathVariable Long id,
            @ApiParam("新库存数量") @RequestBody java.util.Map<String, Integer> body) {
        Integer stock = body.get("stock");
        droneService.updateStock(id, stock);
        return Result.success();
    }
}
