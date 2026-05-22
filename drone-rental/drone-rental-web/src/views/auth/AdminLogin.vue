<template>
  <div class="admin-login-page">
    <div class="admin-login-card">
      <div class="admin-login-header">
        <div class="admin-login-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L4 7V17L12 22L20 17V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            <path d="M12 22V12" stroke="currentColor" stroke-width="2"/>
            <path d="M20 7L12 12L4 7" stroke="currentColor" stroke-width="2"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="admin-login-title">管理后台</h1>
        <p class="admin-login-subtitle">无人机租赁系统管理端</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="admin-login-form"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="管理员账号"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            class="admin-login-submit"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="admin-login-footer">
        <router-link to="/login" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          返回用户登录
        </router-link>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="admin-login-decoration">
      <div class="decoration-circle decoration-circle--1"></div>
      <div class="decoration-circle decoration-circle--2"></div>
      <div class="decoration-circle decoration-circle--3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authStore.adminUserLogin(form)
      ElMessage.success('登录成功')
      router.push('/admin')
    } catch (error) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e2e8f0 50%, #f5f7fa 100%);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.admin-login-card {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid #e2e8f0;
  border-radius: 28px;
  padding: 48px;
  position: relative;
  z-index: 10;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
}

.admin-login-header {
  text-align: center;
  margin-bottom: 36px;
}

.admin-login-logo {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  color: #3b82f6;
  filter: drop-shadow(0 0 20px rgba(59, 130, 246, 0.5));

  svg {
    width: 100%;
    height: 100%;
  }
}

.admin-login-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px;
}

.admin-login-subtitle {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

.admin-login-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper) {
    padding: 4px 16px;
  }
}

.admin-login-submit {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.admin-login-footer {
  margin-top: 24px;
  text-align: center;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.3s;

  &:hover {
    color: #3b82f6;
  }
}

// 装饰元素
.admin-login-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);

  &--1 {
    width: 600px;
    height: 600px;
    top: -200px;
    right: -200px;
    animation: float 20s ease-in-out infinite;
  }

  &--2 {
    width: 400px;
    height: 400px;
    bottom: -100px;
    left: -100px;
    animation: float 15s ease-in-out infinite reverse;
  }

  &--3 {
    width: 300px;
    height: 300px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: radial-gradient(circle, rgba(59, 130, 246, 0.08) 0%, transparent 70%);
    animation: pulse 10s ease-in-out infinite;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-30px, 30px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

@media (max-width: 480px) {
  .admin-login-card {
    padding: 32px 24px;
    border-radius: 20px;
  }

  .admin-login-title {
    font-size: 24px;
  }

  .admin-login-logo {
    width: 56px;
    height: 56px;
  }
}
</style>
