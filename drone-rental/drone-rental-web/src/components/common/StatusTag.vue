<template>
  <span class="status-tag" :class="[`status-tag--${type}`, `status-tag--${size}`]">
    <span v-if="dot" class="status-tag__dot"></span>
    <span class="status-tag__text">{{ text }}</span>
  </span>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'primary', 'success', 'warning', 'error', 'info'].includes(v)
  },
  size: {
    type: String,
    default: 'default',
    validator: (v) => ['small', 'default', 'large'].includes(v)
  },
  dot: {
    type: Boolean,
    default: true
  }
})
</script>

<style lang="scss" scoped>
.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 600;
  white-space: nowrap;

  // 尺寸
  &--small {
    padding: 4px 10px;
    font-size: 12px;

    .status-tag__dot {
      width: 6px;
      height: 6px;
    }
  }

  &--default {
    font-size: 13px;
  }

  &--large {
    padding: 8px 16px;
    font-size: 14px;

    .status-tag__dot {
      width: 10px;
      height: 10px;
    }
  }

  &__dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    animation: pulse 2s infinite;
  }

  // 默认/中性
  &--default {
    background: rgba(100, 116, 139, 0.2);
    color: #64748b;

    .status-tag__dot {
      background: #64748b;
      box-shadow: 0 0 8px rgba(100, 116, 139, 0.5);
    }
  }

  // 主色
  &--primary {
    background: rgba(59, 130, 246, 0.15);
    color: #2563eb;

    .status-tag__dot {
      background: #3b82f6;
      box-shadow: 0 0 8px rgba(59, 130, 246, 0.5);
    }
  }

  // 成功 - Neon Green
  &--success {
    background: rgba(34, 197, 94, 0.15);
    color: #16a34a;

    .status-tag__dot {
      background: #22c55e;
      box-shadow: 0 0 8px rgba(34, 197, 94, 0.5);
    }
  }

  // 警告 - Neon Amber
  &--warning {
    background: rgba(245, 158, 11, 0.15);
    color: #d97706;

    .status-tag__dot {
      background: #f59e0b;
      box-shadow: 0 0 8px rgba(245, 158, 11, 0.5);
    }
  }

  // 错误 - Neon Red
  &--error {
    background: rgba(239, 68, 68, 0.15);
    color: #dc2626;

    .status-tag__dot {
      background: #ef4444;
      box-shadow: 0 0 8px rgba(239, 68, 68, 0.5);
    }
  }

  // 信息
  &--info {
    background: rgba(14, 165, 233, 0.15);
    color: #0284c7;

    .status-tag__dot {
      background: #0ea5e9;
      box-shadow: 0 0 8px rgba(14, 165, 233, 0.5);
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
