import { get, post, put, del } from './request'

// ============ 用户管理 ============

// 获取用户列表
export const getUserList = (params) => {
  return get('/admin/user/list', params)
}

// 获取用户详情
export const getUserDetail = (id) => {
  return get(`/admin/user/${id}`)
}

// 更新用户状态（启用/禁用）
export const updateUserStatus = (id, status) => {
  return put(`/admin/user/${id}/status?status=${status}`)
}

// 更新用户诚信状态
export const updateCreditStatus = (id, creditStatus, reason = '') => {
  let url = `/admin/user/${id}/credit?creditStatus=${creditStatus}`
  if (reason) {
    url += `&reason=${encodeURIComponent(reason)}`
  }
  return put(url)
}

// 获取用户诚信记录
export const getCreditRecords = (userId) => {
  return get(`/admin/user/${userId}/credit-records`)
}

// 重置用户密码
export const resetUserPassword = (id) => {
  return put(`/admin/user/${id}/reset-password`)
}

// ============ 资质审核 ============

// 获取待审核列表
export const getPendingAudits = (params) => {
  return get('/admin/user/qualification/list', params)
}

// 获取审核详情
export const getAuditDetail = (id) => {
  return get(`/admin/user/qualification/${id}`)
}

// 审核资质
export const auditQualification = (id, data) => {
  return put(`/admin/user/qualification/${id}/audit`, data)
}

// 通过审核
export const approveAudit = (id) => {
  return put(`/admin/user/qualification/${id}/audit`, { auditStatus: 1 })
}

// 拒绝审核
export const rejectAudit = (id, data) => {
  return put(`/admin/user/qualification/${id}/audit`, { auditStatus: 2, auditRemark: data.reason })
}

// 获取审核统计
export const getAuditStats = () => {
  return get('/admin/user/qualification/stats')
}

// ============ 控制台统计 ============

// 获取控制台统计数据
export const getDashboardStats = () => {
  return get('/admin/dashboard/stats')
}

// 获取收入趋势
export const getRevenueTrend = (params) => {
  return get('/admin/dashboard/revenue-trend', params)
}

// 获取订单趋势
export const getOrderTrend = (params) => {
  return get('/admin/dashboard/order-trend', params)
}

// 获取热门无人机
export const getPopularDrones = (limit = 5) => {
  return get('/admin/dashboard/popular-drones', { limit })
}

// 获取最新订单
export const getRecentOrders = (limit = 5) => {
  return get('/admin/dashboard/recent-orders', { limit })
}

// 获取待办事项
export const getTodoItems = () => {
  return get('/admin/dashboard/todos')
}
