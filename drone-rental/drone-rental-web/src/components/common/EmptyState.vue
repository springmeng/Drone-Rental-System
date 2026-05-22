<template>
  <div class="empty-state">
    <div class="empty-state__icon">
      <el-icon :size="64"><component :is="icon" /></el-icon>
    </div>
    <h3 class="empty-state__title">{{ title }}</h3>
    <p v-if="description" class="empty-state__description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-state__action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script setup>
import { Folder } from '@element-plus/icons-vue'

defineProps({
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    default: () => Folder
  }
})
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;

  &__icon {
    color: #cbd5e1;
    margin-bottom: 16px;
    opacity: 0.6;
  }

  &__title {
    font-size: 18px;
    font-weight: 600;
    color: #64748b;
    margin: 0 0 8px;
  }

  &__description {
    font-size: 14px;
    color: #64748b;
    margin: 0 0 24px;
    max-width: 300px;
  }

  &__action {
    margin-top: 8px;
  }
}
</style>
