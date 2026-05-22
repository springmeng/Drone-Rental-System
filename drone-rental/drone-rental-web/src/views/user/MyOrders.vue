<template>
  <div class="my-orders-page">
    <PageHeader title="我的订单" subtitle="管理您的所有租赁订单" />

    <!-- 订单状态标签 -->
    <div class="order-tabs">
      <div
        v-for="tab in orderTabs"
        :key="tab.value"
        class="order-tab"
        :class="{ 'order-tab--active': activeTab === tab.value }"
        @click="handleTabChange(tab.value)"
      >
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-loading="loading" class="order-list">
      <GlassCard
        v-for="order in orderList"
        :key="order.id"
        class="order-card"
      >
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatDateTime(order.createdTime) }}</span>
          </div>
          <StatusTag :text="getStatusText(order.orderStatus)" :type="getStatusType(order.orderStatus)" />
        </div>

        <div class="order-content" @click="goToDetail(order.id)">
          <div class="drone-info">
            <img :src="getImageUrl(order.droneImage) || defaultImage" :alt="order.droneModel" class="drone-image" />
            <div class="drone-detail">
              <h4 class="drone-model">{{ order.droneModel }}</h4>
              <p class="rent-period">
                租赁周期：{{ formatDate(order.rentalStartTime) }} 至 {{ formatDate(order.rentalEndTime) }}
                <span class="rent-days">（共{{ order.rentalDays }}天）</span>
              </p>
            </div>
          </div>
          <div class="order-price">
            <span class="price-label">订单金额</span>
            <span class="price-value">¥{{ order.totalAmount }}</span>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-actions">
            <!-- 待支付 (status=0) -->
            <template v-if="order.orderStatus === 0">
              <el-button type="primary" @click="goToPay(order.id)">去支付</el-button>
              <el-button @click="handleCancel(order)">取消订单</el-button>
            </template>

            <!-- 待发货 (status=1) -->
            <template v-else-if="order.orderStatus === 1">
              <el-button disabled>等待发货</el-button>
            </template>

            <!-- 待收货 (status=2) -->
            <template v-else-if="order.orderStatus === 2">
              <el-button type="primary" @click="handleReceive(order)">确认收货</el-button>
            </template>

            <!-- 租赁中 (status=3) -->
            <template v-else-if="order.orderStatus === 3">
              <el-button type="warning" @click="handleReturn(order)">申请退租</el-button>
              <el-button @click="goToFaultReport(order)">故障报修</el-button>
            </template>

            <!-- 已归还 (status=4) -->
            <template v-else-if="order.orderStatus === 4">
              <el-button v-if="!order.hasComment" type="primary" @click="handleComment(order)">去评价</el-button>
              <el-button @click="goToDetail(order.id)">查看详情</el-button>
            </template>

            <!-- 已取消 (status=5) -->
            <template v-else-if="order.orderStatus === 5">
              <el-button @click="goToDetail(order.id)">查看详情</el-button>
            </template>

            <!-- 已退款 (status=6) -->
            <template v-else-if="order.orderStatus === 6">
              <el-button @click="goToDetail(order.id)">查看详情</el-button>
            </template>
          </div>
        </div>
      </GlassCard>

      <EmptyState
        v-if="orderList.length === 0 && !loading"
        title="暂无订单"
        description="快去浏览设备下单吧"
        :icon="Document"
      >
        <template #action>
          <el-button type="primary" @click="router.push('/drones')">浏览设备</el-button>
        </template>
      </EmptyState>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30]"
        layout="total, prev, pager, next"
        background
        @current-change="fetchOrders"
      />
    </div>

    <!-- 评价弹窗 -->
    <el-dialog
      v-model="commentDialogVisible"
      title="评价订单"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="commentFormRef" :model="commentForm" :rules="commentRules" label-position="top">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="commentForm.rating" :colors="['#F59E0B', '#F59E0B', '#22C55E']" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getMyOrders, cancelOrder, confirmReceive, applyReturn, commentOrder } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const orderList = ref([])
const total = ref(0)
const activeTab = ref('')
const commentDialogVisible = ref(false)
const currentOrder = ref(null)

const defaultImage = 'https://via.placeholder.com/120x80/1e293b/64748b?text=Drone'

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const orderTabs = ref([
  { label: '全部', value: '', count: 0 },
  { label: '待支付', value: '0', count: 0 },
  { label: '待发货', value: '1', count: 0 },
  { label: '待收货', value: '2', count: 0 },
  { label: '租赁中', value: '3', count: 0 },
  { label: '已归还', value: '4', count: 0 },
  { label: '已取消', value: '5', count: 0 }
])

const commentFormRef = ref(null)
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

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (activeTab.value !== '') {
      params.status = activeTab.value
    }

    const res = await getMyOrders(params)
    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = (value) => {
  activeTab.value = value
  pagination.page = 1
  fetchOrders()
}

const goToDetail = (id) => {
  router.push(`/orders/${id}`)
}

const goToPay = (id) => {
  router.push(`/orders/${id}/pay`)
}

const goToFaultReport = (order) => {
  router.push({
    path: '/fault-report',
    query: { orderId: order.id, droneId: order.droneId }
  })
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    }).then(async ({ value }) => {
      await cancelOrder(order.id, value)
      ElMessage.success('订单已取消')
      fetchOrders()
    })
  } catch {
    // 取消操作
  }
}

// 确认收货
const handleReceive = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到设备？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await confirmReceive(order.id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch {
    // 取消操作
  }
}

// 申请退租
const handleReturn = async (order) => {
  try {
    await ElMessageBox.confirm('确认申请退租？设备需要寄回，押金将在确认无损后退还。', '申请退租', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await applyReturn(order.id, {})
    ElMessage.success('已申请退租，请尽快寄回设备')
    fetchOrders()
  } catch {
    // 取消操作
  }
}

// 评价
const handleComment = (order) => {
  currentOrder.value = order
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
      await commentOrder(currentOrder.value.id, {
        ...commentForm,
        droneId: currentOrder.value.droneId
      })
      ElMessage.success('评价成功')
      commentDialogVisible.value = false
      fetchOrders()
    } catch (error) {
      // 错误已处理
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.my-orders-page {
  min-height: 100%;
}

// 订单状态标签
.order-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  overflow-x: auto;
}

.order-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;

  &:hover {
    color: #0f172a;
    background: #e2e8f0;
  }

  &--active {
    color: #3b82f6;
    background: rgba(59, 130, 246, 0.15);
  }
}

.tab-count {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

// 订单列表
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 400px;
}

.order-card {
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 16px;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.order-no {
  font-size: 14px;
  color: #64748b;
}

.order-time {
  font-size: 13px;
  color: #64748b;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 16px;
  margin: -16px;
  margin-bottom: 0;
  border-radius: 12px;
  transition: background 0.3s;

  &:hover {
    background: rgba(148, 163, 184, 0.05);
  }
}

.drone-info {
  display: flex;
  gap: 16px;
}

.drone-image {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 12px;
  background: #f5f7fa;
}

.drone-detail {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.drone-model {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.drone-brand {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.rent-period {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.rent-days {
  color: #3b82f6;
}

.order-price {
  text-align: right;
}

.price-label {
  display: block;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  margin-top: 16px;
}

.order-actions {
  display: flex;
  gap: 12px;
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// 响应式
@media (max-width: 768px) {
  .order-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .order-price {
    text-align: left;
    width: 100%;
    padding-top: 16px;
    border-top: 1px solid #e2e8f0;
  }

  .order-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .order-actions {
    flex-wrap: wrap;

    .el-button {
      flex: 1;
      min-width: 100px;
    }
  }
}
</style>
