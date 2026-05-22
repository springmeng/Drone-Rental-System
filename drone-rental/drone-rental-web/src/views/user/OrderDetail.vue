<template>
  <div class="order-detail-page" v-loading="loading">
    <PageHeader
      title="订单详情"
      :breadcrumbs="breadcrumbs"
    />

    <div v-if="order" class="detail-content">
      <div class="detail-main">
        <!-- 订单状态 -->
        <GlassCard class="status-card">
          <div class="status-header">
            <div class="status-icon" :class="`status-icon--${getStatusType(order.orderStatus)}`">
              <el-icon :size="32"><component :is="getStatusIcon(order.orderStatus)" /></el-icon>
            </div>
            <div class="status-info">
              <h3 class="status-text">{{ getStatusText(order.orderStatus) }}</h3>
              <p class="status-desc">{{ getStatusDesc(order.orderStatus) }}</p>
            </div>
          </div>
          <TimelineProgress :items="timelineItems" />
        </GlassCard>

        <!-- 设备信息 -->
        <GlassCard title="设备信息" class="drone-card">
          <div class="drone-info">
            <img :src="getImageUrl(order.droneImage) || defaultImage" :alt="order.droneModel" class="drone-image" />
            <div class="drone-detail">
              <h4 class="drone-model">{{ order.droneModel }}</h4>
              <div class="drone-specs">
                <span>租赁周期：{{ formatDate(order.rentalStartTime) }} 至 {{ formatDate(order.rentalEndTime) }}</span>
                <span>共 {{ order.rentalDays }} 天</span>
              </div>
            </div>
            <div class="drone-price">
              <span class="price-label">单价</span>
              <span class="price-value">¥{{ order.unitPrice }}/天</span>
            </div>
          </div>
        </GlassCard>

        <!-- 费用明细 -->
        <GlassCard title="费用明细" class="price-card">
          <div class="price-list">
            <div class="price-item">
              <span>租赁费用 ({{ order.unitPrice }}元/天 × {{ order.rentalDays }}天)</span>
              <span>¥{{ (order.unitPrice * order.rentalDays).toFixed(2) }}</span>
            </div>
            <div class="price-item">
              <span>押金</span>
              <span>¥{{ order.depositAmount }}</span>
            </div>
          </div>
          <el-divider />
          <div class="price-total">
            <span>订单总额</span>
            <span class="total-value">¥{{ order.totalAmount }}</span>
          </div>
        </GlassCard>

        <!-- 报修记录 -->
        <GlassCard v-if="faultList.length > 0" title="报修记录" class="fault-card">
          <div class="fault-list">
            <div v-for="fault in faultList" :key="fault.id" class="fault-item">
              <div class="fault-header">
                <span class="fault-type">{{ getFaultTypeText(fault.faultType) }}</span>
                <el-tag :type="getFaultStatusType(fault.auditStatus)" size="small">
                  {{ getFaultStatusText(fault.auditStatus) }}
                </el-tag>
              </div>
              <p class="fault-desc">{{ fault.faultDescription }}</p>
              <div class="fault-footer">
                <span class="fault-no">单号：{{ fault.reportNo }}</span>
                <span class="fault-time">{{ formatDateTime(fault.createdTime) }}</span>
              </div>
            </div>
          </div>
        </GlassCard>
      </div>

      <div class="detail-sidebar">
        <!-- 订单信息 -->
        <GlassCard title="订单信息">
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">订单编号</span>
              <span class="info-value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ formatDateTime(order.createdTime) }}</span>
            </div>
            <div v-if="order.payTime" class="info-item">
              <span class="info-label">支付时间</span>
              <span class="info-value">{{ order.payTime }}</span>
            </div>
            <div v-if="order.paymentMethod" class="info-item">
              <span class="info-label">支付方式</span>
              <span class="info-value">{{ getPaymentMethodText(order.paymentMethod) }}</span>
            </div>
            <div v-if="order.deliveryAddress" class="info-item">
              <span class="info-label">收货地址</span>
              <span class="info-value">{{ order.deliveryAddress }}</span>
            </div>
          </div>
        </GlassCard>

        <!-- 收货信息 -->
        <GlassCard v-if="order.address" title="收货信息">
          <div class="address-info">
            <p class="address-user">
              <span>{{ order.receiverName }}</span>
              <span>{{ order.receiverPhone }}</span>
            </p>
            <p class="address-detail">{{ order.address }}</p>
          </div>
        </GlassCard>

        <!-- 操作按钮 -->
        <GlassCard class="action-card">
          <div class="action-buttons">
            <!-- 待支付 (status=0) -->
            <template v-if="order.orderStatus === 0">
              <el-button type="primary" size="large" @click="goToPay">立即支付</el-button>
              <el-button size="large" @click="handleCancel">取消订单</el-button>
            </template>

            <!-- 待发货 (status=1) -->
            <template v-else-if="order.orderStatus === 1">
              <el-button disabled size="large">等待商家发货</el-button>
            </template>

            <!-- 待收货 (status=2) -->
            <template v-else-if="order.orderStatus === 2">
              <el-button type="primary" size="large" @click="handleReceive">确认收货</el-button>
            </template>

            <!-- 租赁中 (status=3) -->
            <template v-else-if="order.orderStatus === 3">
              <el-button type="warning" size="large" @click="handleReturn">申请退租</el-button>
              <el-button size="large" @click="goToFaultReport">故障报修</el-button>
            </template>

            <!-- 已归还 (status=4) 显示评价按钮 -->
            <template v-else-if="order.orderStatus === 4">
              <el-button v-if="!order.hasComment" type="primary" size="large" @click="openCommentDialog">去评价</el-button>
              <el-button v-else disabled size="large">已评价</el-button>
            </template>

            <!-- 已取消 (status=5) / 已退款 (status=6) -->
            <template v-else-if="order.orderStatus >= 5">
              <el-button size="large" @click="router.push('/orders')">查看其他订单</el-button>
            </template>

            <el-button size="large" @click="router.push('/orders')">返回订单列表</el-button>
          </div>
        </GlassCard>
      </div>
    </div>
    <!-- 评价弹窗 -->
    <el-dialog v-model="commentDialogVisible" title="评价订单" width="500px" :close-on-click-modal="false">
      <el-form ref="commentFormRef" :model="commentForm" :rules="commentRules" label-position="top">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="commentForm.rating" :colors="['#F59E0B', '#F59E0B', '#22C55E']" show-text :texts="['很差', '较差', '一般', '满意', '非常满意']" />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitComment">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Clock,
  CreditCard,
  Van,
  Box,
  Timer,
  Select,
  CircleClose
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import TimelineProgress from '@/components/common/TimelineProgress.vue'
import { getOrderDetail, cancelOrder, confirmReceive, applyReturn, commentOrder } from '@/api/order'
import { getFaultsByOrderId } from '@/api/maintenance'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const order = ref(null)
const faultList = ref([])
const commentDialogVisible = ref(false)
const commentFormRef = ref(null)
const defaultImage = 'https://via.placeholder.com/160x100/1e293b/64748b?text=Drone'

const commentForm = reactive({
  rating: 5,
  content: ''
})

const commentRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字', trigger: 'blur' }
  ]
}

const breadcrumbs = [
  { title: '首页', path: '/' },
  { title: '我的订单', path: '/orders' },
  { title: '订单详情' }
]

// 时间线
const timelineItems = computed(() => {
  if (!order.value) return []

  // 取消订单显示不同的时间线
  if (order.value.orderStatus === 5) {
    return [
      { title: '提交订单', time: order.value.createdTime, completed: true },
      { title: '订单取消', time: order.value.updatedTime, completed: true, active: true }
    ]
  }

  // 退款订单
  if (order.value.orderStatus === 6) {
    return [
      { title: '提交订单', time: order.value.createdTime, completed: true },
      { title: '支付成功', time: order.value.payTime, completed: true },
      { title: '已退款', time: order.value.updatedTime, completed: true, active: true }
    ]
  }

  const items = [
    { title: '提交订单', time: order.value.createdTime, completed: true },
    { title: '支付成功', time: order.value.payTime, completed: order.value.orderStatus >= 1 },
    { title: '商家发货', time: order.value.shipTime, completed: order.value.orderStatus >= 2 },
    { title: '确认收货', time: order.value.receiveTime, completed: order.value.orderStatus >= 3 },
    { title: '设备归还', time: order.value.returnTime, completed: order.value.orderStatus >= 4 }
  ]

  // 找到当前活跃的步骤
  const activeIndex = items.findIndex(item => !item.completed)
  if (activeIndex >= 0) {
    items[activeIndex].active = true
  }

  return items
})

const getStatusText = (status) => {
  const map = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    3: '租赁中',
    4: '已归还',
    5: '已取消',
    6: '已退款'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'info',
    2: 'info',
    3: 'primary',
    4: 'success',
    5: 'default',
    6: 'default'
  }
  return map[status] || 'default'
}

const getStatusDesc = (status) => {
  const map = {
    0: '请尽快完成支付，订单将在30分钟后自动取消',
    1: '商家正在准备发货，请耐心等待',
    2: '设备已发出，请注意查收并确认收货',
    3: '设备使用中，如有问题请及时联系客服',
    4: '设备已归还，订单完成',
    5: '订单已取消',
    6: '订单已退款'
  }
  return map[status] || ''
}

const getStatusIcon = (status) => {
  const map = {
    0: markRaw(Clock),
    1: markRaw(CreditCard),
    2: markRaw(Van),
    3: markRaw(Timer),
    4: markRaw(Select),
    5: markRaw(CircleClose),
    6: markRaw(CircleClose)
  }
  return map[status] || Clock
}

const getPaymentMethodText = (method) => {
  const map = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'balance': '余额支付'
  }
  return map[method] || method
}

const getFaultTypeText = (type) => {
  const map = {
    flight: '飞行异常',
    transmission: '图传问题',
    battery: '电池故障',
    gimbal: '云台问题',
    remote: '遥控器故障',
    other: '其他问题'
  }
  return map[type] || type || '-'
}

const getFaultStatusText = (status) => {
  const map = { 0: '待审核', 1: '已确认', 2: '未通过' }
  return map[status] ?? '未知'
}

const getFaultStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info' }
  return map[status] || 'default'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 19)
}

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
    // 获取报修记录
    fetchFaultList()
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

// 获取报修记录
const fetchFaultList = async () => {
  try {
    const res = await getFaultsByOrderId(route.params.id)
    faultList.value = res.data || []
  } catch (error) {
    console.error('获取报修记录失败:', error)
  }
}

const goToPay = () => {
  router.push(`/orders/${order.value.id}/pay`)
}

const goToFaultReport = () => {
  router.push({
    path: '/fault-report',
    query: { orderId: order.value.id, droneId: order.value.droneId }
  })
}

const handleCancel = async () => {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(async ({ value }) => {
      await cancelOrder(order.value.id, value)
      ElMessage.success('订单已取消')
      fetchOrderDetail()
    })
  } catch {}
}

const handleReceive = async () => {
  try {
    await ElMessageBox.confirm('确认已收到设备？', '确认收货')
    await confirmReceive(order.value.id)
    ElMessage.success('已确认收货')
    fetchOrderDetail()
  } catch {}
}

const handleReturn = async () => {
  try {
    await ElMessageBox.confirm('确认申请退租？设备需要寄回，押金将在确认无损后退还。', '申请退租')
    await applyReturn(order.value.id)
    ElMessage.success('已申请退租，请尽快寄回设备')
    fetchOrderDetail()
  } catch {}
}

const openCommentDialog = () => {
  commentForm.rating = 5
  commentForm.content = ''
  commentDialogVisible.value = true
}

const submitComment = async () => {
  if (!commentFormRef.value) return

  await commentFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await commentOrder(order.value.id, {
        ...commentForm,
        droneId: order.value.droneId
      })
      ElMessage.success('评价成功')
      commentDialogVisible.value = false
      fetchOrderDetail()
    } catch (error) {
      // 错误已处理
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style lang="scss" scoped>
.order-detail-page {
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

// 状态卡片
.status-card {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.06) 0%, rgba(241, 245, 249, 0.8) 100%);
}

.status-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &--warning {
    background: rgba(245, 158, 11, 0.2);
    color: #f59e0b;
  }

  &--info {
    background: rgba(14, 165, 233, 0.2);
    color: #0ea5e9;
  }

  &--primary {
    background: rgba(59, 130, 246, 0.2);
    color: #3b82f6;
  }

  &--success {
    background: rgba(34, 197, 94, 0.2);
    color: #22c55e;
  }

  &--default {
    background: rgba(100, 116, 139, 0.2);
    color: #64748b;
  }
}

.status-text {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px;
}

.status-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

// 设备信息
.drone-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.drone-image {
  width: 160px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
  background: #f5f7fa;
}

.drone-detail {
  flex: 1;
}

.drone-model {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px;
}

.drone-brand {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 8px;
}

.drone-specs {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #64748b;
}

.drone-price {
  text-align: right;
}

.price-label {
  display: block;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.price-value {
  font-size: 18px;
  font-weight: 600;
  color: #3b82f6;
}

// 费用明细
.price-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  color: #64748b;

  &--discount {
    color: #22c55e;
  }
}

.price-total {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.total-value {
  font-size: 24px;
  color: #3b82f6;
}

// 订单信息
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.info-label {
  color: #64748b;
}

.info-value {
  color: #0f172a;
}

// 地址信息
.address-user {
  display: flex;
  gap: 16px;
  font-size: 15px;
  color: #0f172a;
  margin: 0 0 8px;
}

.address-detail {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

// 操作按钮
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .el-button {
    width: 100%;
  }
}

// 报修记录
.fault-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.fault-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.fault-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.fault-type {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.fault-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.fault-footer {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #64748b;
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

  .drone-info {
    flex-wrap: wrap;
  }

  .drone-price {
    width: 100%;
    text-align: left;
    padding-top: 16px;
    border-top: 1px solid #e2e8f0;
  }
}
</style>
