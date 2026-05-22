<template>
  <div class="maintenance-management-page">
    <PageHeader title="维保管理" subtitle="管理设备维护保养记录" />

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="设备型号">
          <el-input v-model="filters.droneModel" placeholder="请输入型号" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部" clearable style="width: 120px;">
            <el-option label="用户报修" value="fault" />
            <el-option label="定期维护" value="regular" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 维保列表 -->
    <GlassCard class="table-card">
      <el-table :data="maintenanceList" v-loading="loading" style="width: 100%">
        <el-table-column label="维保单号" prop="ticketNo" width="200" />
        <el-table-column label="设备信息" min-width="150">
          <template #default="{ row }">
            <div class="drone-info">
              <span class="drone-model">{{ row.droneModel || '-' }}</span>
              <span class="drone-brand">{{ row.droneBrand || '' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.maintenanceType === 'fault' ? 'danger' : 'info'" size="small">
              {{ row.faultReportId ? '用户报修' : '定期维护' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="故障类型" width="100">
          <template #default="{ row }">
            {{ getFaultTypeText(row.faultType) }}
          </template>
        </el-table-column>
        <el-table-column label="描述" prop="maintenanceDescription" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :text="getStatusText(row.status)" :type="getStatusType(row.status)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleViewDetail(row)">详情</el-button>
            <template v-if="row.status === 0">
              <el-button text type="success" @click="handleStart(row)">开始处理</el-button>
            </template>
            <template v-if="row.status === 1">
              <el-button text type="warning" @click="handleComplete(row)">完成</el-button>
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
          @size-change="fetchMaintenanceList"
          @current-change="fetchMaintenanceList"
        />
      </div>
    </GlassCard>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="维保详情" width="700px">
      <div v-if="currentMaintenance" class="maintenance-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="维保单号">{{ currentMaintenance.ticketNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <StatusTag :text="getStatusText(currentMaintenance.status)" :type="getStatusType(currentMaintenance.status)" />
          </el-descriptions-item>
          <el-descriptions-item label="设备型号">{{ currentMaintenance.droneModel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备品牌">{{ currentMaintenance.droneBrand || '-' }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ currentMaintenance.faultReportId ? '用户报修' : '定期维护' }}</el-descriptions-item>
          <el-descriptions-item label="故障类型">{{ getFaultTypeText(currentMaintenance.faultType) }}</el-descriptions-item>
          <el-descriptions-item label="问题描述" :span="2">{{ currentMaintenance.maintenanceDescription || '-' }}</el-descriptions-item>
          <el-descriptions-item label="进度备注" :span="2">{{ currentMaintenance.progressNotes || '-' }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ currentMaintenance.assigneeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预估费用">{{ currentMaintenance.estimatedCost ? `¥${currentMaintenance.estimatedCost}` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际费用">{{ currentMaintenance.actualCost ? `¥${currentMaintenance.actualCost}` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际天数">{{ currentMaintenance.actualDays ? `${currentMaintenance.actualDays}天` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentMaintenance.createdTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ currentMaintenance.completeTime || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 完成弹窗 -->
    <el-dialog v-model="completeVisible" title="完成维保" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="completeForm.result" type="textarea" :rows="4" placeholder="请输入处理结果..." />
        </el-form-item>
        <el-form-item label="维修费用">
          <el-input-number v-model="completeForm.cost" :min="0" :precision="2" style="width: 200px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" :loading="completing" @click="handleConfirmComplete">确认完成</el-button>
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
import { getMaintenanceList, getMaintenanceDetail, startMaintenance, completeMaintenance } from '@/api/maintenance'

const loading = ref(false)
const completing = ref(false)
const maintenanceList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const completeVisible = ref(false)
const currentMaintenance = ref(null)

const filters = reactive({
  droneModel: '',
  type: '',
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const completeForm = reactive({
  id: null,
  result: '',
  cost: 0
})

const getStatusText = (status) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已完成' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success' }
  return map[status] || 'default'
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

const fetchMaintenanceList = async () => {
  loading.value = true
  try {
    const res = await getMaintenanceList({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    maintenanceList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取维保列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchMaintenanceList()
}

const handleReset = () => {
  Object.assign(filters, { droneModel: '', type: '', status: null })
  pagination.page = 1
  fetchMaintenanceList()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getMaintenanceDetail(row.id)
    currentMaintenance.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确定开始处理该维保单？', '提示')
    await startMaintenance(row.id)
    ElMessage.success('已开始处理')
    fetchMaintenanceList()
  } catch {}
}

const handleComplete = (row) => {
  completeForm.id = row.id
  completeForm.result = ''
  completeForm.cost = 0
  completeVisible.value = true
}

const handleConfirmComplete = async () => {
  if (!completeForm.result) {
    ElMessage.warning('请输入处理结果')
    return
  }

  completing.value = true
  try {
    await completeMaintenance(completeForm.id, {
      progressNote: completeForm.result,
      actualCost: completeForm.cost
    })
    ElMessage.success('维保已完成')
    completeVisible.value = false
    fetchMaintenanceList()
  } catch (error) {
    // 错误已处理
  } finally {
    completing.value = false
  }
}

onMounted(() => {
  fetchMaintenanceList()
})
</script>

<style lang="scss" scoped>
.maintenance-management-page {
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

.drone-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.drone-model {
  font-weight: 600;
  color: #0f172a;
}

.drone-brand {
  font-size: 12px;
  color: #64748b;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.maintenance-images {
  margin-top: 24px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 12px;
  }
}
</style>
