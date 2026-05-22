<template>
  <div class="page-header">
    <div class="page-header__main">
      <div class="page-header__breadcrumb" v-if="breadcrumbs?.length">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path">
            {{ item.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <h1 class="page-header__title">{{ title }}</h1>
      <p v-if="subtitle" class="page-header__subtitle">{{ subtitle }}</p>
    </div>
    <div class="page-header__actions" v-if="$slots.actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  breadcrumbs: {
    type: Array,
    default: () => []
  }
})
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e2e8f0;

  &__main {
    flex: 1;
  }

  &__breadcrumb {
    margin-bottom: 8px;

    :deep(.el-breadcrumb) {
      font-size: 13px;

      .el-breadcrumb__inner {
        color: #94a3b8;

        &.is-link:hover {
          color: #3b82f6;
        }
      }

      .el-breadcrumb__separator {
        color: #cbd5e1;
      }

      .el-breadcrumb__item:last-child .el-breadcrumb__inner {
        color: #0f172a;
      }
    }
  }

  &__title {
    font-size: 28px;
    font-weight: 700;
    color: #0f172a;
    margin: 0;
    line-height: 1.3;
  }

  &__subtitle {
    font-size: 15px;
    color: #64748b;
    margin: 8px 0 0;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;

    &__title {
      font-size: 22px;
    }

    &__actions {
      width: 100%;
      justify-content: flex-start;
    }
  }
}
</style>
