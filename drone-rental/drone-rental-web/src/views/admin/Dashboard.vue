<template>
  <div class="dashboard-page">
    <h1 class="page-title">控制台</h1>

    <!-- 统计卡片 -->
    <BentoGrid :cols="4" :gap="24" class="stat-grid">
      <StatTile
        v-for="stat in stats"
        :key="stat.label"
        :value="stat.value"
        :label="stat.label"
        :icon="stat.icon"
        :trend="stat.trend"
        :color="stat.color"
      />
    </BentoGrid>

    <div class="dashboard-content">
      <div class="dashboard-main">
        <!-- 收入趋势图 -->
        <GlassCard title="收入趋势" class="chart-card">
          <div class="chart-header">
            <el-radio-group v-model="chartPeriod" size="small" @change="fetchRevenueTrend">
              <el-radio-button value="week">本周</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
              <el-radio-button value="year">本年</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="revenueChartRef" class="chart-container"></div>
        </GlassCard>

        <!-- 订单趋势图 -->
        <GlassCard title="订单趋势" class="chart-card">
          <div ref="orderChartRef" class="chart-container"></div>
        </GlassCard>
      </div>

      <div class="dashboard-sidebar">
        <!-- 待办事项 -->
        <GlassCard title="待办事项" class="todo-card">
          <div class="todo-list">
            <div v-for="item in todoItems" :key="item.type" class="todo-item" @click="handleTodoClick(item)">
              <div class="todo-icon" :style="{ background: item.bgColor, color: item.color }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">{{ item.label }}</span>
                <span class="todo-count">{{ item.count }}</span>
              </div>
              <el-icon class="todo-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </GlassCard>

        <!-- 热门设备 -->
        <GlassCard title="热门设备" class="popular-card">
          <div class="popular-list">
            <div v-for="(drone, index) in popularDrones" :key="drone.id" class="popular-item">
              <span class="popular-rank" :class="`rank-${index + 1}`">{{ index + 1 }}</span>
              <div class="popular-info">
                <span class="popular-name">{{ drone.model }}</span>
                <span class="popular-brand">{{ drone.brand }}</span>
              </div>
              <span class="popular-count">{{ drone.rentCount }}次</span>
            </div>
          </div>
        </GlassCard>

        <!-- 最新订单 -->
        <GlassCard title="最新订单" class="recent-card">
          <div class="recent-list">
            <div v-for="order in recentOrders" :key="order.id" class="recent-item">
              <div class="recent-info">
                <span class="recent-no">{{ order.orderNo }}</span>
                <span class="recent-user">{{ order.userName }}</span>
              </div>
              <div class="recent-meta">
                <StatusTag :text="getOrderStatusText(order.status)" :type="getOrderStatusType(order.status)" size="small" />
                <span class="recent-amount">¥{{ order.totalAmount }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <router-link to="/admin/orders">查看全部</router-link>
          </div>
        </GlassCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  Money,
  ShoppingCart,
  User,
  Goods,
  Document,
  CircleCheck,
  SetUp,
  ArrowRight
} from '@element-plus/icons-vue'
import GlassCard from '@/components/common/GlassCard.vue'
import BentoGrid from '@/components/common/BentoGrid.vue'
import StatTile from '@/components/common/StatTile.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import {
  getDashboardStats,
  getRevenueTrend,
  getOrderTrend,
  getPopularDrones,
  getRecentOrders,
  getTodoItems
} from '@/api/admin'

const router = useRouter()

const chartPeriod = ref('week')
const revenueChartRef = ref(null)
const orderChartRef = ref(null)
let revenueChart = null
let orderChart = null

// 统计数据
const stats = ref([
  { value: '¥0', label: '今日收入', icon: markRaw(Money), color: 'primary', trend: null },
  { value: '0', label: '今日订单', icon: markRaw(ShoppingCart), color: 'success', trend: null },
  { value: '0', label: '总用户数', icon: markRaw(User), color: 'info' },
  { value: '0', label: '设备总数', icon: markRaw(Goods), color: 'warning' }
])

// 待办事项
const todoItems = ref([
  { type: 'audit', label: '待审核', count: 0, icon: markRaw(CircleCheck), color: '#F59E0B', bgColor: 'rgba(245, 158, 11, 0.15)', path: '/admin/audits' },
  { type: 'ship', label: '待发货', count: 0, icon: markRaw(ShoppingCart), color: '#3B82F6', bgColor: 'rgba(59, 130, 246, 0.15)', path: '/admin/orders?status=1' },
  { type: 'return', label: '待确认退租', count: 0, icon: markRaw(Document), color: '#22C55E', bgColor: 'rgba(34, 197, 94, 0.15)', path: '/admin/orders?status=4' },
  { type: 'maintenance', label: '待处理维保', count: 0, icon: markRaw(SetUp), color: '#EF4444', bgColor: 'rgba(239, 68, 68, 0.15)', path: '/admin/maintenance?status=0' }
])

const popularDrones = ref([])
const recentOrders = ref([])

const getOrderStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '租赁中', 4: '待归还', 5: '已完成', 6: '已取消' }
  return map[status] || '未知'
}

const getOrderStatusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'info', 3: 'primary', 4: 'warning', 5: 'success', 6: 'default' }
  return map[status] || 'default'
}

// 获取统计数据
const fetchDashboardStats = async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data || {}
    stats.value[0].value = `¥${data.todayRevenue || 0}`
    stats.value[1].value = String(data.todayOrders || 0)
    stats.value[2].value = String(data.totalUsers || 0)
    stats.value[3].value = String(data.totalDrones || 0)

    // 设置环比趋势（只在有数据时显示）
    if (data.revenueTrend !== undefined && data.revenueTrend !== 0) {
      stats.value[0].trend = {
        value: Math.abs(data.revenueTrend),
        direction: data.revenueTrend >= 0 ? 'up' : 'down'
      }
    }
    if (data.orderTrend !== undefined && data.orderTrend !== 0) {
      stats.value[1].trend = {
        value: Math.abs(data.orderTrend),
        direction: data.orderTrend >= 0 ? 'up' : 'down'
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取收入趋势
const fetchRevenueTrend = async () => {
  try {
    const res = await getRevenueTrend({ period: chartPeriod.value })
    const data = res.data || { dates: [], values: [] }
    initRevenueChart(data)
  } catch (error) {
    console.error('获取收入趋势失败:', error)
    initRevenueChart({ dates: [], values: [] })
  }
}

// 获取订单趋势
const fetchOrderTrend = async () => {
  try {
    const res = await getOrderTrend({ period: 'week' })
    const data = res.data || { dates: [], values: [] }
    initOrderChart(data)
  } catch (error) {
    console.error('获取订单趋势失败:', error)
    initOrderChart({ dates: [], values: [] })
  }
}

// 获取待办事项
const fetchTodoItems = async () => {
  try {
    const res = await getTodoItems()
    const data = res.data || {}
    todoItems.value[0].count = data.pendingAudits || 0
    todoItems.value[1].count = data.pendingShip || 0
    todoItems.value[2].count = data.pendingReturn || 0
    todoItems.value[3].count = data.pendingMaintenance || 0
  } catch (error) {
    console.error('获取待办事项失败:', error)
  }
}

// 获取热门设备
const fetchPopularDrones = async () => {
  try {
    const res = await getPopularDrones(5)
    popularDrones.value = res.data || []
  } catch (error) {
    console.error('获取热门设备失败:', error)
  }
}

// 获取最新订单
const fetchRecentOrders = async () => {
  try {
    const res = await getRecentOrders(5)
    recentOrders.value = res.data || []
  } catch (error) {
    console.error('获取最新订单失败:', error)
  }
}

// 初始化收入趋势图
const initRevenueChart = (data) => {
  if (!revenueChartRef.value) return

  if (!revenueChart) {
    revenueChart = echarts.init(revenueChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#ffffff',
      borderColor: '#e2e8f0',
      textStyle: { color: '#0f172a' }
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.dates || [],
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    series: [{
      data: data.values || [],
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#3b82f6', width: 3 },
      itemStyle: { color: '#3b82f6' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
          { offset: 1, color: 'rgba(59, 130, 246, 0)' }
        ])
      }
    }]
  }

  revenueChart.setOption(option)
}

// 初始化订单趋势图
const initOrderChart = (data) => {
  if (!orderChartRef.value) return

  if (!orderChart) {
    orderChart = echarts.init(orderChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#ffffff',
      borderColor: '#e2e8f0',
      textStyle: { color: '#0f172a' }
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.dates || [],
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    series: [{
      data: data.values || [],
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#22c55e' },
          { offset: 1, color: 'rgba(34, 197, 94, 0.3)' }
        ]),
        borderRadius: [6, 6, 0, 0]
      }
    }]
  }

  orderChart.setOption(option)
}

const handleTodoClick = (item) => {
  router.push(item.path)
}

onMounted(() => {
  fetchDashboardStats()
  fetchRevenueTrend()
  fetchOrderTrend()
  fetchTodoItems()
  fetchPopularDrones()
  fetchRecentOrders()

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    revenueChart?.resize()
    orderChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100%;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 24px;
}

.stat-grid {
  margin-bottom: 24px;
}

.dashboard-content {
  display: flex;
  gap: 24px;
}

.dashboard-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-sidebar {
  width: 360px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// 图表卡片
.chart-card {
  .chart-header {
    margin-bottom: 16px;
  }

  .chart-container {
    height: 280px;
  }
}

// 待办事项
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.95);
  }
}

.todo-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.todo-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-label {
  font-size: 14px;
  color: #64748b;
}

.todo-count {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.todo-arrow {
  color: #475569;
}

// 热门设备
.popular-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.popular-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.popular-rank {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  background: rgba(100, 116, 139, 0.2);
  color: #64748b;

  &.rank-1 {
    background: rgba(245, 158, 11, 0.2);
    color: #f59e0b;
  }

  &.rank-2 {
    background: rgba(148, 163, 184, 0.2);
    color: #64748b;
  }

  &.rank-3 {
    background: rgba(180, 83, 9, 0.2);
    color: #d97706;
  }
}

.popular-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.popular-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.popular-brand {
  font-size: 12px;
  color: #64748b;
}

.popular-count {
  font-size: 14px;
  color: #3b82f6;
  font-weight: 600;
}

// 最新订单
.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e2e8f0;

  &:last-child {
    border-bottom: none;
  }
}

.recent-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.recent-no {
  font-size: 13px;
  color: #64748b;
}

.recent-user {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.recent-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.recent-amount {
  font-size: 14px;
  font-weight: 600;
  color: #3b82f6;
}

.card-footer {
  margin-top: 16px;
  text-align: center;

  a {
    font-size: 14px;
    color: #3b82f6;
    text-decoration: none;

    &:hover {
      color: #60a5fa;
    }
  }
}

// 响应式
@media (max-width: 1280px) {
  .dashboard-content {
    flex-direction: column;
  }

  .dashboard-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;

    > * {
      flex: 1;
      min-width: 300px;
    }
  }
}
</style>
