<template>
  <div class="order-management-page">
    <PageHeader title="订单管理" subtitle="管理所有租赁订单" />

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="订单号">
          <el-input v-model="filters.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户手机">
          <el-input v-model="filters.userPhone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.orderStatus" placeholder="全部" clearable style="width: 120px;">
            <el-option label="待支付" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="租赁中" :value="3" />
            <el-option label="已归还" :value="4" />
            <el-option label="已取消" :value="5" />
            <el-option label="已退款" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 订单列表 -->
    <GlassCard class="table-card">
      <el-table :data="orderList" v-loading="loading" style="width: 100%">
        <el-table-column label="订单信息" min-width="200">
          <template #default="{ row }">
            <div class="order-info">
              <span class="order-no">{{ row.orderNo }}</span>
              <span class="order-time">{{ formatDateTime(row.createdTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <span class="user-name">{{ row.username || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="设备" prop="droneModel" width="150" />
        <el-table-column label="租赁周期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.rentalStartTime) }} ~ {{ formatDate(row.rentalEndTime) }}
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :text="getStatusText(row.orderStatus)" :type="getStatusType(row.orderStatus)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleViewDetail(row)">详情</el-button>
            <!-- 待发货(1): 发货 -->
            <template v-if="row.orderStatus === 1">
              <el-button text type="success" @click="handleShip(row)">发货</el-button>
            </template>
            <!-- 租赁中(3): 确认归还 -->
            <template v-if="row.orderStatus === 3">
              <el-button text type="warning" @click="handleConfirmReturn(row)">确认归还</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="fetchOrderList"
          @current-change="fetchOrderList"
        />
      </div>
    </GlassCard>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <StatusTag :text="getStatusText(currentOrder.orderStatus)" :type="getStatusType(currentOrder.orderStatus)" />
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentOrder.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备型号">{{ currentOrder.droneModel }}</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ currentOrder.unitPrice }}/天</el-descriptions-item>
          <el-descriptions-item label="租赁周期">{{ formatDate(currentOrder.rentalStartTime) }} ~ {{ formatDate(currentOrder.rentalEndTime) }}</el-descriptions-item>
          <el-descriptions-item label="租赁天数">{{ currentOrder.rentalDays }} 天</el-descriptions-item>
          <el-descriptions-item label="押金">¥{{ currentOrder.depositAmount }}</el-descriptions-item>
          <el-descriptions-item label="订单总额">
            <span class="price-highlight">¥{{ currentOrder.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(currentOrder.createdTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.expressCompany" placeholder="请选择快递公司">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="京东物流" value="JD" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.expressNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="handleConfirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { getAdminOrders, getAdminOrderDetail, shipOrder, confirmReturn } from '@/api/order'

const loading = ref(false)
const shipping = ref(false)
const orderList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const shipVisible = ref(false)
const currentOrder = ref(null)

const filters = reactive({
  orderNo: '',
  userPhone: '',
  orderStatus: null,
  dateRange: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const shipForm = reactive({
  orderId: null,
  expressCompany: '',
  expressNo: ''
})

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '租赁中', 4: '已归还', 5: '已取消', 6: '已退款' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'info', 3: 'primary', 4: 'success', 5: 'default', 6: 'default' }
  return map[status] || 'default'
}

const getPaymentMethod = (method) => {
  const map = { alipay: '支付宝', wechat: '微信支付', balance: '余额支付' }
  return map[method] || method || '-'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dateTime.replace('T', ' ').substring(0, 19)
}

// 格式化日期
const formatDate = (dateTime) => {
  if (!dateTime) return '-'
  return dateTime.substring(0, 10)
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filters.dateRange?.length) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }
    delete params.dateRange

    const res = await getAdminOrders(params)
    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchOrderList()
}

const handleReset = () => {
  Object.assign(filters, { orderNo: '', userPhone: '', orderStatus: null, dateRange: null })
  pagination.page = 1
  fetchOrderList()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getAdminOrderDetail(row.id)
    currentOrder.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const handleShip = (row) => {
  shipForm.orderId = row.id
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipVisible.value = true
}

const handleConfirmShip = async () => {
  if (!shipForm.expressCompany || !shipForm.expressNo) {
    ElMessage.warning('请填写快递信息')
    return
  }

  shipping.value = true
  try {
    await shipOrder(shipForm.orderId, {
      expressCompany: shipForm.expressCompany,
      expressNo: shipForm.expressNo
    })
    ElMessage.success('发货成功')
    shipVisible.value = false
    fetchOrderList()
  } catch (error) {
    // 错误已处理
  } finally {
    shipping.value = false
  }
}

const handleConfirmReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确认用户已归还设备且设备无损坏？确认后将退还押金', '确认归还', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await confirmReturn(row.id, {})
    ElMessage.success('已确认归还')
    fetchOrderList()
  } catch {}
}

onMounted(() => {
  fetchOrderList()
})
</script>

<style lang="scss" scoped>
.order-management-page {
  min-height: 100%;
}

.filter-card {
  margin-bottom: 24px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;

  .el-form-item {
    margin-bottom: 0;
  }
}

.table-card {
  :deep(.el-table) {
    --el-table-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-header-bg-color: #f8fafc;
  }
}

.order-info, .user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-no {
  font-weight: 500;
  color: #0f172a;
}

.order-time {
  font-size: 12px;
  color: #64748b;
}

.user-name {
  font-weight: 500;
  color: #0f172a;
}

.user-phone {
  font-size: 12px;
  color: #64748b;
}

.price {
  color: #3b82f6;
  font-weight: 600;
}

.price-highlight {
  font-size: 18px;
  font-weight: 700;
  color: #3b82f6;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>
