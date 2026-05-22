<template>
  <div class="audit-management-page">
    <PageHeader title="资质审核" subtitle="审核用户实名认证申请" />

    <!-- 统计卡片 -->
    <BentoGrid :cols="4" :gap="16" class="stat-grid">
      <StatTile :value="stats.pending" label="待审核" color="warning" :icon="ClockIcon" />
      <StatTile :value="stats.todayApproved" label="今日通过" color="success" :icon="SelectIcon" />
      <StatTile :value="stats.todayRejected" label="今日拒绝" color="error" :icon="CloseIcon" />
      <StatTile :value="stats.total" label="累计审核" color="primary" :icon="DocumentIcon" />
    </BentoGrid>

    <!-- 审核列表 -->
    <GlassCard class="table-card">
      <div class="table-header">
        <el-radio-group v-model="auditStatus" @change="handleStatusChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
          <el-radio-button value="rejected">已拒绝</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="auditList" v-loading="loading" style="width: 100%">
        <el-table-column label="用户" width="120">
          <template #default="{ row }">
            {{ row.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="证件号码" prop="certificateNo" min-width="150" />
        <el-table-column label="证件类型" prop="certificateType" min-width="160" />
        <el-table-column label="资质证书" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.certificateImage"
              :src="getImageUrl(row.certificateImage)"
              :preview-src-list="[getImageUrl(row.certificateImage)]"
              fit="cover"
              style="width: 60px; height: 40px; border-radius: 6px;"
            />
            <span v-else class="text-muted">未上传</span>
          </template>
        </el-table-column>
        <el-table-column label="有效期" min-width="200">
          <template #default="{ row }">
            {{ row.validStartDate }} ~ {{ row.validEndDate }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :text="getAuditStatusText(row.auditStatus)" :type="getAuditStatusType(row.auditStatus)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 4px; flex-wrap: nowrap;">
              <template v-if="row.auditStatus === 0">
                <el-button text type="success" @click="handleApprove(row)">通过</el-button>
                <el-button text type="danger" @click="handleReject(row)">拒绝</el-button>
              </template>
              <el-button text type="primary" @click="handleViewDetail(row)">详情</el-button>
            </div>
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
          @size-change="fetchAuditList"
          @current-change="fetchAuditList"
        />
      </div>
    </GlassCard>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="审核详情" width="700px">
      <div v-if="currentAudit" class="audit-detail">
        <div class="detail-section">
          <h4>资质信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户">{{ currentAudit.username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="证件类型">{{ currentAudit.certificateType }}</el-descriptions-item>
            <el-descriptions-item label="证件号码">{{ currentAudit.certificateNo }}</el-descriptions-item>
            <el-descriptions-item label="有效期">{{ currentAudit.validStartDate }} ~ {{ currentAudit.validEndDate }}</el-descriptions-item>
            <el-descriptions-item label="提交时间" :span="2">{{ formatDateTime(currentAudit.createdTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>资质证书</h4>
          <el-image
            v-if="currentAudit.certificateImage"
            :src="getImageUrl(currentAudit.certificateImage)"
            :preview-src-list="[getImageUrl(currentAudit.certificateImage)]"
            fit="contain"
            style="max-width: 100%; max-height: 300px;"
          />
          <EmptyState v-else title="未上传资质证书" />
        </div>

        <div v-if="currentAudit.auditStatus !== 0" class="detail-section">
          <h4>审核结果</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="审核状态">
              <StatusTag :text="getAuditStatusText(currentAudit.auditStatus)" :type="getAuditStatusType(currentAudit.auditStatus)" />
            </el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ formatDateTime(currentAudit.auditTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="currentAudit.auditStatus === 2" label="拒绝原因" :span="2">
              {{ currentAudit.auditRemark }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <template v-if="currentAudit?.auditStatus === 0">
          <el-button type="success" @click="handleApprove(currentAudit)">通过</el-button>
          <el-button type="danger" @click="handleReject(currentAudit)">拒绝</el-button>
        </template>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, markRaw } from 'vue'
import { Clock, Select, CloseBold, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import BentoGrid from '@/components/common/BentoGrid.vue'
import StatTile from '@/components/common/StatTile.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getPendingAudits, approveAudit, rejectAudit, getAuditStats } from '@/api/admin'

const ClockIcon = markRaw(Clock)
const SelectIcon = markRaw(Select)
const CloseIcon = markRaw(CloseBold)
const DocumentIcon = markRaw(Document)

const loading = ref(false)
const auditList = ref([])
const total = ref(0)
const auditStatus = ref('')
const detailVisible = ref(false)
const currentAudit = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const stats = reactive({
  pending: 0,
  todayApproved: 0,
  todayRejected: 0,
  total: 0
})

const getAuditStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

const getAuditStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'error' }
  return map[status] || 'default'
}

const maskIdCard = (idCard) => {
  if (!idCard) return '-'
  return idCard.replace(/^(.{4})(.+)(.{4})$/, '$1**********$3')
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 19)
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `/api${url}`
}

const fetchAuditList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.pageSize
    }
    if (auditStatus.value) {
      params.auditStatus = auditStatus.value === 'pending' ? 0 : auditStatus.value === 'approved' ? 1 : 2
    }

    const res = await getPendingAudits(params)
    auditList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取审核列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = () => {
  pagination.page = 1
  fetchAuditList()
}

const handleViewDetail = (row) => {
  currentAudit.value = row
  detailVisible.value = true
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定通过该用户的实名认证申请？', '审核通过', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveAudit(row.id, {})
    ElMessage.success('审核通过')
    detailVisible.value = false
    fetchAuditList()
  } catch {}
}

const handleReject = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    await rejectAudit(row.id, { reason: value })
    ElMessage.success('已拒绝')
    detailVisible.value = false
    fetchAuditList()
  } catch {}
}

const fetchStats = async () => {
  try {
    const res = await getAuditStats()
    const data = res.data || {}
    stats.pending = data.pending || 0
    stats.todayApproved = data.todayApproved || 0
    stats.todayRejected = data.todayRejected || 0
    stats.total = data.total || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchAuditList()
  fetchStats()
})
</script>

<style lang="scss" scoped>
.audit-management-page {
  min-height: 100%;
}

.stat-grid {
  margin-bottom: 24px;
}

.table-card {
  :deep(.el-table) {
    --el-table-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-header-bg-color: #f8fafc;
  }
}

.table-header {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-weight: 600;
  color: #0f172a;
}

.user-phone {
  font-size: 13px;
  color: #64748b;
}

.text-muted {
  color: #475569;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

// 详情弹窗
.audit-detail {
  .detail-section {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #0f172a;
      margin: 0 0 16px;
    }
  }
}
</style>
