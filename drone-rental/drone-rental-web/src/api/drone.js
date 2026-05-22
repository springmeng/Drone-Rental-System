import { get, post, put, del } from './request'

// ============ 用户端 ============

// 获取无人机列表
export const getDroneList = (params) => {
  return get('/drone/list', params)
}

// 获取无人机详情
export const getDroneDetail = (id) => {
  return get(`/drone/detail/${id}`)
}

// 获取无人机品牌列表
export const getDroneBrands = () => {
  return get('/drone/brands')
}

// 获取无人机类型列表
export const getDroneTypes = () => {
  return get('/drone/types')
}

// 获取无人机评论
export const getDroneComments = (droneId, params) => {
  return get(`/drone/${droneId}/comments`, params)
}

// ============ 管理端 ============

// 获取无人机列表（管理端）
export const getAdminDroneList = (params) => {
  return get('/admin/drone/list', params)
}

// 获取无人机详情（管理端）
export const getAdminDroneDetail = (id) => {
  return get(`/admin/drone/${id}`)
}

// 新增无人机
export const createDrone = (data) => {
  return post('/admin/drone/add', data)
}

// 更新无人机
export const updateDrone = (id, data) => {
  return put(`/admin/drone/${id}`, data)
}

// 删除无人机
export const deleteDrone = (id) => {
  return del(`/admin/drone/${id}`)
}

// 更新无人机状态
export const updateDroneStatus = (id, status) => {
  return put(`/admin/drone/${id}/status?status=${status}`)
}

// 更新上架状态
export const updateDroneShelf = (id, onShelf) => {
  return put(`/admin/drone/${id}/shelf?onShelf=${onShelf}`)
}

// 更新库存
export const updateDroneStock = (id, stock) => {
  return put(`/admin/drone/${id}/stock`, { stock })
}
