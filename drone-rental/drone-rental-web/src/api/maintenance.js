import { get, post, put, upload } from './request'

// ============ 用户端 ============

// 上传故障图片
export const uploadFaultImage = (file) => {
  return upload('/common/upload', file, 'file')
}

// 提交故障报修
export const submitFault = (data) => {
  return post('/fault/report', data)
}

// 获取我的报修记录
export const getMyFaults = (params) => {
  return get('/fault/my', params)
}

// 获取报修详情
export const getFaultDetail = (id) => {
  return get(`/fault/${id}`)
}

// 获取报修对应的维修工单
export const getFaultMaintenance = (faultId) => {
  return get(`/fault/${faultId}/maintenance`)
}

// 根据订单ID获取故障报修列表
export const getFaultsByOrderId = (orderId) => {
  return get(`/fault/order/${orderId}`)
}

// ============ 管理端 ============

// 获取故障报修列表
export const getFaultList = (params) => {
  return get('/admin/maintenance/fault/list', params)
}

// 获取故障报修详情
export const getAdminFaultDetail = (id) => {
  return get(`/admin/maintenance/fault/${id}`)
}

// 审核故障报修
export const auditFault = (id, data) => {
  return put(`/admin/maintenance/fault/${id}/audit`, data)
}

// 获取维修工单列表
export const getMaintenanceList = (params) => {
  return get('/admin/maintenance/ticket/list', params)
}

// 获取维修工单详情
export const getMaintenanceDetail = (id) => {
  return get(`/admin/maintenance/ticket/${id}`)
}

// 更新维修工单
export const updateMaintenance = (id, data) => {
  return put(`/admin/maintenance/ticket/${id}`, data)
}

// 开始维修
export const startMaintenance = (id) => {
  return post(`/admin/maintenance/ticket/${id}/start`)
}

// 完成维修
export const completeMaintenance = (id, data) => {
  return post(`/admin/maintenance/ticket/${id}/complete`, data)
}
