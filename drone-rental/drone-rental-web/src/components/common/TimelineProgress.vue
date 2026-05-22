<template>
  <div class="timeline-progress">
    <div
      v-for="(item, index) in items"
      :key="index"
      class="timeline-item"
      :class="{ 'timeline-item--active': item.active, 'timeline-item--completed': item.completed }"
    >
      <div class="timeline-item__indicator">
        <div class="timeline-item__node">
          <el-icon v-if="item.completed"><Check /></el-icon>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <div v-if="index < items.length - 1" class="timeline-item__line"></div>
      </div>
      <div class="timeline-item__content">
        <div class="timeline-item__title">{{ item.title }}</div>
        <div v-if="item.description" class="timeline-item__description">{{ item.description }}</div>
        <div v-if="item.time" class="timeline-item__time">{{ item.time }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'

defineProps({
  items: {
    type: Array,
    required: true,
    // 每项包含: { title, description?, time?, active?, completed? }
  }
})
</script>

<style lang="scss" scoped>
.timeline-progress {
  padding: 8px 0;
}

.timeline-item {
  display: flex;
  gap: 16px;
  position: relative;

  &__indicator {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex-shrink: 0;
  }

  &__node {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    background: #f1f5f9;
    color: #94a3b8;
    border: 2px solid #e2e8f0;
    transition: all 0.3s ease;
    z-index: 1;
  }

  &__line {
    width: 2px;
    flex: 1;
    min-height: 40px;
    background: #e2e8f0;
    margin: 8px 0;
  }

  &__content {
    flex: 1;
    padding-bottom: 24px;
  }

  &__title {
    font-size: 15px;
    font-weight: 600;
    color: #64748b;
    margin-bottom: 4px;
  }

  &__description {
    font-size: 14px;
    color: #94a3b8;
    margin-bottom: 4px;
    line-height: 1.5;
  }

  &__time {
    font-size: 12px;
    color: #94a3b8;
  }

  // 激活状态
  &--active {
    .timeline-item__node {
      background: rgba(59, 130, 246, 0.2);
      color: #3b82f6;
      border-color: #3b82f6;
      box-shadow: 0 0 12px rgba(59, 130, 246, 0.4);
      animation: pulse 2s infinite;
    }

    .timeline-item__title {
      color: #0f172a;
    }

    .timeline-item__description {
      color: #94a3b8;
    }
  }

  // 完成状态
  &--completed {
    .timeline-item__node {
      background: #22c55e;
      color: white;
      border-color: #22c55e;
      box-shadow: 0 0 12px rgba(34, 197, 94, 0.4);
    }

    .timeline-item__line {
      background: linear-gradient(to bottom, #22c55e, #e2e8f0);
    }

    .timeline-item__title {
      color: #4ade80;
    }
  }

  &:last-child {
    .timeline-item__content {
      padding-bottom: 0;
    }
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 12px rgba(59, 130, 246, 0.4);
  }
  50% {
    box-shadow: 0 0 20px rgba(59, 130, 246, 0.6);
  }
}
</style>
