import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import pinia from './stores'

// 样式
import 'element-plus/dist/index.css'
import '@/assets/styles/variables.scss'
import '@/assets/styles/global.scss'
import '@/assets/styles/element.scss'

// 创建应用
const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 挂载应用
app.mount('#app')
