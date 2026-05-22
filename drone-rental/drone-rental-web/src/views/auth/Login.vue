<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="auth-logo">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
              <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="12" r="2" fill="currentColor"/>
            </svg>
          </div>
          <h1 class="auth-title">无人机系统</h1>
          <p class="auth-subtitle">欢迎回来，请登录您的账户</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="auth-form"
          size="large"
          @submit.prevent="handleSubmit"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <div class="auth-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="auth-link">忘记密码？</a>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="auth-submit"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <span>还没有账户？</span>
          <router-link to="/register" class="auth-link">立即注册</router-link>
        </div>

        <div class="auth-divider">
          <span>其他登录方式</span>
        </div>

        <div class="auth-admin-entry">
          <router-link to="/admin/login" class="admin-link">
            <el-icon><Setting /></el-icon>
            管理员入口
          </router-link>
        </div>
      </div>

      <div class="auth-bg">
        <div class="auth-bg__content">
          <h2>专业无人机租赁平台</h2>
          <p>提供各类无人机设备租赁服务，满足您的航拍、测绘、农业等多种需求</p>
          <div class="auth-bg__features">
            <div class="auth-bg__feature">
              <el-icon><Goods /></el-icon>
              <span>丰富机型</span>
            </div>
            <div class="auth-bg__feature">
              <el-icon><Timer /></el-icon>
              <span>灵活租期</span>
            </div>
            <div class="auth-bg__feature">
              <el-icon><Service /></el-icon>
              <span>专业服务</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock, Setting, Goods, Timer, Service } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authStore.userLogin(form)
      ElMessage.success('登录成功')

      // 跳转到之前的页面或首页
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (error) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 24px;
}

.auth-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid #e2e8f0;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
}

.auth-card {
  flex: 1;
  padding: 48px;
  display: flex;
  flex-direction: column;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  color: #3b82f6;

  svg {
    width: 100%;
    height: 100%;
  }
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px;
}

.auth-subtitle {
  font-size: 15px;
  color: #94a3b8;
  margin: 0;
}

.auth-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper) {
    padding: 4px 16px;
  }
}

.auth-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  :deep(.el-checkbox__label) {
    color: #64748b;
  }
}

.auth-link {
  color: #3b82f6;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;

  &:hover {
    color: #60a5fa;
  }
}

.auth-submit {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #94a3b8;

  .auth-link {
    margin-left: 8px;
  }
}

.auth-divider {
  display: flex;
  align-items: center;
  margin: 24px 0;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: rgba(148, 163, 184, 0.2);
  }

  span {
    padding: 0 16px;
    font-size: 13px;
    color: #94a3b8;
  }
}

.auth-admin-entry {
  text-align: center;
}

.admin-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.3s;

  &:hover {
    color: #0f172a;
    background: #f1f5f9;
  }
}

.auth-bg {
  width: 420px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -50%;
    left: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, transparent 70%);
  }

  &__content {
    position: relative;
    z-index: 1;
    padding: 48px;
    text-align: center;

    h2 {
      font-size: 24px;
      font-weight: 700;
      color: #f8fafc;
      margin: 0 0 16px;
    }

    p {
      font-size: 15px;
      color: #94a3b8;
      line-height: 1.6;
      margin: 0 0 32px;
    }
  }

  &__features {
    display: flex;
    justify-content: center;
    gap: 24px;
  }

  &__feature {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;

    .el-icon {
      font-size: 28px;
      color: #3b82f6;
    }

    span {
      font-size: 13px;
      color: #94a3b8;
    }
  }
}

@media (max-width: 900px) {
  .auth-bg {
    display: none;
  }

  .auth-card {
    padding: 32px;
  }
}

@media (max-width: 480px) {
  .auth-page {
    padding: 16px;
  }

  .auth-container {
    border-radius: 20px;
  }

  .auth-card {
    padding: 24px;
  }

  .auth-title {
    font-size: 24px;
  }
}
</style>
