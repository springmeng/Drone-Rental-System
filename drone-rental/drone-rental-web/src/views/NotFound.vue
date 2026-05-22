<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面未找到</h1>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
      <div class="error-actions">
        <el-button type="primary" size="large" @click="router.push('/')">
          返回首页
        </el-button>
        <el-button size="large" @click="router.back()">
          返回上一页
        </el-button>
      </div>
    </div>

    <!-- 装饰背景 -->
    <div class="not-found-decoration">
      <div class="drone-icon">
        <svg viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="100" cy="100" r="80" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4" opacity="0.2"/>
          <circle cx="100" cy="100" r="60" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4" opacity="0.3"/>
          <path d="M100 60L100 140M60 100L140 100" stroke="currentColor" stroke-width="2" opacity="0.5"/>
          <circle cx="100" cy="100" r="15" fill="currentColor" opacity="0.3"/>
          <circle cx="60" cy="60" r="12" stroke="currentColor" stroke-width="2" fill="#0f172a"/>
          <circle cx="140" cy="60" r="12" stroke="currentColor" stroke-width="2" fill="#0f172a"/>
          <circle cx="60" cy="140" r="12" stroke="currentColor" stroke-width="2" fill="#0f172a"/>
          <circle cx="140" cy="140" r="12" stroke="currentColor" stroke-width="2" fill="#0f172a"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  position: relative;
  overflow: hidden;
}

.not-found-content {
  text-align: center;
  position: relative;
  z-index: 10;
}

.error-code {
  font-size: 160px;
  font-weight: 800;
  color: transparent;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  background-clip: text;
  line-height: 1;
  margin-bottom: 16px;
  text-shadow: 0 0 60px rgba(59, 130, 246, 0.3);
}

.error-title {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 12px;
}

.error-desc {
  font-size: 16px;
  color: #94a3b8;
  margin: 0 0 32px;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;

  .el-button {
    min-width: 140px;
  }
}

.not-found-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.drone-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  height: 400px;
  color: #3b82f6;
  opacity: 0.1;
  animation: rotate 30s linear infinite;

  svg {
    width: 100%;
    height: 100%;
  }
}

@keyframes rotate {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@media (max-width: 640px) {
  .error-code {
    font-size: 100px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-actions {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }
}
</style>
