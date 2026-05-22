<template>
  <div class="fault-audit-page">
    <PageHeader title="故障报修审核" subtitle="审核用户提交的故障报修申请" />

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="故障单号">
          <el-input v-model="filters.reportNo" placeholder="请输入故障单号" clearable />
        </el-form-item>
        <el-form-item label="故障类型">
          <el-select v-model="filters.faultType" placeholder="全部" clearable style="width: 120px;">
            <el-option label="飞行异常" value="flight" />
            <el-option label="图传问题" value="transmission" />
            <el-option label="电池故障" value="battery" />
            <el-option label="云台问题" value="gimbal" />
            <el-option label="遥控器故障" value="remote" />
            <el-option label="其他问题" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filters.auditStatus" placeholder="全部" clearable style="width: 120px;">
            <el-option label="待审核" :value="0" />
            <el-option label="确认故障" :value="1" />
            <el-option label="非故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 故障报修列表 -->
    <GlassCard class="table-card">
      <el-table :data="faultList" v-loading="loading" style="width: 100%">
        <el-table-column label="故障单号" prop="reportNo" width="200" />
        <el-table-column label="用户" width="100">
          <template #default="{ row }">
            {{ row.userName || row.userId }}
          </template>
        </el-table-column>
        <el-table-column label="设备信息" min-width="150">
          <template #default="{ row }">
            <div class="drone-info">
              <span class="drone-model">{{ row.droneModel || '-' }}</span>
              <span class="drone-brand">{{ row.droneBrand || '' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="故障类型" width="100">
          <template #default="{ row }">
            <el-tag type="danger" size="small">{{ getFaultTypeText(row.faultType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="故障描述" prop="faultDescription" min-width="200" show-overflow-tooltip />
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <StatusTag :text="getAuditStatusText(row.auditStatus)" :type="getAuditStatusType(row.auditStatus)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="提交时间" prop="createdTime" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleViewDetail(row)">详情</el-button>
            <el-button v-if="row.auditStatus === 0" text type="warning" @click="handleAudit(row)">审核</el-button>
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
          @size-change="fetchFaultList"
          @current-change="fetchFaultList"
        />
      </div>
    </GlassCard>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="故障报修详情" width="700px">
      <div v-if="currentFault" class="fault-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="故障单号">{{ currentFault.reportNo }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <StatusTag :text="getAuditStatusText(currentFault.auditStatus)" :type="getAuditStatusType(currentFault.auditStatus)" />
          </el-descriptions-item>
          <el-descriptions-item label="提交用户">{{ currentFault.userName || currentFault.userId }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentFault.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备型号">{{ currentFault.droneModel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联订单">{{ currentFault.orderNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="故障类型">
            <el-tag type="danger" size="small">{{ getFaultTypeText(currentFault.faultType) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="故障时间">{{ currentFault.faultTime || currentFault.createdTime }}</el-descriptions-item>
          <el-descriptions-item label="故障描述" :span="2">{{ currentFault.faultDescription }}</el-descriptions-item>
          <el-descriptions-item v-if="currentFault.auditStatus !== 0" label="审核备注" :span="2">{{ currentFault.auditRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="currentFault.auditStatus !== 0" label="审核时间">{{ currentFault.auditTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentFault.faultImages" class="fault-images">
          <h4>故障图片</h4>
          <el-image
            v-for="(img, index) in currentFault.faultImages.split(',')"
            :key="index"
            :src="img"
            :preview-src-list="currentFault.faultImages.split(',')"
            fit="cover"
            style="width: 100px; height: 100px; border-radius: 8px; margin-right: 12px;"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentFault?.auditStatus === 0" type="primary" @click="handleAudit(currentFault); detailVisible = false">去审核</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" title="审核故障报修" width="500px">
      <div class="audit-info" v-if="auditingFault">
        <p><strong>故障单号：</strong>{{ auditingFault.reportNo }}</p>
        <p><strong>故障类型：</strong>{{ getFaultTypeText(auditingFault.faultType) }}</p>
        <p><strong>故障描述：</strong>{{ auditingFault.faultDescription }}</p>
      </div>
      <el-divider />
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :value="1">确认故障</el-radio>
            <el-radio :value="2">非故障</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="3" placeholder="请输入审核备注..." />
        </el-form-item>
      </el-form>
      <div class="audit-tip" v-if="auditForm.auditStatus === 1">
        <el-alert type="info" :closable="false" show-icon>
          确认故障后将自动生成维修工单，无人机状态将变为"维护中"
        </el-alert>
      </div>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditing" @click="handleConfirmAudit">确认审核</el-button>
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
import { getFaultList, getAdminFaultDetail, auditFault } from '@/api/maintenance'

const loading = ref(false)
const auditing = ref(false)
const faultList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const auditVisible = ref(false)
const currentFault = ref(null)
const auditingFault = ref(null)

const filters = reactive({
  reportNo: '',
  faultType: '',
  auditStatus: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const auditForm = reactive({
  auditStatus: 1,
  auditRemark: ''
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
  return map[type] || type || '-'
}

const getAuditStatusText = (status) => {
  const map = { 0: '待审核', 1: '确认故障', 2: '非故障' }
  return map[status] ?? '未知'
}

const getAuditStatusType = (status) => {
  const map = { 0: 'warning', 1: 'danger', 2: 'info' }
  return map[status] || 'default'
}

const fetchFaultList = async () => {
  loading.value = true
  try {
    const res = await getFaultList({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    faultList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取故障报修列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchFaultList()
}

const handleReset = () => {
  Object.assign(filters, { reportNo: '', faultType: '', auditStatus: null })
  pagination.page = 1
  fetchFaultList()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getAdminFaultDetail(row.id)
    currentFault.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleAudit = (row) => {
  auditingFault.value = row
  auditForm.auditStatus = 1
  auditForm.auditRemark = ''
  auditVisible.value = true
}

const handleConfirmAudit = async () => {
  if (!auditForm.auditStatus) {
    ElMessage.warning('请选择审核结果')
    return
  }

  const confirmText = auditForm.auditStatus === 1
    ? '确认该报修为故障？将自动生成维修工单'
    : '确认该报修为非故障？'

  try {
    await ElMessageBox.confirm(confirmText, '确认审核')

    auditing.value = true
    await auditFault(auditingFault.value.id, auditForm)

    ElMessage.success('审核完成')
    auditVisible.value = false
    fetchFaultList()
  } catch (error) {
    if (error !== 'cancel') {
      // 错误已处理
    }
  } finally {
    auditing.value = false
  }
}

onMounted(() => {
  fetchFaultList()
})
</script>

<style lang="scss" scoped>
.fault-audit-page {
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

.fault-images {
  margin-top: 24px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 12px;
  }
}

.audit-info {
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;

  p {
    margin: 0 0 8px;
    color: #64748b;

    &:last-child {
      margin-bottom: 0;
    }

    strong {
      color: #0f172a;
    }
  }
}

.audit-tip {
  margin-top: 16px;
}
</style>
