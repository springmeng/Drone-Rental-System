package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.entity.Comment;
import com.drone.rental.entity.Drone;
import com.drone.rental.service.CommentService;
import com.drone.rental.service.DroneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 无人机控制器（用户端）
 */
@Api(tags = "无人机管理-用户端")
@RestController
@RequestMapping("/drone")
public class DroneController {

    @Autowired
    private DroneService droneService;

    @Autowired
    private CommentService commentService;

    @ApiOperation("分页查询可租赁无人机列表")
    @GetMapping("/list")
    public Result<IPage<Drone>> listAvailableDrones(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("品牌") @RequestParam(required = false) String brand,
            @ApiParam("类型") @RequestParam(required = false) String type,
            @ApiParam("状态:1-可租赁,0-缺货") @RequestParam(required = false) Integer status,
            @ApiParam("最低价格") @RequestParam(required = false) java.math.BigDecimal minPrice,
            @ApiParam("最高价格") @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @ApiParam("排序字段") @RequestParam(defaultValue = "createdTime") String sortBy,
            @ApiParam("排序方向") @RequestParam(defaultValue = "desc") String sortOrder) {
        IPage<Drone> result = droneService.pageAvailableDrones(page, pageSize, keyword, brand, type, status, minPrice, maxPrice, sortBy, sortOrder);
        return Result.success(result);
    }

    @ApiOperation("获取无人机详情")
    @GetMapping("/detail/{id}")
    public Result<Drone> getDroneDetail(@PathVariable Long id) {
        Drone drone = droneService.getDroneDetail(id);
        return Result.success(drone);
    }

    @ApiOperation("获取无人机品牌列表")
    @GetMapping("/brands")
    public Result<java.util.List<String>> getDroneBrands() {
        java.util.List<String> brands = droneService.getAllBrands();
        return Result.success(brands);
    }

    @ApiOperation("获取无人机类型列表")
    @GetMapping("/types")
    public Result<java.util.List<String>> getDroneTypes() {
        java.util.List<String> types = droneService.getAllTypes();
        return Result.success(types);
    }

    @ApiOperation("获取无人机评论列表")
    @GetMapping("/{droneId}/comments")
    public Result<IPage<Comment>> getDroneComments(
            @PathVariable Long droneId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Comment> page = commentService.getDroneComments(droneId, pageNum, pageSize);
        return Result.success(page);
    }
}
