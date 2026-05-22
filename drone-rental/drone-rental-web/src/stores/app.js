import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 全局加载状态
  const loading = ref(false)

  // 页面标题
  const pageTitle = ref('')

  // 侧边栏折叠状态（管理端）
  const sidebarCollapsed = ref(false)

  // 移动端菜单状态
  const mobileMenuOpen = ref(false)

  // 主题模式（预留）
  const themeMode = ref('dark')

  // 设置加载状态
  const setLoading = (value) => {
    loading.value = value
  }

  // 设置页面标题
  const setPageTitle = (title) => {
    pageTitle.value = title
    document.title = title ? `${title} -无人机租赁系统` : '无人机租赁系统'
  }

  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 切换移动端菜单
  const toggleMobileMenu = () => {
    mobileMenuOpen.value = !mobileMenuOpen.value
  }

  // 关闭移动端菜单
  const closeMobileMenu = () => {
    mobileMenuOpen.value = false
  }

  return {
    // 状态
    loading,
    pageTitle,
    sidebarCollapsed,
    mobileMenuOpen,
    themeMode,

    // 方法
    setLoading,
    setPageTitle,
    toggleSidebar,
    toggleMobileMenu,
    closeMobileMenu
  }
})
