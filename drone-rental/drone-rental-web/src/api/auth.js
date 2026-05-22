import { post, get } from './request'

// 用户登录
export const login = (data) => {
  return post('/auth/login', data)
}

// 用户注册
export const register = (data) => {
  return post('/auth/register', data)
}

// 管理员登录
export const adminLogin = (data) => {
  return post('/auth/admin/login', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return get('/user/info')
}

// 获取管理员信息（与用户共用同一接口）
export const getAdminInfo = () => {
  return get('/user/info')
}
