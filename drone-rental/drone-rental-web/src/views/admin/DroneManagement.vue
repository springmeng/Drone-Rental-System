<template>
  <div class="drone-management-page">
    <PageHeader title="无人机管理" subtitle="管理系统所有无人机设备">
      <template #actions>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增设备
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索筛选 -->
    <GlassCard class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="型号/品牌" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部" clearable style="width: 140px;">
            <el-option label="航拍无人机" value="航拍" />
            <el-option label="测绘无人机" value="测绘" />
            <el-option label="农业无人机" value="农业" />
            <el-option label="巡检无人机" value="巡检" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="可租赁" :value="1" />
            <el-option label="缺货" :value="0" />
            <el-option label="维护中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="上架状态">
          <el-select v-model="filters.onShelf" placeholder="全部" clearable style="width: 120px;">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 设备列表 -->
    <GlassCard class="table-card">
      <el-table :data="droneList" v-loading="loading" style="width: 100%">
        <el-table-column label="设备信息" min-width="250">
          <template #default="{ row }">
            <div class="drone-info">
              <el-image :src="getImageUrl(row.image)" fit="cover" style="width: 80px; height: 50px; border-radius: 8px;" />
              <div class="drone-detail">
                <span class="drone-model">{{ row.model }}</span>
                <span class="drone-brand">{{ row.brand }} · {{ row.type }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="日租金" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.pricePerDay }}</span>
          </template>
        </el-table-column>
        <el-table-column label="押金" width="100">
          <template #default="{ row }">
            <span>¥{{ row.deposit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="80">
          <template #default="{ row }">
            <span :class="{ 'stock-low': row.stock < 3 }">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备状态" width="100">
          <template #default="{ row }">
            <StatusTag :text="getStatusText(row.status)" :type="getStatusType(row.status)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="上架状态" width="100">
          <template #default="{ row }">
            <StatusTag
              :text="row.onShelf === 1 ? '已上架' : '已下架'"
              :type="row.onShelf === 1 ? 'success' : 'default'"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column label="租赁次数" width="100" prop="rentCount" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="info" @click="handleStock(row)">库存</el-button>
            <el-button
              text
              :type="row.onShelf === 1 ? 'warning' : 'success'"
              @click="handleToggleShelf(row)"
            >
              {{ row.onShelf === 1 ? '下架' : '上架' }}
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
          @size-change="fetchDroneList"
          @current-change="fetchDroneList"
        />
      </div>
    </GlassCard>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" :title="isEdit ? '编辑设备' : '新增设备'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="航拍无人机" value="航拍" />
                <el-option label="测绘无人机" value="测绘" />
                <el-option label="农业无人机" value="农业" />
                <el-option label="巡检无人机" value="巡检" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="可租赁" :value="1" />
                <el-option label="缺货" :value="0" />
                <el-option label="维护中" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上架状态" prop="onShelf">
              <el-select v-model="form.onShelf" placeholder="请选择上架状态">
                <el-option label="上架" :value="1" />
                <el-option label="下架" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日租金" prop="pricePerDay">
              <el-input-number v-model="form.pricePerDay" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="form.deposit" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="飞行时间" prop="flightTime">
              <el-input-number v-model="form.flightTime" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大速度" prop="maxSpeed">
              <el-input-number v-model="form.maxSpeed" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大载重" prop="maxPayload">
              <el-input-number v-model="form.maxPayload" :min="0" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设备描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入设备描述" />
        </el-form-item>
        <el-form-item label="设备图片" prop="image">
          <div class="image-upload-wrapper">
            <el-upload
              class="drone-image-upload"
              :show-file-list="false"
              :http-request="handleImageUpload"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <div v-if="form.image" class="image-preview">
                <el-image :src="getImageUrl(form.image)" fit="cover" />
                <div class="image-actions">
                  <el-icon><Upload /></el-icon>
                  <span>更换图片</span>
                </div>
              </div>
              <div v-else class="upload-placeholder" v-loading="uploading">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <span>上传设备图片</span>
              </div>
            </el-upload>
            <div class="upload-tip">支持 jpg、png 格式，大小不超过 5MB</div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 库存弹窗 -->
    <el-dialog v-model="stockVisible" title="调整库存" width="400px">
      <el-form :model="stockForm" label-width="80px">
        <el-form-item label="当前库存">
          <span class="current-stock">{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整为">
          <el-input-number v-model="stockForm.newStock" :min="0" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockVisible = false">取消</el-button>
        <el-button type="primary" :loading="stockSaving" @click="handleSaveStock">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { getAdminDroneList, createDrone, updateDrone, deleteDrone, updateDroneStock, updateDroneShelf } from '@/api/drone'
import { upload } from '@/api/request'

const loading = ref(false)
const saving = ref(false)
const stockSaving = ref(false)
const droneList = ref([])
const total = ref(0)
const editVisible = ref(false)
const stockVisible = ref(false)
const isEdit = ref(false)
const currentDrone = ref(null)

const defaultImage = 'https://via.placeholder.com/80x50/1e293b/64748b?text=Drone'

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return defaultImage
  if (url.startsWith('http')) return url
  return `/api${url}`
}

const filters = reactive({
  keyword: '',
  type: '',
  status: null,
  onShelf: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const formRef = ref(null)
const form = reactive({
  brand: '',
  model: '',
  type: '',
  status: 1,
  onShelf: 1,
  pricePerDay: 0,
  deposit: 0,
  stock: 0,
  flightTime: 0,
  maxSpeed: 0,
  maxPayload: 0,
  description: '',
  image: ''
})

const uploading = ref(false)

const stockForm = reactive({
  id: null,
  currentStock: 0,
  newStock: 0
})

const rules = {
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  pricePerDay: [{ required: true, message: '请输入日租金', trigger: 'blur' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }]
}

const getStatusText = (status) => {
  const map = { 0: '缺货', 1: '可租赁', 2: '维护中' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'error', 1: 'success', 2: 'warning' }
  return map[status] || 'default'
}

const fetchDroneList = async () => {
  loading.value = true
  try {
    const res = await getAdminDroneList({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    droneList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchDroneList()
}

const handleReset = () => {
  Object.assign(filters, { keyword: '', type: '', status: null, onShelf: null })
  pagination.page = 1
  fetchDroneList()
}

const handleAdd = () => {
  isEdit.value = false
  currentDrone.value = null
  Object.assign(form, {
    brand: '', model: '', type: '', status: 1, onShelf: 1,
    pricePerDay: 0, deposit: 0, stock: 0,
    flightTime: 0, maxSpeed: 0, maxPayload: 0, description: '', image: ''
  })
  editVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentDrone.value = row
  Object.assign(form, row)
  editVisible.value = true
}

// 图片上传处理
const handleImageUpload = async (options) => {
  const { file } = options
  uploading.value = true
  try {
    const res = await upload('/common/upload', file)
    form.image = res.data
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error('图片上传失败')
  } finally {
    uploading.value = false
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      if (isEdit.value) {
        await updateDrone(currentDrone.value.id, form)
        ElMessage.success('更新成功')
      } else {
        await createDrone(form)
        ElMessage.success('新增成功')
      }
      editVisible.value = false
      fetchDroneList()
    } catch (error) {
      // 错误已处理
    } finally {
      saving.value = false
    }
  })
}

const handleStock = (row) => {
  stockForm.id = row.id
  stockForm.currentStock = row.stock
  stockForm.newStock = row.stock
  stockVisible.value = true
}

const handleSaveStock = async () => {
  stockSaving.value = true
  try {
    await updateDroneStock(stockForm.id, stockForm.newStock)
    ElMessage.success('库存已更新')
    stockVisible.value = false
    fetchDroneList()
  } catch (error) {
    // 错误已处理
  } finally {
    stockSaving.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？此操作不可恢复', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDrone(row.id)
    ElMessage.success('删除成功')
    fetchDroneList()
  } catch {}
}

const handleToggleShelf = async (row) => {
  const newShelf = row.onShelf === 1 ? 0 : 1
  const action = newShelf === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定要${action}该设备吗？${newShelf === 0 ? '下架后前台将无法租赁该设备' : '上架后前台可以租赁该设备'}`, `${action}确认`, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: newShelf === 0 ? 'warning' : 'success'
    })
    await updateDroneShelf(row.id, newShelf)
    ElMessage.success(`${action}成功`)
    fetchDroneList()
  } catch {}
}

onMounted(() => {
  fetchDroneList()
})
</script>

<style lang="scss" scoped>
.drone-management-page {
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
  align-items: center;
  gap: 12px;
}

.drone-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.drone-model {
  font-weight: 600;
  color: #0f172a;
}

.drone-brand {
  font-size: 13px;
  color: #64748b;
}

.price {
  color: #3b82f6;
  font-weight: 600;
}

.stock-low {
  color: #f59e0b;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.current-stock {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
}

.image-upload-wrapper {
  width: 100%;
}

.drone-image-upload {
  :deep(.el-upload) {
    width: 200px;
    height: 130px;
    border-radius: 12px;
    overflow: hidden;
  }
}

.image-preview {
  position: relative;
  width: 200px;
  height: 130px;
  cursor: pointer;

  .el-image {
    width: 100%;
    height: 100%;
  }

  .image-actions {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    background: rgba(0, 0, 0, 0.08);
    opacity: 0;
    transition: opacity 0.3s;
    color: #fff;
    font-size: 13px;

    .el-icon {
      font-size: 24px;
    }
  }

  &:hover .image-actions {
    opacity: 1;
  }
}

.upload-placeholder {
  width: 200px;
  height: 130px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f1f5f9;
  border: 2px dashed rgba(148, 163, 184, 0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: #64748b;

  &:hover {
    border-color: #3b82f6;
    color: #3b82f6;
  }

  .upload-icon {
    font-size: 32px;
  }
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
}
</style>
