<template>
  <div class="drone-detail-page" v-loading="loading">
    <PageHeader
      :title="drone?.model || '设备详情'"
      :breadcrumbs="breadcrumbs"
    />

    <div v-if="drone" class="detail-content">
      <div class="detail-main">
        <!-- 图片展示 -->
        <GlassCard class="image-card">
          <div class="main-image">
            <img :src="currentImage" :alt="drone.model" />
            <StatusTag
              :text="statusText"
              :type="statusType"
              size="large"
              class="status-badge"
            />
          </div>
          <div v-if="imageList.length > 1" class="image-thumbs">
            <div
              v-for="(img, index) in imageList"
              :key="index"
              class="image-thumb"
              :class="{ 'image-thumb--active': currentImageIndex === index }"
              @click="currentImageIndex = index"
            >
              <img :src="img" :alt="`${drone.model}-${index + 1}`" />
            </div>
          </div>
        </GlassCard>

        <!-- 参数规格 -->
        <GlassCard title="参数规格" class="specs-card">
          <div class="specs-grid">
            <div class="spec-item">
              <span class="spec-label">品牌</span>
              <span class="spec-value">{{ drone.brand }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">型号</span>
              <span class="spec-value">{{ drone.model }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">类型</span>
              <span class="spec-value">{{ drone.type }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">飞行时间</span>
              <span class="spec-value">{{ drone.flightTime }} 分钟</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">最大速度</span>
              <span class="spec-value">{{ drone.maxSpeed }} km/h</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">最大载重</span>
              <span class="spec-value">{{ drone.maxPayload }} kg</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">遥控距离</span>
              <span class="spec-value">{{ drone.maxRange }} m</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">重量</span>
              <span class="spec-value">{{ drone.weight }} g</span>
            </div>
          </div>
        </GlassCard>

        <!-- 设备描述 -->
        <GlassCard title="设备描述" class="desc-card">
          <p class="drone-desc">{{ drone.description || '暂无描述信息' }}</p>
        </GlassCard>

        <!-- 评价列表 -->
        <GlassCard title="用户评价" class="comments-card">
          <div v-if="comments.length > 0" class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <el-avatar :size="40" :src="comment.userAvatar">
                  {{ comment.userNickname?.charAt(0) }}
                </el-avatar>
                <div class="comment-info">
                  <span class="comment-user">{{ comment.userNickname }}</span>
                  <el-rate v-model="comment.rating" disabled size="small" />
                </div>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div v-if="comment.reply" class="comment-reply">
                <span class="reply-label">商家回复：</span>
                {{ comment.reply }}
              </div>
            </div>
          </div>
          <EmptyState v-else title="暂无评价" description="成为第一个评价的用户" />
        </GlassCard>
      </div>

      <!-- 侧边租赁信息 -->
      <div class="detail-sidebar">
        <GlassCard class="rent-card">
          <div class="price-section">
            <div class="price-main">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ drone.pricePerDay }}</span>
              <span class="price-unit">/天</span>
            </div>
            <div class="price-deposit">
              押金：¥{{ drone.deposit }}
            </div>
          </div>

          <div class="stock-section">
            <span class="stock-label">库存</span>
            <span class="stock-value" :class="{ 'stock-value--low': drone.stock < 3 }">
              {{ drone.stock }} 台
            </span>
          </div>

          <el-divider />

          <div class="rent-form">
            <div class="form-item">
              <label>租赁日期</label>
              <el-date-picker
                v-model="rentDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :disabled-date="disabledDate"
                value-format="YYYY-MM-DD"
                @change="calculatePrice"
              />
            </div>

            <div class="form-item">
              <label>租赁天数</label>
              <div class="rent-days">{{ rentDays }} 天</div>
            </div>

            <div class="price-breakdown">
              <div class="breakdown-item">
                <span>租赁费用</span>
                <span>¥{{ rentPrice }}</span>
              </div>
              <div class="breakdown-item">
                <span>押金</span>
                <span>¥{{ drone.deposit }}</span>
              </div>
              <el-divider />
              <div class="breakdown-total">
                <span>合计</span>
                <span class="total-price">¥{{ totalPrice }}</span>
              </div>
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            :disabled="!canRent"
            class="rent-btn"
            @click="handleRent"
          >
            {{ rentBtnText }}
          </el-button>

          <div v-if="!authStore.isLoggedIn" class="login-tip">
            请先 <router-link to="/login">登录</router-link> 后再租赁
          </div>
          <div v-else-if="!authStore.isVerified" class="login-tip">
            请先完成 <router-link to="/profile">实名认证</router-link>
          </div>
        </GlassCard>

        <!-- 服务保障 -->
        <GlassCard class="service-card">
          <h4 class="service-title">服务保障</h4>
          <div class="service-list">
            <div class="service-item">
              <el-icon><Select /></el-icon>
              <span>正品保证</span>
            </div>
            <div class="service-item">
              <el-icon><Select /></el-icon>
              <span>专业调试</span>
            </div>
            <div class="service-item">
              <el-icon><Select /></el-icon>
              <span>全程保险</span>
            </div>
            <div class="service-item">
              <el-icon><Select /></el-icon>
              <span>售后无忧</span>
            </div>
          </div>
        </GlassCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Select } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useAuthStore } from '@/stores/auth'
import { getDroneDetail } from '@/api/drone'
import { getDroneComments } from '@/api/comment'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const drone = ref(null)
const comments = ref([])
const currentImageIndex = ref(0)
const rentDateRange = ref([])

const breadcrumbs = [
  { title: '首页', path: '/' },
  { title: '设备浏览', path: '/drones' },
  { title: '设备详情' }
]

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/600x400/1e293b/64748b?text=Drone'
  if (url.startsWith('http')) return url
  return `/api${url}`
}

// 图片列表
const imageList = computed(() => {
  if (!drone.value?.image) return ['https://via.placeholder.com/600x400/1e293b/64748b?text=Drone']
  return drone.value.image.split(',').filter(Boolean).map(getImageUrl)
})

const currentImage = computed(() => imageList.value[currentImageIndex.value])

// 状态
const statusText = computed(() => {
  const statusMap = { 0: '缺货', 1: '可租赁', 2: '维护中' }
  return statusMap[drone.value?.status] || '未知'
})

const statusType = computed(() => {
  const typeMap = { 0: 'error', 1: 'success', 2: 'warning' }
  return typeMap[drone.value?.status] || 'default'
})

// 租赁天数
const rentDays = computed(() => {
  if (!rentDateRange.value?.length) return 0
  const [start, end] = rentDateRange.value
  const startDate = new Date(start)
  const endDate = new Date(end)
  return Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1
})

// 租赁费用
const rentPrice = computed(() => {
  return rentDays.value * (drone.value?.pricePerDay || 0)
})

// 总价
const totalPrice = computed(() => {
  return rentPrice.value + (drone.value?.deposit || 0)
})

// 是否可以租赁
const canRent = computed(() => {
  return authStore.isLoggedIn &&
         authStore.isVerified &&
         drone.value?.status === 1 &&
         drone.value?.stock > 0 &&
         rentDays.value > 0
})

const rentBtnText = computed(() => {
  if (!authStore.isLoggedIn) return '请先登录'
  if (!authStore.isVerified) return '请先实名认证'
  if (drone.value?.status !== 1) return '暂不可租'
  if (drone.value?.stock <= 0) return '库存不足'
  if (rentDays.value <= 0) return '请选择租赁日期'
  return '立即租赁'
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const calculatePrice = () => {
  // 价格计算逻辑
}

// 获取设备详情
const fetchDroneDetail = async () => {
  loading.value = true
  try {
    const res = await getDroneDetail(route.params.id)
    drone.value = res.data
  } catch (error) {
    ElMessage.error('获取设备详情失败')
    router.push('/drones')
  } finally {
    loading.value = false
  }
}

// 获取评价
const fetchComments = async () => {
  try {
    const res = await getDroneComments(route.params.id, { page: 1, pageSize: 10 })
    comments.value = res.data?.records || []
  } catch (error) {
    console.error('获取评价失败:', error)
  }
}

// 立即租赁
const handleRent = async () => {
  if (!canRent.value) return

  try {
    const [startDate, endDate] = rentDateRange.value
    const res = await createOrder({
      droneId: drone.value.id,
      startDate,
      endDate
    })

    ElMessage.success('订单创建成功')
    router.push(`/orders/${res.data.id}/pay`)
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

onMounted(async () => {
  // 如果已登录，刷新用户信息以获取最新的认证状态
  if (authStore.isLoggedIn) {
    await authStore.fetchUserInfo()
  }
  fetchDroneDetail()
  fetchComments()
})
</script>

<style lang="scss" scoped>
.drone-detail-page {
  min-height: 100%;
}

.detail-content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.detail-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-sidebar {
  width: 360px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: sticky;
  top: 96px;
}

// 图片展示
.image-card {
  overflow: hidden;
}

.main-image {
  position: relative;
  aspect-ratio: 16 / 10;
  border-radius: 16px;
  overflow: hidden;
  background: #f5f7fa;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .status-badge {
    position: absolute;
    top: 16px;
    right: 16px;
  }
}

.image-thumbs {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.image-thumb {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  opacity: 0.6;
  transition: all 0.3s;

  &:hover {
    opacity: 0.8;
  }

  &--active {
    opacity: 1;
    border-color: #3b82f6;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

// 参数规格
.specs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.spec-label {
  font-size: 14px;
  color: #64748b;
}

.spec-value {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

// 描述
.drone-desc {
  font-size: 15px;
  color: #64748b;
  line-height: 1.8;
  margin: 0;
}

// 评价
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-user {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.comment-time {
  font-size: 13px;
  color: #475569;
}

.comment-content {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 12px;
}

.comment-reply {
  padding: 12px 16px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
  font-size: 14px;
  color: #64748b;

  .reply-label {
    color: #3b82f6;
    font-weight: 500;
  }
}

// 租赁卡片
.rent-card {
  padding: 24px;
}

.price-section {
  text-align: center;
  margin-bottom: 20px;
}

.price-main {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
}

.price-symbol {
  font-size: 20px;
  color: #3b82f6;
}

.price-value {
  font-size: 40px;
  font-weight: 700;
  color: #3b82f6;
}

.price-unit {
  font-size: 16px;
  color: #64748b;
}

.price-deposit {
  font-size: 14px;
  color: #64748b;
  margin-top: 8px;
}

.stock-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.stock-label {
  font-size: 14px;
  color: #64748b;
}

.stock-value {
  font-size: 15px;
  font-weight: 600;
  color: #4ade80;

  &--low {
    color: #f59e0b;
  }
}

.rent-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-size: 13px;
    color: #64748b;
  }

  :deep(.el-date-editor) {
    width: 100% !important;
    max-width: 100%;
  }
}

.rent-days {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.price-breakdown {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.breakdown-total {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.total-price {
  color: #3b82f6;
  font-size: 20px;
}

.rent-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
}

.login-tip {
  text-align: center;
  font-size: 13px;
  color: #64748b;
  margin-top: 12px;

  a {
    color: #3b82f6;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

// 服务保障
.service-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 16px;
}

.service-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #64748b;

  .el-icon {
    color: #22c55e;
  }
}

// 响应式
@media (max-width: 1024px) {
  .detail-content {
    flex-direction: column;
  }

  .detail-sidebar {
    width: 100%;
    position: static;
  }

  .specs-grid {
    grid-template-columns: 1fr;
  }
}
</style>
