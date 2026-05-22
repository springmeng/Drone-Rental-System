import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia

// 导出stores
export { useAuthStore } from './auth'
export { useAppStore } from './app'
