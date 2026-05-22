import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, adminLogin, getCurrentUser, getAdminInfo } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const isAdmin = ref(localStorage.getItem('isAdmin') === 'true')

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 用户是否已实名认证
  const isVerified = computed(() => user.value?.status === 1)

  // 用户可以租赁的条件：已登录 + 已实名认证
  const canRent = computed(() => isLoggedIn.value && isVerified.value)

  // 设置token
  const setToken = (newToken, admin = false) => {
    token.value = newToken
    isAdmin.value = admin
    localStorage.setItem('token', newToken)
    localStorage.setItem('isAdmin', String(admin))
  }

  // 设置用户信息
  const setUser = (userInfo) => {
    user.value = userInfo
  }

  // 用户登录
  const userLogin = async (credentials) => {
    const res = await login(credentials)
    const { token: newToken, ...userInfo } = res.data
    setToken(newToken, false)
    setUser(userInfo)
    // 登录成功后获取完整用户信息（包含头像等）
    await fetchUserInfo()
    return res
  }

  // 用户注册
  const userRegister = async (data) => {
    const res = await register(data)
    // 注册成功后自动登录
    const { token: newToken, ...userInfo } = res.data
    setToken(newToken, false)
    setUser(userInfo)
    // 获取完整用户信息
    await fetchUserInfo()
    return res
  }

  // 管理员登录
  const adminUserLogin = async (credentials) => {
    const res = await adminLogin(credentials)
    const { token: newToken, ...userInfo } = res.data
    setToken(newToken, true)
    setUser(userInfo)
    // 获取完整用户信息（包含头像等）
    await fetchUserInfo()
    return res
  }

  // 获取当前用户信息
  const fetchUserInfo = async () => {
    if (!token.value) return null

    try {
      if (isAdmin.value) {
        const res = await getAdminInfo()
        setUser(res.data)
      } else {
        const res = await getCurrentUser()
        setUser(res.data)
      }
      return user.value
    } catch (error) {
      // 获取用户信息失败，清除登录状态
      logout()
      return null
    }
  }

  // 更新用户信息（本地）
  const updateUserInfo = (newInfo) => {
    if (user.value) {
      user.value = { ...user.value, ...newInfo }
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    user.value = null
    isAdmin.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('isAdmin')
  }

  // 初始化时检查登录状态
  const initAuth = async () => {
    if (token.value) {
      await fetchUserInfo()
    }
  }

  return {
    // 状态
    token,
    user,
    isAdmin,

    // 计算属性
    isLoggedIn,
    isVerified,
    canRent,

    // 方法
    setToken,
    setUser,
    userLogin,
    userRegister,
    adminUserLogin,
    fetchUserInfo,
    updateUserInfo,
    logout,
    initAuth
  }
}, {
  // 持久化配置（可选，如果需要更复杂的持久化）
  persist: false
})
