<template>
  <div class="stat-tile" :class="[`stat-tile--${variant}`, { 'stat-tile--clickable': clickable }]">
    <div class="stat-tile__icon" v-if="icon">
      <el-icon :size="28"><component :is="icon" /></el-icon>
    </div>
    <div class="stat-tile__content">
      <div class="stat-tile__value">
        <span class="stat-tile__number">{{ formattedValue }}</span>
        <span v-if="unit" class="stat-tile__unit">{{ unit }}</span>
      </div>
      <div class="stat-tile__label">{{ label }}</div>
      <div v-if="trendValue !== null" class="stat-tile__trend" :class="trendClass">
        <el-icon v-if="trendDirection === 'up'"><ArrowUp /></el-icon>
        <el-icon v-else-if="trendDirection === 'down'"><ArrowDown /></el-icon>
        <span>{{ trendValue }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  value: {
    type: [Number, String],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  icon: {
    type: Object,
    default: null
  },
  unit: {
    type: String,
    default: ''
  },
  variant: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'primary', 'success', 'warning', 'error'].includes(v)
  },
  trend: {
    type: [Number, Object],
    default: undefined
  },
  clickable: {
    type: Boolean,
    default: false
  }
})

const formattedValue = computed(() => {
  if (typeof props.value === 'number') {
    return props.value.toLocaleString()
  }
  return props.value
})

// 处理 trend，支持对象格式 { value, direction } 和数字格式
const trendValue = computed(() => {
  if (props.trend === undefined || props.trend === null) return null
  if (typeof props.trend === 'object') {
    return props.trend.value ?? null
  }
  return Math.abs(props.trend)
})

const trendDirection = computed(() => {
  if (props.trend === undefined || props.trend === null) return null
  if (typeof props.trend === 'object') {
    return props.trend.direction || (props.trend.value > 0 ? 'up' : props.trend.value < 0 ? 'down' : null)
  }
  return props.trend > 0 ? 'up' : props.trend < 0 ? 'down' : null
})

const trendClass = computed(() => {
  if (trendDirection.value === 'up') return 'stat-tile__trend--up'
  if (trendDirection.value === 'down') return 'stat-tile__trend--down'
  return ''
})
</script>

<style lang="scss" scoped>
.stat-tile {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  transition: all 0.3s ease;

  &--clickable {
    cursor: pointer;

    &:hover {
      transform: translateY(-2px);
      border-color: rgba(59, 130, 246, 0.3);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    }
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 16px;
    background: rgba(59, 130, 246, 0.15);
    color: #3b82f6;
    flex-shrink: 0;
  }

  &--primary .stat-tile__icon {
    background: rgba(59, 130, 246, 0.15);
    color: #3b82f6;
  }

  &--success .stat-tile__icon {
    background: rgba(34, 197, 94, 0.15);
    color: #22c55e;
  }

  &--warning .stat-tile__icon {
    background: rgba(245, 158, 11, 0.15);
    color: #f59e0b;
  }

  &--error .stat-tile__icon {
    background: rgba(239, 68, 68, 0.15);
    color: #ef4444;
  }

  &__content {
    flex: 1;
    min-width: 0;
  }

  &__value {
    display: flex;
    align-items: baseline;
    gap: 4px;
    margin-bottom: 4px;
  }

  &__number {
    font-size: 32px;
    font-weight: 700;
    color: #0f172a;
    line-height: 1.2;
  }

  &__unit {
    font-size: 16px;
    color: #64748b;
    font-weight: 500;
  }

  &__label {
    font-size: 14px;
    color: #64748b;
    font-weight: 500;
  }

  &__trend {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    margin-top: 8px;
    padding: 4px 8px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;

    &--up {
      background: rgba(34, 197, 94, 0.15);
      color: #22c55e;
    }

    &--down {
      background: rgba(239, 68, 68, 0.15);
      color: #ef4444;
    }
  }
}
</style>
