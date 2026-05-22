<template>
  <div class="comment-management-page">
    <PageHeader title="评价管理" subtitle="管理用户评价和回复" />

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="用户">
          <el-input v-model="filters.userName" placeholder="用户昵称" clearable />
        </el-form-item>
        <el-form-item label="设备">
          <el-input v-model="filters.droneModel" placeholder="设备型号" clearable />
        </el-form-item>
        <el-form-item label="评分">
          <el-select v-model="filters.rating" placeholder="全部" clearable style="width: 120px;">
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星及以下" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.hasReply" placeholder="全部" clearable style="width: 120px;">
            <el-option label="已回复" :value="true" />
            <el-option label="未回复" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 评价列表 -->
    <GlassCard class="table-card">
      <el-table :data="commentList" v-loading="loading" style="width: 100%">
        <el-table-column label="用户" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="row.userAvatar">{{ row.userNickname?.charAt(0) }}</el-avatar>
              <span class="user-name">{{ row.userNickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="设备" prop="droneModel" width="150" />
        <el-table-column label="评分" width="140">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled size="small" />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" min-width="250">
          <template #default="{ row }">
            <p class="comment-content">{{ row.content }}</p>
            <p v-if="row.reply" class="comment-reply">
              <span class="reply-label">回复：</span>{{ row.reply }}
            </p>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <StatusTag
              :text="row.reply ? '已回复' : '未回复'"
              :type="row.reply ? 'success' : 'warning'"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column label="评价时间" width="160">
          <template #default="{ row }">
            {{ row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleReply(row)">
              {{ row.reply ? '修改回复' : '回复' }}
            </el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
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
          @size-change="fetchCommentList"
          @current-change="fetchCommentList"
        />
      </div>
    </GlassCard>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyVisible" title="回复评价" width="600px">
      <div v-if="currentComment" class="reply-dialog">
        <div class="original-comment">
          <div class="comment-header">
            <el-avatar :size="40" :src="currentComment.userAvatar">
              {{ currentComment.userNickname?.charAt(0) }}
            </el-avatar>
            <div class="comment-info">
              <span class="user-name">{{ currentComment.userNickname }}</span>
              <el-rate v-model="currentComment.rating" disabled size="small" />
            </div>
            <span class="comment-time">{{ currentComment.createTime }}</span>
          </div>
          <p class="comment-text">{{ currentComment.content }}</p>
        </div>

        <el-form label-position="top">
          <el-form-item label="回复内容">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="4"
              placeholder="请输入回复内容..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="handleConfirmReply">提交回复</el-button>
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
import { getAdminComments, replyComment, deleteComment } from '@/api/comment'

const loading = ref(false)
const replying = ref(false)
const commentList = ref([])
const total = ref(0)
const replyVisible = ref(false)
const currentComment = ref(null)
const replyContent = ref('')

const filters = reactive({
  userName: '',
  droneModel: '',
  rating: null,
  hasReply: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const fetchCommentList = async () => {
  loading.value = true
  try {
    const res = await getAdminComments({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    commentList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取评价列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchCommentList()
}

const handleReset = () => {
  Object.assign(filters, { userName: '', droneModel: '', rating: null, hasReply: null })
  pagination.page = 1
  fetchCommentList()
}

const handleReply = (row) => {
  currentComment.value = row
  replyContent.value = row.reply || ''
  replyVisible.value = true
}

const handleConfirmReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replying.value = true
  try {
    await replyComment(currentComment.value.id, replyContent.value)
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchCommentList()
  } catch (error) {
    // 错误已处理
  } finally {
    replying.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？此操作不可恢复', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(row.id)
    ElMessage.success('删除成功')
    fetchCommentList()
  } catch {}
}

onMounted(() => {
  fetchCommentList()
})
</script>

<style lang="scss" scoped>
.comment-management-page {
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
  gap: 10px;
}

.user-name {
  font-weight: 500;
  color: #0f172a;
}

.comment-content {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.comment-reply {
  font-size: 13px;
  color: #64748b;
  margin: 8px 0 0;
  padding: 8px 12px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 8px;

  .reply-label {
    color: #3b82f6;
    font-weight: 500;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

// 回复弹窗
.reply-dialog {
  .original-comment {
    padding: 16px;
    background: #f8fafc;
    border-radius: 12px;
    margin-bottom: 20px;
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

  .comment-time {
    font-size: 13px;
    color: #64748b;
  }

  .comment-text {
    font-size: 14px;
    color: #64748b;
    margin: 0;
    line-height: 1.6;
  }
}
</style>
