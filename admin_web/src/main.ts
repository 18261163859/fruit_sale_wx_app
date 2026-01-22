import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './styles/index.css'

// 手动引入 Element Plus 的 MessageBox 样式（按需引入时需要）
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/message/style/css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 初始化用户信息和主题
import { useUserStore } from './stores/user'
import { useAppStore } from './stores/app'

const userStore = useUserStore()
const appStore = useAppStore()

userStore.initUserInfo()
appStore.initTheme()

app.mount('#app')