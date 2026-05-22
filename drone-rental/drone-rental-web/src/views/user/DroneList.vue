<template>
  <div class="drone-list-page">
    <PageHeader title="设备浏览" subtitle="选择适合您需求的无人机设备" />

    <!-- 筛选区域 -->
    <GlassCard class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>品牌</label>
          <el-select v-model="filters.brand" placeholder="全部品牌" clearable @change="handleSearch">
            <el-option
              v-for="brand in brandOptions"
              :key="brand"
              :label="brand"
              :value="brand"
            />
          </el-select>
        </div>
        <div class="filter-item">
          <label>类型</label>
          <el-select v-model="filters.type" placeholder="全部类型" clearable @change="handleSearch">
            <el-option
              v-for="type in typeOptions"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </div>
        <div class="filter-item">
          <label>价格区间</label>
          <el-select v-model="filters.priceRange" placeholder="不限" clearable @change="handleSearch">
            <el-option label="100元以下/天" value="0-100" />
            <el-option label="100-300元/天" value="100-300" />
            <el-option label="300-500元/天" value="300-500" />
            <el-option label="500元以上/天" value="500-" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>状态</label>
          <el-select v-model="filters.status" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="可租赁" :value="1" />
            <el-option label="缺货" :value="0" />
          </el-select>
        </div>
        <div class="filter-item filter-item--search">
          <label>搜索</label>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索设备名称"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
        </div>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </GlassCard>

    <!-- 排序区域 -->
    <div class="sort-bar">
      <div class="sort-options">
        <span
          v-for="option in sortOptions"
          :key="option.value"
          class="sort-option"
          :class="{ 'sort-option--active': sortBy === option.value }"
          @click="handleSort(option.value)"
        >
          {{ option.label }}
          <el-icon v-if="sortBy === option.value">
            <SortUp v-if="sortOrder === 'asc'" />
            <SortDown v-else />
          </el-icon>
        </span>
      </div>
      <div class="result-count">
        共 <span>{{ total }}</span> 个设备
      </div>
    </div>

    <!-- 设备列表 -->
    <div v-loading="loading" class="drone-grid">
      <BentoGrid v-if="droneList.length > 0" :cols="4" :gap="24">
        <DroneCard
          v-for="drone in droneList"
          :key="drone.id"
          :drone="drone"
          @click="handleDroneClick"
        />
      </BentoGrid>
      <EmptyState
        v-else-if="!loading"
        title="暂无符合条件的设备"
        description="请尝试调整筛选条件"
        :icon="Goods"
      >
        <template #action>
          <el-button type="primary" @click="handleReset">重置筛选</el-button>
        </template>
      </EmptyState>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[8, 12, 16, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, SortUp, SortDown, Goods } from '@element-plus/icons-vue'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import BentoGrid from '@/components/common/BentoGrid.vue'
import DroneCard from '@/components/business/DroneCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getDroneList, getDroneBrands } from '@/api/drone'

const router = useRouter()

const loading = ref(false)
const droneList = ref([])
const total = ref(0)
const brandOptions = ref([])
// 固定类型选项（航拍/测绘/农业/巡检）
const typeOptions = ref(['航拍', '测绘', '农业', '巡检'])

const filters = reactive({
  brand: '',
  type: '',
  priceRange: '',
  status: null,
  keyword: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 12
})

const sortBy = ref('createTime')
const sortOrder = ref('desc')

const sortOptions = [
  { label: '最新上架', value: 'createTime' },
  { label: '价格', value: 'pricePerDay' },
  { label: '销量', value: 'rentCount' }
]

// 获取设备列表
const fetchDroneList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value,
      ...filters
    }

    // 处理价格区间
    if (filters.priceRange) {
      const [min, max] = filters.priceRange.split('-')
      if (min) params.minPrice = min
      if (max) params.maxPrice = max
    }

    const res = await getDroneList(params)
    droneList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取筛选选项（只获取品牌，类型使用固定值）
const fetchFilterOptions = async () => {
  try {
    const brandsRes = await getDroneBrands()
    brandOptions.value = brandsRes.data || []
  } catch (error) {
    console.error('获取筛选选项失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchDroneList()
}

const handleReset = () => {
  Object.assign(filters, {
    brand: '',
    type: '',
    priceRange: '',
    status: null,
    keyword: ''
  })
  pagination.page = 1
  sortBy.value = 'createTime'
  sortOrder.value = 'desc'
  fetchDroneList()
}

const handleSort = (field) => {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'desc'
  }
  fetchDroneList()
}

const handlePageChange = () => {
  fetchDroneList()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchDroneList()
}

const handleDroneClick = (drone) => {
  router.push(`/drones/${drone.id}`)
}

onMounted(() => {
  fetchDroneList()
  fetchFilterOptions()
})
</script>

<style lang="scss" scoped>
.drone-list-page {
  min-height: 100%;
}

// 筛选卡片
.filter-card {
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-size: 13px;
    color: #64748b;
  }

  .el-select,
  .el-input {
    width: 160px;
  }

  &--search {
    .el-input {
      width: 200px;
    }
  }
}

// 排序栏
.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
}

.sort-options {
  display: flex;
  gap: 8px;
}

.sort-option {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    color: #0f172a;
    background: #e2e8f0;
  }

  &--active {
    color: #3b82f6;
    background: rgba(59, 130, 246, 0.1);
  }
}

.result-count {
  font-size: 14px;
  color: #64748b;

  span {
    color: #3b82f6;
    font-weight: 600;
  }
}

// 设备列表
.drone-grid {
  min-height: 400px;
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: 24px 0;
}

// 响应式
@media (max-width: 1024px) {
  .filter-item {
    .el-select,
    .el-input {
      width: 140px;
    }
  }
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    align-items: stretch;

    .filter-item {
      width: 100%;

      .el-select,
      .el-input {
        width: 100%;
      }
    }

    .el-button {
      width: 100%;
    }
  }

  .sort-bar {
    flex-direction: column;
    gap: 12px;
  }

  .sort-options {
    width: 100%;
    justify-content: center;
  }
}
</style>
