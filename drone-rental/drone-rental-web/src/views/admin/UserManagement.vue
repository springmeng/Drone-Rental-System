<template>
  <div class="user-management-page">
    <PageHeader title="用户管理" subtitle="管理系统所有注册用户" />

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="用户名/昵称/手机号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 用户列表 -->
    <GlassCard class="table-card">
      <el-table :data="userList" v-loading="loading" style="width: 100%">
        <el-table-column label="用户信息" min-width="240">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="getAvatarUrl(row.avatar)">{{ row.nickname?.charAt(0) }}</el-avatar>
              <div class="user-detail">
                <span class="user-name">{{ row.nickname }}</span>
                <span class="user-username">@{{ row.username }}</span>
                <span class="user-phone">{{ row.phone || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="实名信息" min-width="150">
          <template #default="{ row }">
            <div class="verification-info">
              <span v-if="row.idCard" class="id-card-text">{{ maskIdCard(row.idCard) }}</span>
              <span v-else class="text-muted">-</span>
              <StatusTag
                :text="getVerificationStatusText(row.verificationStatus)"
                :type="getVerificationStatusType(row.verificationStatus)"
                size="small"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" width="120">
          <template #default="{ row }">
            <StatusTag :text="getStatusText(row.status)" :type="getStatusType(row.status)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="订单数" width="100" prop="orderCount" />
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleViewDetail(row)">详情</el-button>
            <el-button
              text
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button text type="warning" @click="handleResetPassword(row)">重置密码</el-button>
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
          @size-change="fetchUserList"
          @current-change="fetchUserList"
        />
      </div>
    </GlassCard>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="650px">
      <div v-if="currentUser" class="user-detail-dialog">
        <div class="detail-header">
          <el-avatar :size="80" :src="getAvatarUrl(currentUser.avatar)">{{ currentUser.nickname?.charAt(0) }}</el-avatar>
          <div class="detail-info">
            <h3>{{ currentUser.nickname }}</h3>
            <p class="username-text">@{{ currentUser.username }}</p>
            <div class="status-tags">
              <StatusTag :text="getStatusText(currentUser.status)" :type="getStatusType(currentUser.status)" />
              <StatusTag
                :text="getVerificationStatusText(currentUser.verificationStatus)"
                :type="getVerificationStatusType(currentUser.verificationStatus)"
              />
              <StatusTag
                :text="getCreditStatusText(currentUser.creditStatus)"
                :type="getCreditStatusType(currentUser.creditStatus)"
              />
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ currentUser.realName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ currentUser.idCard || '-' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ currentUser.createdTime }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ currentUser.lastLoginTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 消费信息 -->
        <div class="detail-section">
          <h4>消费信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单数量">{{ currentUser.orderCount || 0 }} 单</el-descriptions-item>
            <el-descriptions-item label="消费金额">¥{{ currentUser.totalSpent || 0 }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 诚信记录 -->
        <div class="detail-section">
          <h4>诚信状态</h4>
          <div class="credit-info">
            <div class="credit-status-card" :class="currentUser.creditStatus === 1 ? 'credit-good' : 'credit-bad'">
              <div class="credit-icon">
                <el-icon :size="32">
                  <component :is="currentUser.creditStatus === 1 ? 'Select' : 'CloseBold'" />
                </el-icon>
              </div>
              <div class="credit-detail">
                <span class="credit-label">诚信状态</span>
                <span class="credit-value">{{ getCreditStatusText(currentUser.creditStatus) }}</span>
              </div>
            </div>
            <div class="credit-desc">
              <p v-if="currentUser.creditStatus === 1">该用户诚信记录良好，可正常租赁设备。</p>
              <p v-else class="credit-warning">该用户存在不良记录，已被限制租赁服务。</p>
            </div>
          </div>
        </div>

        <!-- 诚信变更历史 -->
        <div class="detail-section">
          <h4>诚信变更历史</h4>
          <div v-if="creditRecords.length === 0" class="no-records">
            <span>暂无诚信变更记录</span>
          </div>
          <div v-else class="credit-records-list">
            <div v-for="record in creditRecords" :key="record.id" class="record-item">
              <div class="record-header">
                <el-tag :type="getChangeTypeType(record.changeType)" size="small">
                  {{ getChangeTypeText(record.changeType) }}
                </el-tag>
                <span class="record-time">{{ record.createdTime }}</span>
              </div>
              <div class="record-content">
                <p class="record-reason">{{ record.reason }}</p>
                <p class="record-operator">操作人: {{ record.operatorName }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button
          v-if="currentUser?.creditStatus === 1"
          type="danger"
          @click="handleUpdateCredit(currentUser, 0)"
        >
          标记为不良
        </el-button>
        <el-button
          v-else-if="currentUser?.creditStatus === 0"
          type="success"
          @click="handleUpdateCredit(currentUser, 1)"
        >
          恢复诚信
        </el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Select, CloseBold } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { getUserList, getUserDetail, updateUserStatus, updateCreditStatus, resetUserPassword, getCreditRecords } from '@/api/admin'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentUser = ref(null)
const creditRecords = ref([])

const filters = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const getStatusText = (status) => {
  const map = { 0: '禁用', 1: '正常' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'error', 1: 'success' }
  return map[status] || 'default'
}

const getCreditStatusText = (status) => {
  const map = { 0: '诚信不良', 1: '诚信良好' }
  return map[status] || '未知'
}

const getCreditStatusType = (status) => {
  const map = { 0: 'error', 1: 'success' }
  return map[status] || 'default'
}

const getVerificationStatusText = (status) => {
  const map = { 0: '未认证', 1: '待审核', 2: '已认证', 3: '未通过' }
  return map[status] ?? '未认证'
}

const getVerificationStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'error' }
  return map[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dateTime.replace('T', ' ').substring(0, 19)
}

// 脱敏证件号码
const maskIdCard = (idCard) => {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 4) + '****' + idCard.substring(idCard.length - 4)
}

// 获取头像完整URL
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `/api${avatar}`
}

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchUserList()
}

const handleReset = () => {
  filters.keyword = ''
  filters.status = null
  pagination.page = 1
  fetchUserList()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getUserDetail(row.id)
    currentUser.value = res.data
    detailVisible.value = true
    // 获取诚信记录
    fetchCreditRecords(row.id)
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

const fetchCreditRecords = async (userId) => {
  try {
    const res = await getCreditRecords(userId)
    creditRecords.value = res.data || []
  } catch (error) {
    console.error('获取诚信记录失败:', error)
    creditRecords.value = []
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success(`${action}成功`)
    fetchUserList()
  } catch {}
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户密码吗？重置后密码为123456', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为123456')
  } catch {}
}

const handleUpdateCredit = async (user, creditStatus) => {
  const action = creditStatus === 0 ? '标记为诚信不良' : '恢复诚信状态'

  try {
    const { value: reason } = await ElMessageBox.prompt(
      creditStatus === 0
        ? '确定将该用户标记为诚信不良？标记后该用户将无法进行租赁。'
        : '确定恢复该用户的诚信状态？恢复后该用户可正常租赁。',
      action,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入变更原因（可选）',
        type: creditStatus === 0 ? 'warning' : 'success'
      }
    )
    await updateCreditStatus(user.id, creditStatus, reason || '')
    ElMessage.success(`已${action}`)
    // 更新当前用户的诚信状态
    currentUser.value.creditStatus = creditStatus
    // 刷新诚信记录
    fetchCreditRecords(user.id)
    fetchUserList()
  } catch {}
}

const getChangeTypeText = (type) => {
  const map = { 1: '标记不良', 2: '恢复正常', 3: '系统扣分', 4: '系统加分' }
  return map[type] || '未知'
}

const getChangeTypeType = (type) => {
  const map = { 1: 'danger', 2: 'success', 3: 'warning', 4: 'primary' }
  return map[type] || 'info'
}

onMounted(() => {
  fetchUserList()
})
</script>

<style lang="scss" scoped>
.user-management-page {
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

.user-username {
  font-size: 13px;
  color: #3b82f6;
}

.user-phone {
  font-size: 12px;
  color: #64748b;
}

.text-muted {
  color: #475569;
}

.verification-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.id-card-text {
  font-size: 13px;
  color: #64748b;
  font-family: monospace;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

// 详情弹窗内容样式
.user-detail-dialog {
  .detail-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #e2e8f0;

    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #0f172a;
      margin: 0 0 4px;
    }

    .username-text {
      font-size: 14px;
      color: #3b82f6;
      margin: 0 0 8px;
    }
  }

  .detail-info {
    display: flex;
    flex-direction: column;
  }

  .status-tags {
    display: flex;
    gap: 8px;
  }

  .detail-section {
    margin-top: 24px;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #0f172a;
      margin: 0 0 16px;
    }
  }

  .credit-info {
    .credit-status-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      border-radius: 16px;
      margin-bottom: 16px;

      &.credit-good {
        background: rgba(34, 197, 94, 0.1);
        border: 1px solid rgba(34, 197, 94, 0.3);

        .credit-icon {
          background: rgba(34, 197, 94, 0.2);
          color: #22c55e;
        }

        .credit-value {
          color: #22c55e;
        }
      }

      &.credit-bad {
        background: rgba(239, 68, 68, 0.1);
        border: 1px solid rgba(239, 68, 68, 0.3);

        .credit-icon {
          background: rgba(239, 68, 68, 0.2);
          color: #ef4444;
        }

        .credit-value {
          color: #ef4444;
        }
      }
    }

    .credit-icon {
      width: 56px;
      height: 56px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .credit-detail {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .credit-label {
      font-size: 14px;
      color: #64748b;
    }

    .credit-value {
      font-size: 20px;
      font-weight: 700;
    }

    .credit-desc {
      p {
        margin: 0;
        font-size: 14px;
        color: #64748b;
        line-height: 1.6;
      }

      .credit-warning {
        color: #f87171;
      }
    }
  }

  .no-records {
    padding: 24px;
    text-align: center;
    color: #64748b;
    background: #fafbfc;
    border-radius: 12px;
  }

  .credit-records-list {
    max-height: 300px;
    overflow-y: auto;

    .record-item {
      padding: 16px;
      background: #fafbfc;
      border-radius: 12px;
      margin-bottom: 12px;
      border: 1px solid #e2e8f0;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .record-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;
    }

    .record-time {
      font-size: 12px;
      color: #64748b;
    }

    .record-content {
      p {
        margin: 0;
        font-size: 14px;
        line-height: 1.6;
      }

      .record-reason {
        color: #334155;
        margin-bottom: 4px;
      }

      .record-operator {
        color: #64748b;
        font-size: 12px;
      }
    }
  }
}
</style>
