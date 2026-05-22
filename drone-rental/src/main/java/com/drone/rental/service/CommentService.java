package com.drone.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.drone.rental.dto.CommentDTO;
import com.drone.rental.entity.Comment;
import com.drone.rental.vo.CommentVO;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发表评论
     */
    void addComment(CommentDTO dto);

    /**
     * 获取当前用户的评论列表
     */
    IPage<Comment> getCurrentUserComments(Integer pageNum, Integer pageSize);

    /**
     * 获取无人机的评论列表
     */
    IPage<Comment> getDroneComments(Long droneId, Integer pageNum, Integer pageSize);

    /**
     * 删除评论（用户删除自己的）
     */
    void deleteComment(Long id);

    /**
     * 分页查询评论（管理员）- 带关联信息
     */
    IPage<CommentVO> pageCommentsVO(Integer pageNum, Integer pageSize, Long droneId, Integer status, String userName, String droneModel, Integer rating, Boolean hasReply);

    /**
     * 分页查询评论（管理员）
     */
    IPage<Comment> pageComments(Integer pageNum, Integer pageSize, Long droneId, Integer status, String userName, String droneModel, Integer rating, Boolean hasReply);

    /**
     * 屏蔽/恢复评论（管理员）
     */
    void updateCommentStatus(Long id, Integer status);

    /**
     * 回复评论（管理员）
     */
    void replyComment(Long id, String replyContent);
}
