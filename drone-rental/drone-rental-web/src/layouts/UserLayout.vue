<template>
  <div class="user-layout">
    <!-- 导航栏 -->
    <header class="user-header">
      <div class="user-header__container">
        <div class="user-header__logo" @click="router.push('/')">
          <div class="user-header__logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
              <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="12" r="2" fill="currentColor"/>
            </svg>
          </div>
          <span class="user-header__logo-text">无人机租赁系统</span>
        </div>

        <nav class="user-header__nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="user-header__nav-item"
            :class="{ 'user-header__nav-item--active': isActive(item.path) }"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </router-link>
        </nav>

        <div class="user-header__actions">
          <template v-if="authStore.isLoggedIn">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-header__user">
                <el-avatar :size="36" :src="avatarUrl">
                  {{ authStore.user?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="user-header__username">{{ authStore.user?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </template>
        </div>

        <!-- 移动端菜单按钮 -->
        <button class="user-header__mobile-btn" @click="mobileMenuOpen = !mobileMenuOpen">
          <el-icon :size="24"><Menu /></el-icon>
        </button>
      </div>
    </header>

    <!-- 移动端菜单 -->
    <Transition name="slide">
      <div v-if="mobileMenuOpen" class="user-mobile-menu">
        <div class="user-mobile-menu__overlay" @click="mobileMenuOpen = false"></div>
        <div class="user-mobile-menu__content">
          <div class="user-mobile-menu__header">
            <span>菜单</span>
            <el-icon @click="mobileMenuOpen = false"><Close /></el-icon>
          </div>
          <nav class="user-mobile-menu__nav">
            <router-link
              v-for="item in navItems"
              :key="item.path"
              :to="item.path"
              class="user-mobile-menu__item"
              @click="mobileMenuOpen = false"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </router-link>
          </nav>
          <div v-if="authStore.isLoggedIn" class="user-mobile-menu__user">
            <el-avatar :size="48" :src="avatarUrl">
              {{ authStore.user?.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="user-mobile-menu__user-info">
              <span class="user-mobile-menu__user-name">{{ authStore.user?.nickname }}</span>
              <span class="user-mobile-menu__user-phone">{{ authStore.user?.phone }}</span>
            </div>
          </div>
          <div class="user-mobile-menu__actions">
            <template v-if="authStore.isLoggedIn">
              <el-button @click="router.push('/profile'); mobileMenuOpen = false">个人中心</el-button>
              <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
            </template>
            <template v-else>
              <el-button type="primary" @click="router.push('/login'); mobileMenuOpen = false">登录</el-button>
              <el-button @click="router.push('/register'); mobileMenuOpen = false">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 主内容区 -->
    <main class="user-main">
      <router-view v-slot="{ Component }">
        <Transition name="fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="user-footer">
      <div class="user-footer__container">
        <div class="user-footer__info">
          <div class="user-footer__brand">
            <div class="user-footer__logo-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
                <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="12" r="2" fill="currentColor"/>
              </svg>
            </div>
            <span>无人机租赁系统</span>
          </div>
          <p class="user-footer__desc">专业无人机租赁服务平台，提供各类无人机设备租赁及售后服务</p>
        </div>
        <div class="user-footer__links">
          <div class="user-footer__links-group">
            <h4>快速链接</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/drones">设备浏览</router-link>
            <router-link to="/orders">我的订单</router-link>
          </div>
          <div class="user-footer__links-group">
            <h4>帮助支持</h4>
            <a href="#">租赁指南</a>
            <a href="#">常见问题</a>
            <a href="#">联系我们</a>
          </div>
        </div>
      </div>
      <div class="user-footer__bottom">
        <span>© {{ new Date().getFullYear() }} 无人机租赁系统</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled,
  Goods,
  Document,
  User,
  ArrowDown,
  SwitchButton,
  Menu,
  Close
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const mobileMenuOpen = ref(false)

// 获取头像完整URL
const avatarUrl = computed(() => {
  const avatar = authStore.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `/api${avatar}`
})

const navItems = [
  { path: '/', title: '首页', icon: HomeFilled },
  { path: '/drones', title: '设备浏览', icon: Goods },
  { path: '/orders', title: '我的订单', icon: Document },
  { path: '/profile', title: '个人中心', icon: User }
]

const isActive = (path) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    authStore.logout()
    mobileMenuOpen.value = false
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // 取消退出
  }
}
</script>

<style lang="scss" scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

// 导航栏
.user-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid #e2e8f0;

  &__container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px;
    height: 72px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 32px;
  }

  &__logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: opacity 0.3s;

    &:hover {
      opacity: 0.8;
    }
  }

  &__logo-icon {
    width: 40px;
    height: 40px;
    color: #3b82f6;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  &__logo-text {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    letter-spacing: 0.5px;
  }

  &__nav {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    justify-content: center;
  }

  &__nav-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 500;
    color: #64748b;
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      color: #0f172a;
      background: rgba(59, 130, 246, 0.06);
    }

    &--active {
      color: #3b82f6;
      background: rgba(59, 130, 246, 0.1);
    }

    .el-icon {
      font-size: 18px;
    }
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 12px 6px 6px;
    border-radius: 28px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(59, 130, 246, 0.3);
    }
  }

  &__username {
    font-size: 14px;
    font-weight: 500;
    color: #0f172a;
  }

  &__mobile-btn {
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
}

// 移动端菜单
.user-mobile-menu {
  position: fixed;
  inset: 0;
  z-index: 200;

  &__overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(4px);
  }

  &__content {
    position: absolute;
    top: 0;
    right: 0;
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

    span {
      font-size: 18px;
      font-weight: 600;
      color: #0f172a;
    }

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
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 12px;
    font-size: 15px;
    color: #64748b;
    text-decoration: none;
    transition: all 0.3s;

    &:hover,
    &.router-link-active {
      color: #3b82f6;
      background: rgba(59, 130, 246, 0.1);
    }

    .el-icon {
      font-size: 20px;
    }
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    border-top: 1px solid #e2e8f0;
    background: #f8fafc;
  }

  &__user-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__user-name {
    font-size: 15px;
    font-weight: 600;
    color: #0f172a;
  }

  &__user-phone {
    font-size: 13px;
    color: #94a3b8;
  }

  &__actions {
    padding: 16px 20px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    border-top: 1px solid #e2e8f0;

    .el-button {
      width: 100%;
    }
  }
}

// 主内容区
.user-main {
  flex: 1;
  padding: 32px 24px;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

// 页脚
.user-footer {
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
  margin-top: auto;

  &__container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 48px 24px;
    display: flex;
    justify-content: space-between;
    gap: 48px;
  }

  &__info {
    max-width: 360px;
  }

  &__brand {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 16px;

    span {
      font-size: 18px;
      font-weight: 700;
      color: #0f172a;
    }
  }

  &__logo-icon {
    width: 32px;
    height: 32px;
    color: #3b82f6;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  &__desc {
    font-size: 14px;
    color: #94a3b8;
    line-height: 1.6;
    margin: 0;
  }

  &__links {
    display: flex;
    gap: 64px;
  }

  &__links-group {
    display: flex;
    flex-direction: column;
    gap: 12px;

    h4 {
      font-size: 15px;
      font-weight: 600;
      color: #0f172a;
      margin: 0 0 4px;
    }

    a {
      font-size: 14px;
      color: #94a3b8;
      text-decoration: none;
      transition: color 0.3s;

      &:hover {
        color: #3b82f6;
      }
    }
  }

  &__bottom {
    padding: 20px 24px;
    text-align: center;
    border-top: 1px solid #e2e8f0;

    span {
      font-size: 13px;
      color: #94a3b8;
    }
  }
}

// 响应式
@media (max-width: 992px) {
  .user-header {
    &__nav {
      display: none;
    }

    &__mobile-btn {
      display: flex;
    }
  }

  .user-footer {
    &__container {
      flex-direction: column;
      gap: 32px;
    }

    &__links {
      gap: 48px;
    }
  }
}

@media (max-width: 640px) {
  .user-header {
    &__container {
      padding: 0 16px;
      height: 64px;
    }

    &__logo-text {
      display: none;
    }

    &__actions {
      display: none;
    }
  }

  .user-main {
    padding: 24px 16px;
  }

  .user-footer {
    &__container {
      padding: 32px 16px;
    }

    &__links {
      flex-direction: column;
      gap: 24px;
    }
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
  .user-mobile-menu__overlay {
    opacity: 0;
  }

  .user-mobile-menu__content {
    transform: translateX(100%);
  }
}
</style>
