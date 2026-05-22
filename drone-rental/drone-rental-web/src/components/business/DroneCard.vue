<template>
  <div class="drone-card" @click="handleClick">
    <div class="drone-card__image">
      <img :src="imageUrl" :alt="drone.model" />
      <div class="drone-card__status">
        <StatusTag :text="statusText" :type="statusType" size="small" />
      </div>
    </div>
    <div class="drone-card__content">
      <div class="drone-card__header">
        <h3 class="drone-card__title">{{ drone.model }}</h3>
        <span class="drone-card__brand">{{ drone.brand }}</span>
      </div>
      <div class="drone-card__specs">
        <div class="drone-card__spec">
          <el-icon><Timer /></el-icon>
          <span>{{ drone.flightTime }}分钟</span>
        </div>
        <div class="drone-card__spec">
          <el-icon><Coin /></el-icon>
          <span>{{ drone.maxPayload }}kg</span>
        </div>
        <div class="drone-card__spec">
          <el-icon><Odometer /></el-icon>
          <span>{{ drone.maxSpeed }}km/h</span>
        </div>
      </div>
      <div class="drone-card__footer">
        <div class="drone-card__price">
          <span class="drone-card__price-value">¥{{ drone.pricePerDay }}</span>
          <span class="drone-card__price-unit">/天</span>
        </div>
        <div class="drone-card__stock">
          库存: <span :class="{ 'low': drone.stock < 3 }">{{ drone.stock }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Timer, Coin, Odometer } from '@element-plus/icons-vue'
import StatusTag from '@/components/common/StatusTag.vue'

const props = defineProps({
  drone: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const defaultImage = 'https://via.placeholder.com/300x200/1e293b/64748b?text=Drone'

// 获取图片完整URL
const imageUrl = computed(() => {
  const url = props.drone.image
  if (!url) return defaultImage
  if (url.startsWith('http')) return url
  return `/api${url}`
})

const statusText = computed(() => {
  const statusMap = {
    0: '缺货',
    1: '可租赁',
    2: '维护中'
  }
  return statusMap[props.drone.status] || '未知'
})

const statusType = computed(() => {
  const typeMap = {
    0: 'error',
    1: 'success',
    2: 'warning'
  }
  return typeMap[props.drone.status] || 'default'
})

const handleClick = () => {
  emit('click', props.drone)
}
</script>

<style lang="scss" scoped>
.drone-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);

  &:hover {
    transform: translateY(-6px);
    border-color: rgba(59, 130, 246, 0.3);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08), 0 0 20px rgba(59, 130, 246, 0.1);

    .drone-card__image img {
      transform: scale(1.05);
    }
  }

  &__image {
    position: relative;
    height: 180px;
    overflow: hidden;
    background: #f1f5f9;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
  }

  &__status {
    position: absolute;
    top: 12px;
    right: 12px;
  }

  &__content {
    padding: 20px;
  }

  &__header {
    margin-bottom: 12px;
  }

  &__title {
    font-size: 18px;
    font-weight: 700;
    color: #0f172a;
    margin: 0 0 4px;
  }

  &__brand {
    font-size: 13px;
    color: #64748b;
  }

  &__specs {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e2e8f0;
  }

  &__spec {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #64748b;

    .el-icon {
      color: #3b82f6;
    }
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__price {
    display: flex;
    align-items: baseline;
    gap: 2px;
  }

  &__price-value {
    font-size: 24px;
    font-weight: 700;
    color: #3b82f6;
  }

  &__price-unit {
    font-size: 13px;
    color: #64748b;
  }

  &__stock {
    font-size: 13px;
    color: #64748b;

    span {
      color: #4ade80;
      font-weight: 600;

      &.low {
        color: #f59e0b;
      }
    }
  }
}
</style>
