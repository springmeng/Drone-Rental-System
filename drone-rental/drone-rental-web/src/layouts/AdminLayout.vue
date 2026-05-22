<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar" :class="{ 'admin-sidebar--collapsed': collapsed }">
      <div class="admin-sidebar__header">
        <div class="admin-sidebar__logo">
          <div class="admin-sidebar__logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
              <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="12" r="2" fill="currentColor"/>
            </svg>
          </div>
          <Transition name="fade">
            <span v-if="!collapsed" class="admin-sidebar__logo-text">管理后台</span>
          </Transition>
        </div>
      </div>

      <nav class="admin-sidebar__nav">
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="admin-sidebar__item"
          :class="{ 'admin-sidebar__item--active': isActive(item.path) }"
          @click="router.push(item.path)"
        >
          <el-icon :size="20"><component :is="item.icon" /></el-icon>
          <Transition name="fade">
            <span v-if="!collapsed" class="admin-sidebar__item-text">{{ item.title }}</span>
          </Transition>
          <Transition name="fade">
            <el-badge
              v-if="!collapsed && item.badge"
              :value="item.badge"
              :max="99"
              class="admin-sidebar__badge"
            />
          </Transition>
        </div>
      </nav>

      <div class="admin-sidebar__footer">
        <div class="admin-sidebar__item" @click="collapsed = !collapsed">
          <el-icon :size="20">
            <Expand v-if="collapsed" />
            <Fold v-else />
          </el-icon>
          <Transition name="fade">
            <span v-if="!collapsed" class="admin-sidebar__item-text">收起菜单</span>
          </Transition>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="admin-content">
      <!-- 顶部栏 -->
      <header class="admin-header">
        <div class="admin-header__left">
          <button class="admin-header__menu-btn" @click="mobileMenuOpen = true">
            <el-icon :size="24"><Menu /></el-icon>
          </button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">后台管理</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentMenu">{{ currentMenu.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="admin-header__right">
          <!-- 待办提醒 -->
          <el-badge :value="pendingCount" :max="99" :hidden="pendingCount === 0">
            <el-button text circle @click="router.push('/admin/audits')">
              <el-icon :size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>

          <!-- 管理员信息 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-header__user">
              <el-avatar :size="36" :src="getAvatarUrl">
                {{ authStore.user?.username?.charAt(0) || 'A' }}
              </el-avatar>
              <div class="admin-header__user-info">
                <span class="admin-header__user-name">{{ authStore.user?.username || '管理员' }}</span>
                <span class="admin-header__user-role">系统管理员</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">
                  <el-icon><HomeFilled /></el-icon>返回前台
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="admin-main">
        <router-view v-slot="{ Component }">
          <Transition name="fade" mode="out-in">
            <component :is="Component" />
          </Transition>
        </router-view>
      </main>
    </div>

    <!-- 移动端侧边栏 -->
    <Transition name="slide">
      <div v-if="mobileMenuOpen" class="admin-mobile-menu">
        <div class="admin-mobile-menu__overlay" @click="mobileMenuOpen = false"></div>
        <div class="admin-mobile-menu__content">
          <div class="admin-mobile-menu__header">
            <div class="admin-sidebar__logo">
              <div class="admin-sidebar__logo-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                  <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
                  <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
                  <circle cx="12" cy="12" r="2" fill="currentColor"/>
                </svg>
              </div>
              <span class="admin-sidebar__logo-text">管理后台</span>
            </div>
            <el-icon @click="mobileMenuOpen = false"><Close /></el-icon>
          </div>
          <nav class="admin-mobile-menu__nav">
            <div
              v-for="item in menuItems"
              :key="item.path"
              class="admin-mobile-menu__item"
              :class="{ 'admin-mobile-menu__item--active': isActive(item.path) }"
              @click="router.push(item.path); mobileMenuOpen = false"
            >
              <el-icon :size="20"><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
              <el-badge v-if="item.badge" :value="item.badge" :max="99" />
            </div>
          </nav>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled,
  User,
  Document,
  Goods,
  SetUp,
  ChatLineSquare,
  Bell,
  ArrowDown,
  SwitchButton,
  Menu,
  Close,
  Expand,
  Fold,
  Warning
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAuditStats } from '@/api/admin'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const collapsed = ref(false)
const mobileMenuOpen = ref(false)
const pendingCount = ref(0)

// 获取头像完整URL
const getAvatarUrl = computed(() => {
  const avatar = authStore.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `/api${avatar}`
})

// 获取待审核数量
const fetchPendingCount = async () => {
  try {
    const res = await getAuditStats()
    pendingCount.value = res.data?.pending || 0
  } catch (error) {
    console.error('获取待审核数量失败:', error)
  }
}

// 菜单项使用 computed 以响应 pendingCount 变化
const menuItems = computed(() => [
  { path: '/admin', title: '控制台', icon: HomeFilled },
  { path: '/admin/users', title: '用户管理', icon: User },
  { path: '/admin/audits', title: '资质审核', icon: Document, badge: pendingCount.value || null },
  { path: '/admin/drones', title: '无人机管理', icon: Goods },
  { path: '/admin/orders', title: '订单管理', icon: Document },
  { path: '/admin/fault-audit', title: '故障报修审核', icon: Warning },
  { path: '/admin/maintenance', title: '维保管理', icon: SetUp },
  { path: '/admin/comments', title: '评价管理', icon: ChatLineSquare }
])

const currentMenu = computed(() => {
  return menuItems.value.find(item => {
    if (item.path === '/admin') {
      return route.path === '/admin'
    }
    return route.path.startsWith(item.path)
  })
})

const isActive = (path) => {
  if (path === '/admin') {
    return route.path === '/admin'
  }
  return route.path.startsWith(path)
}

const handleCommand = async (command) => {
  switch (command) {
    case 'home':
      router.push('/')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch {
        // 取消退出
      }
      break
  }
}

// 组件挂载时获取待审核数量
onMounted(() => {
  fetchPendingCount()
})
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  background: #f5f7fa;
}

// 侧边栏
.admin-sidebar {
  width: 260px;
  height: 100vh;
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-right: 1px solid #e2e8f0;
  transition: width 0.3s ease;

  &--collapsed {
    width: 72px;

    .admin-sidebar__logo {
      justify-content: center;
    }

    .admin-sidebar__item {
      justify-content: center;
      padding: 14px;
    }
  }

  &__header {
    padding: 20px;
    border-bottom: 1px solid #e2e8f0;
  }

  &__logo {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__logo-icon {
    width: 36px;
    height: 36px;
    color: #3b82f6;
    flex-shrink: 0;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  &__logo-text {
    font-size: 18px;
    font-weight: 700;
    color: #0f172a;
    white-space: nowrap;
  }

  &__nav {
    flex: 1;
    padding: 16px 12px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 12px;
    color: #64748b;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;

    &:hover {
      color: #0f172a;
      background: rgba(59, 130, 246, 0.06);
    }

    &--active {
      color: #3b82f6;
      background: rgba(59, 130, 246, 0.15);

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 24px;
        background: #3b82f6;
        border-radius: 0 3px 3px 0;
      }
    }
  }

  &__item-text {
    font-size: 15px;
    font-weight: 500;
    white-space: nowrap;
  }

  &__badge {
    margin-left: auto;
  }

  &__footer {
    padding: 12px;
    border-top: 1px solid #e2e8f0;
  }
}

// 主内容区
.admin-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

// 顶部栏
.admin-header {
  height: 72px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 50;

  &__left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__menu-btn {
    display: none;
    padding: 8px;
    background: transparent;
    border: none;
    color: #64748b;
    cursor: pointer;

    &:hover {
      color: #0f172a;
    }
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 6px 16px 6px 6px;
    border-radius: 28px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(59, 130, 246, 0.3);
    }
  }

  &__user-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__user-name {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    line-height: 1;
  }

  &__user-role {
    font-size: 12px;
    color: #94a3b8;
    line-height: 1;
  }

  :deep(.el-breadcrumb) {
    font-size: 14px;

    .el-breadcrumb__inner {
      color: #94a3b8;

      &.is-link:hover {
        color: #3b82f6;
      }
    }

    .el-breadcrumb__separator {
      color: #cbd5e1;
    }

    .el-breadcrumb__item:last-child .el-breadcrumb__inner {
      color: #0f172a;
    }
  }
}

// 页面内容
.admin-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

// 移动端侧边栏
.admin-mobile-menu {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: none;

  &__overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(4px);
  }

  &__content {
    position: absolute;
    top: 0;
    left: 0;
    width: 280px;
    height: 100%;
    background: #ffffff;
    display: flex;
    flex-direction: column;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
    border-bottom: 1px solid #e2e8f0;

    .el-icon {
      font-size: 24px;
      color: #64748b;
      cursor: pointer;

      &:hover {
        color: #0f172a;
      }
    }
  }

  &__nav {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 12px;
    font-size: 15px;
    color: #64748b;
    cursor: pointer;
    transition: all 0.3s;

    &:hover,
    &--active {
      color: #3b82f6;
      background: rgba(59, 130, 246, 0.1);
    }

    .el-badge {
      margin-left: auto;
    }
  }
}

// 响应式
@media (max-width: 1024px) {
  .admin-sidebar {
    display: none;
  }

  .admin-header {
    &__menu-btn {
      display: flex;
    }
  }

  .admin-mobile-menu {
    display: block;
  }
}

@media (max-width: 640px) {
  .admin-header {
    padding: 0 16px;

    &__user-info {
      display: none;
    }
  }

  .admin-main {
    padding: 16px;
  }
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  .admin-mobile-menu__overlay {
    opacity: 0;
  }

  .admin-mobile-menu__content {
    transform: translateX(-100%);
  }
}
</style>
