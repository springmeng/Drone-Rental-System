<template>
  <div class="order-pay-page" v-loading="loading">
    <PageHeader title="订单支付" :breadcrumbs="breadcrumbs" />

    <div v-if="order" class="pay-content">
      <GlassCard class="order-summary">
        <div class="summary-header">
          <h3>订单信息</h3>
          <span class="order-no">订单号：{{ order.orderNo }}</span>
        </div>

        <div class="drone-info">
          <img :src="getImageUrl(order.droneImage) || defaultImage" :alt="order.droneModel" />
          <div class="drone-detail">
            <h4>{{ order.droneModel }}</h4>
            <p class="rent-period">
              {{ formatDate(order.rentalStartTime) }} 至 {{ formatDate(order.rentalEndTime) }}（{{ order.rentalDays }}天）
            </p>
          </div>
        </div>

        <el-divider />

        <div class="price-breakdown">
          <div class="breakdown-item">
            <span>租赁费用 ({{ order.unitPrice }}元/天 × {{ order.rentalDays }}天)</span>
            <span>¥{{ (order.unitPrice * order.rentalDays).toFixed(2) }}</span>
          </div>
          <div class="breakdown-item">
            <span>押金</span>
            <span>¥{{ order.depositAmount }}</span>
          </div>
        </div>

        <div class="total-amount">
          <span>应付金额</span>
          <span class="amount">¥{{ order.totalAmount }}</span>
        </div>
      </GlassCard>

      <GlassCard class="delivery-address-card">
        <h3>收货地址</h3>
        <el-input
          v-model="deliveryAddress"
          type="textarea"
          :rows="2"
          placeholder="请输入收货地址（收件人、联系电话、详细地址）"
          maxlength="200"
          show-word-limit
        />
      </GlassCard>

      <GlassCard class="payment-methods">
        <h3>选择支付方式</h3>

        <div class="method-list">
          <div
            v-for="method in paymentMethods"
            :key="method.value"
            class="method-item"
            :class="{ 'method-item--active': selectedMethod === method.value }"
            @click="selectedMethod = method.value"
          >
            <div class="method-icon" :style="{ background: method.bgColor }">
              <el-icon :size="24" :color="method.color"><component :is="method.icon" /></el-icon>
            </div>
            <div class="method-info">
              <span class="method-name">{{ method.name }}</span>
              <span class="method-desc">{{ method.desc }}</span>
            </div>
            <div class="method-check">
              <el-icon v-if="selectedMethod === method.value" :size="20"><Select /></el-icon>
            </div>
          </div>
        </div>

        <div class="pay-action">
          <div class="countdown" v-if="countdown > 0">
            请在 <span>{{ formatCountdown }}</span> 内完成支付
          </div>
          <el-button
            type="primary"
            size="large"
            :loading="paying"
            :disabled="!selectedMethod"
            @click="handlePay"
          >
            确认支付 ¥{{ order.totalAmount }}
          </el-button>
        </div>
      </GlassCard>

      <!-- 支付结果弹窗 -->
      <el-dialog
        v-model="resultDialogVisible"
        :show-close="false"
        :close-on-click-modal="false"
        width="400px"
        class="pay-result-dialog"
      >
        <div class="result-content">
          <div class="result-icon result-icon--success" v-if="paySuccess">
            <el-icon :size="64"><Select /></el-icon>
          </div>
          <div class="result-icon result-icon--error" v-else>
            <el-icon :size="64"><CloseBold /></el-icon>
          </div>
          <h3>{{ paySuccess ? '支付成功' : '支付失败' }}</h3>
          <p>{{ paySuccess ? '订单支付成功，商家将尽快发货' : '支付遇到问题，请重试' }}</p>
        </div>
        <template #footer>
          <el-button v-if="paySuccess" type="primary" @click="goToOrderDetail">
            查看订单
          </el-button>
          <el-button v-else @click="resultDialogVisible = false">重试</el-button>
          <el-button @click="router.push('/orders')">返回订单列表</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Select, CloseBold, Wallet, CreditCard, Money } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import { getOrderDetail, payOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const paying = ref(false)
const order = ref(null)
const selectedMethod = ref('alipay')
const deliveryAddress = ref('')
const countdown = ref(30 * 60) // 30分钟
const resultDialogVisible = ref(false)
const paySuccess = ref(false)
const countdownTimer = ref(null)

const defaultImage = 'https://via.placeholder.com/100x70/1e293b/64748b?text=Drone'

const breadcrumbs = [
  { title: '首页', path: '/' },
  { title: '我的订单', path: '/orders' },
  { title: '订单支付' }
]

const paymentMethods = [
  {
    value: 'alipay',
    name: '支付宝',
    desc: '推荐支付宝用户使用',
    icon: markRaw(Wallet),
    color: '#1677FF',
    bgColor: 'rgba(22, 119, 255, 0.1)'
  },
  {
    value: 'wechat',
    name: '微信支付',
    desc: '推荐微信用户使用',
    icon: markRaw(CreditCard),
    color: '#07C160',
    bgColor: 'rgba(7, 193, 96, 0.1)'
  },
  {
    value: 'balance',
    name: '余额支付',
    desc: '使用账户余额支付',
    icon: markRaw(Money),
    color: '#F59E0B',
    bgColor: 'rgba(245, 158, 11, 0.1)'
  }
]

const formatCountdown = computed(() => {
  const minutes = Math.floor(countdown.value / 60)
  const seconds = countdown.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

// 格式化日期
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.substring(0, 10)
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `/api${url}`
}

// 获取订单详情
const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data

    // 如果订单不是待支付状态，跳转到详情页
    if (order.value.orderStatus !== 0) {
      ElMessage.warning('订单状态已变更')
      router.push(`/orders/${order.value.id}`)
    }
  } catch (error) {
    ElMessage.error('获取订单信息失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

// 开始倒计时
const startCountdown = () => {
  countdownTimer.value = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      clearInterval(countdownTimer.value)
      ElMessage.warning('订单已超时取消')
      router.push('/orders')
    }
  }, 1000)
}

// 支付
const handlePay = async () => {
  if (!deliveryAddress.value.trim()) {
    ElMessage.warning('请填写收货地址')
    return
  }
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  paying.value = true
  try {
    // 模拟支付（实际项目中会调用支付接口）
    await payOrder(order.value.id, selectedMethod.value, deliveryAddress.value.trim())

    paySuccess.value = true
    resultDialogVisible.value = true
  } catch (error) {
    paySuccess.value = false
    resultDialogVisible.value = true
  } finally {
    paying.value = false
  }
}

const goToOrderDetail = () => {
  router.push(`/orders/${order.value.id}`)
}

onMounted(() => {
  fetchOrderDetail()
  startCountdown()
})

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style lang="scss" scoped>
.order-pay-page {
  min-height: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.pay-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// 订单摘要
.order-summary {
  .summary-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #0f172a;
      margin: 0;
    }

    .order-no {
      font-size: 14px;
      color: #64748b;
    }
  }
}

.drone-info {
  display: flex;
  gap: 16px;

  img {
    width: 100px;
    height: 70px;
    object-fit: cover;
    border-radius: 10px;
    background: #f5f7fa;
  }

  .drone-detail {
    flex: 1;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #0f172a;
      margin: 0 0 4px;
    }

    p {
      font-size: 14px;
      color: #64748b;
      margin: 0;
    }

    .rent-period {
      color: #64748b;
      margin-top: 8px;
    }
  }
}

.price-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #64748b;
}

.total-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  font-size: 16px;
  color: #0f172a;

  .amount {
    font-size: 28px;
    font-weight: 700;
    color: #3b82f6;
  }
}

// 收货地址
.delivery-address-card {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 16px;
  }
}

// 支付方式
.payment-methods {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 20px;
  }
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-radius: 16px;
  background: #f8fafc;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.95);
  }

  &--active {
    border-color: #3b82f6;
    background: rgba(59, 130, 246, 0.1);
  }
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.method-info {
  flex: 1;
}

.method-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 13px;
  color: #64748b;
}

.method-check {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.pay-action {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;

  .countdown {
    font-size: 14px;
    color: #64748b;

    span {
      color: #f59e0b;
      font-weight: 600;
    }
  }

  .el-button {
    width: 100%;
    height: 52px;
    font-size: 18px;
    font-weight: 600;
  }
}

// 支付结果弹窗
.result-content {
  text-align: center;
  padding: 32px 0;

  h3 {
    font-size: 24px;
    font-weight: 700;
    color: #0f172a;
    margin: 20px 0 12px;
  }

  p {
    font-size: 15px;
    color: #64748b;
    margin: 0;
  }
}

.result-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;

  &--success {
    background: rgba(34, 197, 94, 0.2);
    color: #22c55e;
  }

  &--error {
    background: rgba(239, 68, 68, 0.2);
    color: #ef4444;
  }
}

:deep(.pay-result-dialog) {
  .el-dialog__footer {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}
</style>
