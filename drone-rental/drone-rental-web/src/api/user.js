import { get, post, put, upload } from './request'

// 获取用户信息
export const getUserInfo = () => {
  return get('/user/info')
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return put('/user/info', data)
}

// 修改密码
export const updatePassword = (data) => {
  return put('/user/password', null, { params: data })
}

// 提交飞行资质
export const submitQualification = (data) => {
  return post('/user/qualification', data)
}

// 提交实名认证（映射到资质提交）
export const submitRealName = (data) => {
  // 将实名认证数据映射为资质数据格式
  const qualificationData = {
    certificateNo: data.idCard,
    certificateType: '身份证',
    certificateImage: data.certificate,
    validStartDate: new Date().toISOString().split('T')[0],
    validEndDate: new Date(new Date().setFullYear(new Date().getFullYear() + 10)).toISOString().split('T')[0]
  }
  return post('/user/qualification', qualificationData)
}

// 上传证书图片
export const uploadCertificate = (file) => {
  return upload('/common/upload', file, 'file')
}

// 获取当前用户的飞行资质
export const getQualification = () => {
  return get('/user/qualification')
}

// 获取我的订单
export const getMyOrders = (params) => {
  return get('/user/orders', params)
}
