<template>
  <div class="bento-grid" :style="gridStyle">
    <slot></slot>
  </div>
</template>

<script setup>
const props = defineProps({
  cols: {
    type: [Number, Object],
    default: () => ({ sm: 1, md: 2, lg: 3, xl: 4 })
  },
  gap: {
    type: [String, Number],
    default: 24
  }
})

const gridStyle = computed(() => {
  const gapValue = typeof props.gap === 'number' ? `${props.gap}px` : props.gap

  if (typeof props.cols === 'number') {
    return {
      display: 'grid',
      gridTemplateColumns: `repeat(${props.cols}, 1fr)`,
      gap: gapValue
    }
  }

  return {
    display: 'grid',
    gap: gapValue,
    gridTemplateColumns: 'repeat(1, 1fr)'
  }
})
</script>

<style lang="scss" scoped>
.bento-grid {
  width: 100%;

  // 响应式断点
  @media (min-width: 640px) {
    grid-template-columns: repeat(v-bind('props.cols?.sm || 1'), 1fr);
  }

  @media (min-width: 768px) {
    grid-template-columns: repeat(v-bind('props.cols?.md || 2'), 1fr);
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(v-bind('props.cols?.lg || 3'), 1fr);
  }

  @media (min-width: 1280px) {
    grid-template-columns: repeat(v-bind('props.cols?.xl || 4'), 1fr);
  }
}

// 便当盒子项 - 可跨列/跨行
:deep(.bento-item--span-2) {
  grid-column: span 2;
}

:deep(.bento-item--span-3) {
  grid-column: span 3;
}

:deep(.bento-item--row-span-2) {
  grid-row: span 2;
}
</style>
