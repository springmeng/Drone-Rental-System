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
          <h1 class="auth-title">注册账户</h1>
          <p class="auth-subtitle">创建您的无人机租赁系统账户</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="auth-form"
          size="large"
          @submit.prevent="handleSubmit"
        >
          <el-form-item prop="nickname">
            <el-input
              v-model="form.nickname"
              placeholder="请输入昵称"
              :prefix-icon="User"
            />
          </el-form-item>

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
              placeholder="请输入密码（至少6位）"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="agreement">
            <el-checkbox v-model="form.agreement">
              我已阅读并同意
              <a href="#" class="auth-link" @click.prevent>《用户服务协议》</a>
              和
              <a href="#" class="auth-link" @click.prevent>《隐私政策》</a>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="auth-submit"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <span>已有账户？</span>
          <router-link to="/login" class="auth-link">立即登录</router-link>
        </div>
      </div>

      <div class="auth-bg">
        <div class="auth-bg__content">
          <h2>加入翱翔无人机</h2>
          <p>注册即可享受专业的无人机租赁服务</p>
          <div class="auth-bg__steps">
            <div class="auth-bg__step">
              <div class="auth-bg__step-num">1</div>
              <div class="auth-bg__step-info">
                <h4>注册账户</h4>
                <p>填写基本信息完成注册</p>
              </div>
            </div>
            <div class="auth-bg__step">
              <div class="auth-bg__step-num">2</div>
              <div class="auth-bg__step-info">
                <h4>实名认证</h4>
                <p>提交资质完成认证</p>
              </div>
            </div>
            <div class="auth-bg__step">
              <div class="auth-bg__step-num">3</div>
              <div class="auth-bg__step-info">
                <h4>开始租赁</h4>
                <p>浏览设备下单租赁</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  nickname: '',
  username: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreement = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请阅读并同意服务协议'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authStore.userRegister({
        nickname: form.nickname,
        username: form.username,
        password: form.password
      })
      ElMessage.success('注册成功')
      router.push('/')
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

  &__content {
    position: relative;
    z-index: 1;
    padding: 48px;

    h2 {
      font-size: 24px;
      font-weight: 700;
      color: #f8fafc;
      margin: 0 0 12px;
      text-align: center;
    }

    > p {
      font-size: 15px;
      color: #94a3b8;
      text-align: center;
      margin: 0 0 40px;
    }
  }

  &__steps {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  &__step {
    display: flex;
    align-items: flex-start;
    gap: 16px;
  }

  &__step-num {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: rgba(59, 130, 246, 0.2);
    border: 2px solid #3b82f6;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 700;
    color: #3b82f6;
    flex-shrink: 0;
  }

  &__step-info {
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #f8fafc;
      margin: 0 0 4px;
    }

    p {
      font-size: 14px;
      color: #64748b;
      margin: 0;
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
