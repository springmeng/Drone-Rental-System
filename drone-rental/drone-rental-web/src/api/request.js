import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()

    // 如果有token，添加到请求头
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }

    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 根据后端返回的code判断请求是否成功
    if (res.code === 200) {
      return res
    }

    // 处理业务错误
    const errorMessage = res.message || '请求失败'

    // 特殊错误码处理
    switch (res.code) {
      case 401:
        // 未登录或token过期
        handleUnauthorized()
        break
      case 403:
        // 无权限
        ElMessage.error('无权限访问')
        break
      default:
        ElMessage.error(errorMessage)
    }

    return Promise.reject(new Error(errorMessage))
  },
  (error) => {
    console.error('响应错误:', error)

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          handleUnauthorized()
          break
        case 403:
          ElMessage.error('无权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '网络错误，请稍后重试')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

// 处理未授权
let isShowingLoginDialog = false
const handleUnauthorized = () => {
  const authStore = useAuthStore()

  if (isShowingLoginDialog) return

  isShowingLoginDialog = true
  authStore.logout()

  ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
    confirmButtonText: '重新登录',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      router.push('/login')
    })
    .finally(() => {
      isShowingLoginDialog = false
    })
}

export default service

// 封装常用请求方法
export const get = (url, params, config = {}) => {
  return service.get(url, { params, ...config })
}

export const post = (url, data, config = {}) => {
  return service.post(url, data, config)
}

export const put = (url, data, config = {}) => {
  return service.put(url, data, config)
}

export const del = (url, params, config = {}) => {
  return service.delete(url, { params, ...config })
}

// 上传文件
export const upload = (url, file, fieldName = 'file', config = {}) => {
  const formData = new FormData()
  formData.append(fieldName, file)

  return service.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...config
  })
}
