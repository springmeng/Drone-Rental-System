<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 初始化认证状态
onMounted(async () => {
  await authStore.initAuth()
})
</script>

<style lang="scss">
// 全局样式已在main.js中引入
</style>
