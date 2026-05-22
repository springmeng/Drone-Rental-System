<template>
  <div class="fault-report-page">
    <PageHeader title="故障报修" subtitle="提交设备故障报修申请" :breadcrumbs="breadcrumbs" />

    <div class="report-content">
      <GlassCard class="report-form-card">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          label-position="top"
          size="large"
        >
          <el-form-item label="关联订单" prop="orderId">
            <el-select
              v-model="form.orderId"
              placeholder="请选择关联订单"
              filterable
              @change="handleOrderChange"
            >
              <el-option
                v-for="order in rentingOrders"
                :key="order.id"
                :label="`${order.orderNo} - ${order.droneModel}`"
                :value="order.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="设备型号">
            <el-input :value="selectedDrone?.model" disabled placeholder="选择订单后自动填充" />
          </el-form-item>

          <el-form-item label="故障类型" prop="faultType">
            <el-select v-model="form.faultType" placeholder="请选择故障类型">
              <el-option label="飞行异常" value="flight" />
              <el-option label="图传问题" value="transmission" />
              <el-option label="电池故障" value="battery" />
              <el-option label="云台问题" value="gimbal" />
              <el-option label="遥控器故障" value="remote" />
              <el-option label="其他问题" value="other" />
            </el-select>
          </el-form-item>

          <el-form-item label="故障描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="5"
              placeholder="请详细描述故障情况，包括故障发生时间、现象等..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="故障图片">
            <el-upload
              v-model:file-list="imageList"
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="5"
              accept="image/*"
              :on-preview="handlePreview"
              :on-exceed="handleExceed"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">最多上传5张图片，支持jpg、png格式</div>
          </el-form-item>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              提交报修
            </el-button>
            <el-button @click="router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </GlassCard>

      <!-- 报修记录 -->
      <GlassCard title="我的报修记录" class="history-card">
        <div v-if="faultList.length > 0" class="fault-list">
          <div v-for="fault in faultList" :key="fault.id" class="fault-item">
            <div class="fault-header">
              <span class="fault-type">{{ getFaultTypeText(fault.faultType) }}</span>
              <StatusTag :text="getFaultStatusText(fault.auditStatus)" :type="getFaultStatusType(fault.auditStatus)" size="small" />
            </div>
            <p class="fault-desc">{{ fault.faultDescription }}</p>
            <div class="fault-footer">
              <span class="fault-order">订单：{{ fault.orderNo || '-' }}</span>
              <span class="fault-time">{{ fault.createdTime }}</span>
            </div>
          </div>
        </div>
        <EmptyState v-else title="暂无报修记录" />
      </GlassCard>
    </div>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="600px">
      <img :src="previewUrl" style="width: 100%" alt="预览" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useAuthStore } from '@/stores/auth'
import { getMyOrders } from '@/api/order'
import { submitFault, getMyFaults, uploadFaultImage } from '@/api/maintenance'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const submitting = ref(false)
const rentingOrders = ref([])
const faultList = ref([])
const imageList = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')

const breadcrumbs = [
  { title: '首页', path: '/' },
  { title: '我的订单', path: '/orders' },
  { title: '故障报修' }
]

const form = reactive({
  orderId: route.query.orderId ? Number(route.query.orderId) : null,
  faultType: '',
  description: '',
  contactPhone: authStore.user?.phone || ''
})

const rules = {
  orderId: [{ required: true, message: '请选择关联订单', trigger: 'change' }],
  faultType: [{ required: true, message: '请选择故障类型', trigger: 'change' }],
  description: [
    { required: true, message: '请描述故障情况', trigger: 'blur' },
    { min: 10, message: '故障描述至少10个字', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const selectedOrder = computed(() => {
  if (!form.orderId) return null
  return rentingOrders.value.find(o => o.id === form.orderId)
})

const selectedDrone = computed(() => {
  return selectedOrder.value ? { model: selectedOrder.value.droneModel } : null
})

const getFaultTypeText = (type) => {
  const map = {
    flight: '飞行异常',
    transmission: '图传问题',
    battery: '电池故障',
    gimbal: '云台问题',
    remote: '遥控器故障',
    other: '其他问题'
  }
  return map[type] || type
}

const getFaultStatusText = (status) => {
  const map = {
    0: '待处理',
    1: '处理中',
    2: '已完成'
  }
  return map[status] || '未知'
}

const getFaultStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'success'
  }
  return map[status] || 'default'
}

// 获取租赁中的订单
const fetchRentingOrders = async () => {
  try {
    const res = await getMyOrders({ status: 3 }) // 3: 租赁中
    rentingOrders.value = res.data?.records || []
  } catch (error) {
    console.error('获取订单失败:', error)
  }
}

// 获取报修记录
const fetchFaultList = async () => {
  try {
    const res = await getMyFaults({ page: 1, pageSize: 10 })
    faultList.value = res.data?.records || []
  } catch (error) {
    console.error('获取报修记录失败:', error)
  }
}

const handleOrderChange = (orderId) => {
  const order = rentingOrders.value.find(o => o.id === orderId)
  if (order) {
    form.droneId = order.droneId
  }
}

const handlePreview = (file) => {
  previewUrl.value = file.url
  previewVisible.value = true
}

const handleExceed = () => {
  ElMessage.warning('最多上传5张图片')
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 上传图片
      const imageUrls = []
      for (const file of imageList.value) {
        if (file.raw) {
          const res = await uploadFaultImage(file.raw)
          imageUrls.push(res.data)
        }
      }

      // 提交报修
      await submitFault({
        orderId: form.orderId,
        droneId: selectedOrder.value?.droneId,
        faultType: form.faultType,
        faultDescription: form.description,
        faultImages: imageUrls.join(','),
        contactPhone: form.contactPhone
      })

      ElMessage.success('报修申请已提交')
      formRef.value.resetFields()
      imageList.value = []
      fetchFaultList()
    } catch (error) {
      // 错误已处理
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  fetchRentingOrders()
  fetchFaultList()
})
</script>

<style lang="scss" scoped>
.fault-report-page {
  min-height: 100%;
}

.report-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  align-items: start;
}

.report-form-card {
  :deep(.el-form) {
    max-width: 600px;
  }

  :deep(.el-select) {
    width: 100%;
  }
}

.upload-tip {
  font-size: 12px;
  color: #64748b;
  margin-top: 8px;
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
  .report-content {
    grid-template-columns: 1fr;
  }
}
</style>
