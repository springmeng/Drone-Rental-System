<template>
  <div class="glass-card" :class="[sizeClass, { 'hoverable': hoverable, 'glow': glow }]">
    <div v-if="$slots.header || title" class="glass-card__header">
      <slot name="header">
        <h3 class="glass-card__title">{{ title }}</h3>
        <p v-if="subtitle" class="glass-card__subtitle">{{ subtitle }}</p>
      </slot>
      <div v-if="$slots.extra" class="glass-card__extra">
        <slot name="extra"></slot>
      </div>
    </div>
    <div class="glass-card__body" :style="{ padding: noPadding ? '0' : undefined }">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="glass-card__footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  subtitle: String,
  size: {
    type: String,
    default: 'default',
    validator: (v) => ['small', 'default', 'large'].includes(v)
  },
  hoverable: {
    type: Boolean,
    default: false
  },
  glow: {
    type: Boolean,
    default: false
  },
  noPadding: {
    type: Boolean,
    default: false
  }
})

const sizeClass = computed(() => {
  const props = getCurrentInstance().props
  return `glass-card--${props.size}`
})
</script>

<style lang="scss" scoped>
.glass-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 28px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.3s ease;

  &.hoverable {
    cursor: pointer;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
      border-color: rgba(59, 130, 246, 0.3);
    }
  }

  &.glow {
    box-shadow: 0 4px 14px rgba(59, 130, 246, 0.15), 0 1px 3px rgba(0, 0, 0, 0.06);
  }

  &--small {
    .glass-card__body {
      padding: 12px 16px;
    }
  }

  &--default {
    .glass-card__body {
      padding: 20px 24px;
    }
  }

  &--large {
    .glass-card__body {
      padding: 28px 32px;
    }
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    border-bottom: 1px solid #e2e8f0;
  }

  &__title {
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
    margin: 0;
  }

  &__subtitle {
    font-size: 14px;
    color: #64748b;
    margin: 4px 0 0;
  }

  &__extra {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__footer {
    padding: 16px 24px;
    border-top: 1px solid #e2e8f0;
    background: #f8fafc;
  }
}
</style>
