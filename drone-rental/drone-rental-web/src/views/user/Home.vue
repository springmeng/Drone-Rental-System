<template>
  <div class="home-page">
    <!-- Hero区域 -->
    <section class="hero">
      <div class="hero__content">
        <h1 class="hero__title">
          <span class="hero__title-highlight">专业无人机</span>
          <br />租赁服务平台
        </h1>
        <p class="hero__description">
          提供各类高品质无人机设备租赁服务，满足航拍、测绘、农业、巡检等多种场景需求
        </p>
        <div class="hero__actions">
          <el-button type="primary" size="large" @click="router.push('/drones')">
            浏览设备
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
          <el-button size="large" plain @click="scrollToFeatures">
            了解更多
          </el-button>
        </div>
      </div>
      <div class="hero__visual">
        <div class="hero__drone-illustration">
          <svg viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="100" cy="100" r="80" stroke="#3b82f6" stroke-width="2" stroke-dasharray="4 4" opacity="0.3"/>
            <circle cx="100" cy="100" r="60" stroke="#3b82f6" stroke-width="2" stroke-dasharray="4 4" opacity="0.5"/>
            <circle cx="100" cy="100" r="40" stroke="#3b82f6" stroke-width="2"/>
            <path d="M100 60L100 140M60 100L140 100" stroke="#3b82f6" stroke-width="2"/>
            <circle cx="100" cy="100" r="15" fill="#3b82f6"/>
            <circle cx="60" cy="60" r="12" stroke="#3b82f6" stroke-width="2" fill="#0f172a"/>
            <circle cx="140" cy="60" r="12" stroke="#3b82f6" stroke-width="2" fill="#0f172a"/>
            <circle cx="60" cy="140" r="12" stroke="#3b82f6" stroke-width="2" fill="#0f172a"/>
            <circle cx="140" cy="140" r="12" stroke="#3b82f6" stroke-width="2" fill="#0f172a"/>
          </svg>
        </div>
      </div>
    </section>

    <!-- 统计数据 -->
    <section class="stats">
      <BentoGrid :cols="4" :gap="24">
        <StatTile
          v-for="stat in stats"
          :key="stat.label"
          :value="stat.value"
          :label="stat.label"
          :icon="stat.icon"
          :trend="stat.trend"
          :color="stat.color"
        />
      </BentoGrid>
    </section>

    <!-- 热门设备 -->
    <section class="popular-drones">
      <div class="section-header">
        <h2 class="section-title">热门设备</h2>
        <router-link to="/drones" class="section-link">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <BentoGrid :cols="4" :gap="24">
        <DroneCard
          v-for="drone in popularDrones"
          :key="drone.id"
          :drone="drone"
          @click="handleDroneClick"
        />
      </BentoGrid>
      <EmptyState
        v-if="popularDrones.length === 0 && !loading"
        title="暂无热门设备"
        description="敬请期待"
      />
    </section>

    <!-- 特色服务 -->
    <section id="features" class="features">
      <h2 class="section-title section-title--center">我们的优势</h2>
      <BentoGrid :cols="3" :gap="24">
        <GlassCard v-for="feature in features" :key="feature.title" class="feature-card">
          <template #header>
            <div class="feature-icon" :style="{ color: feature.color }">
              <el-icon :size="32"><component :is="feature.icon" /></el-icon>
            </div>
          </template>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.description }}</p>
        </GlassCard>
      </BentoGrid>
    </section>

    <!-- 租赁流程 -->
    <section class="process">
      <h2 class="section-title section-title--center">租赁流程</h2>
      <div class="process-steps">
        <div
          v-for="(step, index) in processSteps"
          :key="step.title"
          class="process-step"
        >
          <div class="process-step__num">{{ index + 1 }}</div>
          <div class="process-step__content">
            <h4 class="process-step__title">{{ step.title }}</h4>
            <p class="process-step__desc">{{ step.description }}</p>
          </div>
          <div v-if="index < processSteps.length - 1" class="process-step__arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta">
      <GlassCard class="cta-card">
        <h2 class="cta-title">开始您的无人机租赁之旅</h2>
        <p class="cta-desc">注册账户，完成资质认证，即可享受专业的无人机租赁服务</p>
        <div class="cta-actions">
          <el-button v-if="!authStore.isLoggedIn" type="primary" size="large" @click="router.push('/register')">
            立即注册
          </el-button>
          <el-button v-else type="primary" size="large" @click="router.push('/drones')">
            浏览设备
          </el-button>
        </div>
      </GlassCard>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Goods,
  Timer,
  Service,
  Medal,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import GlassCard from '@/components/common/GlassCard.vue'
import BentoGrid from '@/components/common/BentoGrid.vue'
import StatTile from '@/components/common/StatTile.vue'
import DroneCard from '@/components/business/DroneCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getDroneList } from '@/api/drone'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const popularDrones = ref([])

// 统计数据
const stats = [
  { value: '50+', label: '设备型号', icon: markRaw(Goods), color: 'primary' },
  { value: '1000+', label: '累计订单', icon: markRaw(TrendCharts), color: 'success' },
  { value: '500+', label: '注册用户', icon: markRaw(User), color: 'info' },
  { value: '99%', label: '好评率', icon: markRaw(Medal), color: 'warning' }
]

// 特色服务
const features = [
  {
    icon: markRaw(Goods),
    title: '丰富机型',
    description: '涵盖航拍、测绘、农业、巡检等多个领域的专业无人机',
    color: '#3b82f6'
  },
  {
    icon: markRaw(Timer),
    title: '灵活租期',
    description: '支持按天、按周、按月灵活租赁，满足不同项目需求',
    color: '#22c55e'
  },
  {
    icon: markRaw(Service),
    title: '专业服务',
    description: '提供设备调试、技术支持、售后维保一站式服务',
    color: '#f59e0b'
  }
]

// 租赁流程
const processSteps = [
  { title: '注册认证', description: '完成账户注册和资质认证' },
  { title: '选择设备', description: '浏览设备并选择合适的机型' },
  { title: '下单支付', description: '确认订单信息并完成支付' },
  { title: '收货使用', description: '签收设备开始使用' },
  { title: '归还评价', description: '使用完毕归还并评价' }
]

// 获取热门设备
const fetchPopularDrones = async () => {
  loading.value = true
  try {
    const res = await getDroneList({ page: 1, pageSize: 4, status: 1 })
    popularDrones.value = res.data?.records || []
  } catch (error) {
    console.error('获取热门设备失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDroneClick = (drone) => {
  router.push(`/drones/${drone.id}`)
}

const scrollToFeatures = () => {
  document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  fetchPopularDrones()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100%;
}

// Hero区域
.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 64px;
  padding: 64px 0;

  &__content {
    flex: 1;
    max-width: 600px;
  }

  &__title {
    font-size: 48px;
    font-weight: 800;
    color: #0f172a;
    line-height: 1.2;
    margin: 0 0 24px;
  }

  &__title-highlight {
    color: #3b82f6;
    text-shadow: 0 0 40px rgba(59, 130, 246, 0.5);
  }

  &__description {
    font-size: 18px;
    color: #64748b;
    line-height: 1.7;
    margin: 0 0 32px;
  }

  &__actions {
    display: flex;
    gap: 16px;

    .el-button {
      padding: 12px 32px;
      height: auto;
    }
  }

  &__visual {
    flex-shrink: 0;
  }

  &__drone-illustration {
    width: 300px;
    height: 300px;
    animation: float 6s ease-in-out infinite;

    svg {
      width: 100%;
      height: 100%;
      filter: drop-shadow(0 0 30px rgba(59, 130, 246, 0.3));
    }
  }
}

// 统计区域
.stats {
  margin-bottom: 80px;
}

// 通用section样式
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;

  &--center {
    text-align: center;
    margin-bottom: 40px;
  }
}

.section-link {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  color: #3b82f6;
  text-decoration: none;
  transition: color 0.3s;

  &:hover {
    color: #60a5fa;
  }
}

// 热门设备
.popular-drones {
  margin-bottom: 80px;
}

// 特色服务
.features {
  margin-bottom: 80px;
}

.feature-card {
  text-align: center;
  padding: 40px 32px;
}

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 20px;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 12px;
}

.feature-desc {
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

// 租赁流程
.process {
  margin-bottom: 80px;
}

.process-steps {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
}

.process-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  max-width: 160px;
  position: relative;

  &__num {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 700;
    color: white;
    margin-bottom: 16px;
    box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
  }

  &__content {
    flex: 1;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 8px;
  }

  &__desc {
    font-size: 14px;
    color: #64748b;
    margin: 0;
  }

  &__arrow {
    position: absolute;
    right: -24px;
    top: 18px;
    color: #475569;
    display: none;
  }
}

// CTA
.cta {
  margin-bottom: 40px;
}

.cta-card {
  text-align: center;
  padding: 64px 48px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(241, 245, 249, 0.8) 100%);
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 16px;
}

.cta-desc {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 32px;
}

.cta-actions {
  .el-button {
    padding: 14px 48px;
    height: auto;
    font-size: 16px;
  }
}

// 动画
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

// 响应式
@media (max-width: 1024px) {
  .hero {
    flex-direction: column;
    text-align: center;
    gap: 48px;

    &__content {
      max-width: 100%;
    }

    &__actions {
      justify-content: center;
    }

    &__drone-illustration {
      width: 240px;
      height: 240px;
    }
  }

  .process-step__arrow {
    display: none;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 32px 0;

    &__title {
      font-size: 32px;
    }

    &__description {
      font-size: 16px;
    }
  }

  .section-title {
    font-size: 24px;
  }

  .cta-card {
    padding: 40px 24px;
  }

  .cta-title {
    font-size: 24px;
  }
}
</style>
