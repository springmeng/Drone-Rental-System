import { get, post, put, del } from './request'

// ============ 用户端 ============

// 获取无人机评价列表（通过 DroneController）
export const getDroneComments = (droneId, params) => {
  return get(`/drone/${droneId}/comments`, params)
}

// 添加评价
export const addComment = (data) => {
  return post('/comment/add', data)
}

// 获取我的评价
export const getMyComments = (params) => {
  return get('/comment/my', params)
}

// 删除我的评价
export const deleteMyComment = (id) => {
  return del(`/comment/${id}`)
}

// ============ 管理端 ============

// 获取评价列表
export const getAdminComments = (params) => {
  return get('/admin/comment/list', params)
}

// 更新评价状态（屏蔽/显示）
export const updateCommentStatus = (id, status) => {
  return put(`/admin/comment/${id}/status`, { status })
}

// 回复评价
export const replyComment = (id, content) => {
  return post(`/admin/comment/${id}/reply`, { replyContent: content })
}

// 删除评价
export const deleteComment = (id) => {
  return del(`/admin/comment/${id}`)
}
