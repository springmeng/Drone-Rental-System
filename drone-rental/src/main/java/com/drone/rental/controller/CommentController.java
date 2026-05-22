package com.drone.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.drone.rental.common.Result;
import com.drone.rental.dto.CommentDTO;
import com.drone.rental.entity.Comment;
import com.drone.rental.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器（用户端）
 */
@Api(tags = "评论管理-用户端")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("发表评论")
    @PostMapping("/add")
    public Result<Void> addComment(@Validated @RequestBody CommentDTO dto) {
        commentService.addComment(dto);
        return Result.success();
    }

    @ApiOperation("获取当前用户的评论列表")
    @GetMapping("/my")
    public Result<IPage<Comment>> getCurrentUserComments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Comment> page = commentService.getCurrentUserComments(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
